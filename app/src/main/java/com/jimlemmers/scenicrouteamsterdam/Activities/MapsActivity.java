package com.jimlemmers.scenicrouteamsterdam.Activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jimlemmers.scenicrouteamsterdam.Classes.Constants;
import com.jimlemmers.scenicrouteamsterdam.Classes.GeofenceErrorMessages;
import com.jimlemmers.scenicrouteamsterdam.Interfaces.OnTaskCompleted;
import com.jimlemmers.scenicrouteamsterdam.Classes.POI;
import com.jimlemmers.scenicrouteamsterdam.Services.GeofenceTransitionsIntentService;
import com.jimlemmers.scenicrouteamsterdam.Utils.PermissionUtils;
import com.jimlemmers.scenicrouteamsterdam.Classes.Point;
import com.jimlemmers.scenicrouteamsterdam.R;
import com.jimlemmers.scenicrouteamsterdam.Classes.Route;
import com.jimlemmers.scenicrouteamsterdam.Async.RouteGetter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener, OnTaskCompleted,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    private String TAG = "MapsActivity";
    private GoogleMap mMap;
    private Route mRoute;
    private boolean cycling;
    private boolean preview;
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<Geofence> mGeofenceList = new ArrayList<>();
    private PendingIntent mGeofencePendingIntent;
    private String from;
    private String to;
    private String fromName;
    private String toName;
    private String routeKey;
    private String points;
    private int used;
    private Button saveRouteButton;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private boolean apiConnected = false;

    // Location and Geofencing related variables.
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGeofencePendingIntent = null;

        buildGoogleApiClient();

        Intent intent = getIntent();
        from = intent.getExtras().getString("from");
        fromName = intent.getExtras().getString("fromName");
        to = intent.getExtras().getString("to");
        toName = intent.getExtras().getString("toName");
        cycling = intent.getExtras().getBoolean("cycling", true);
        routeKey = intent.getExtras().getString("key");
        used = intent.getExtras().getInt("used");
        points = intent.getExtras().getString("points");
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();

        if (routeKey == null) {
            new RouteGetter(from, fromName, to, toName, cycling, this);
        } else if (points != null && routeKey != null) {
            new RouteGetter(from, fromName, to, toName, cycling, 0, routeKey, points, this);
        }
        else {
            DatabaseReference myReference = database.getReference()
                    .child("routes").child(user.getUid()).child(routeKey);
            myReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, dataSnapshot.toString());
                    Route route = dataSnapshot.getValue(Route.class);
                    new RouteGetter(route.from, route.fromName, route.to, route.toName,
                            route.cycling, route.timesUsed, routeKey, "", MapsActivity.this);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        saveRouteButton = (Button) findViewById(R.id.save_route);
        saveRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = database.getReference("my_routes")
                        .child(user.getUid());
                saveRoute(reference);
                Toast.makeText(MapsActivity.this, "Route saved!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //TODO remove the testing stuff.
        //mRoute = new Route(new JSONObject(testRouteJSON));
        mMap.setOnInfoWindowClickListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.350, 4.9), 12));
        enableMyLocation();

        if (mGeofenceList.size() > 0) {
            getGeofencingRequest();
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void updateRoute(){
        DatabaseReference myRef = database.getReference(Constants.FIREBASE_ROUTE).child(user.getUid());
        mRoute.timesUsed += 1;
        myRef.child(mRoute.key).setValue(mRoute);
    }

    public void onTaskCompleted(Route route) {
        mRoute = route;
        drawRoute();
        while (!apiConnected) {

        }
        saveRouteButton.setVisibility(View.VISIBLE);
        if (mGeofenceList.size() > 0 ) {
            try {
                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        getGeofencingRequest(),
                        getGeofencePendingIntent()
                ).setResultCallback(this);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, mRoute.key + routeKey);
        saveOrUpdate();
    }

    public void saveOrUpdate() {
        if (points != null) {
            if (mRoute.key != null && routeKey != null) {
                Log.d(TAG, mRoute.key + routeKey);
                mRoute.key = routeKey;
                updateRoute();
            } else {
                DatabaseReference reference = database.getReference("routes")
                        .child(user.getUid());
                saveRoute(reference);
            }
        }
    }

    public void drawRoute(){
        if (mMap == null) {
            return;
        }

        if (used != 0 ) {
            mRoute.timesUsed = used;
        }

        if (mRoute.points.size() < 2) {
            Log.d(TAG, "no points on the mRoute");
            return;
        }

        PolylineOptions options = new PolylineOptions();

        options.color(Color.parseColor("#CC2222DD"));
        options.width(5);
        options.visible(true);

        for (int i = 0; i < mRoute.points.size(); i++) {
            Point point = mRoute.points.get(i);
            LatLng location = new LatLng(point.location.latitude,
                    point.location.longitude);
            options.add(location);
        }
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.poi_marker);
        for (int i = 0; i < mRoute.pois.size(); i++) {
            POI poi = mRoute.pois.get(i);
            LatLng location = new LatLng(poi.location.latitude,
                    poi.location.longitude);
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.latitude, location.longitude))
                    //.icon(icon)
                    .anchor(0.5f, 0.5f)
                    .snippet(poi.name)
                    .title(poi.name));
            //TODO set the name of the POI in the information-box.
            marker.setTag(poi);
            createGeofence(poi);
        }

        mMap.addPolyline(options);
    }

    public void createGeofence(POI point) {
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(point.name != null ? point.name: "Test")
                .setCircularRegion(
                        point.location.latitude,
                        point.location.longitude,
                        Constants.GEOFENCE_RADIUS)
                .setExpirationDuration(Constants.GEOFENCE_TIMEOUT)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        viewPOI((POI) marker.getTag());
        Log.d(TAG, "clicked a marker");
    }

    public void viewPOI(POI poi) {
        Intent intent = new Intent(this, InformationActivity.class);
        if (poi.uri != null) {
            intent.putExtra("url", poi.uri);
        } else {
            String HTML = poi.generateHTML();
            Log.d(TAG, HTML);
            intent.putExtra("HTML", HTML);
        }
        startActivity(intent);
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        apiConnected = true;

        Log.i(TAG, "Connected to the Google API client.");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "The connection was suspended with the server");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: this is because of;" + connectionResult.getErrorMessage());
    }

    /**
     * Runs when the result of calling addGeofences() and removeGeofences() becomes available.
     * Either method can complete successfully or with an error.
     * <p>
     * Since this activity implements the {@link ResultCallback} interface, we are required to
     * define this method.
     *
     * @param status The Status returned through a PendingIntent when addGeofences() or
     *               removeGeofences() get called.
     */
    public void onResult(Status status) {
        if (status.isSuccess()) {
            Log.i(TAG, "Added all the geofences for the user");

        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void saveRoute(DatabaseReference reference) {
        String key = reference.push().getKey();
        Map<String, Object> child = new HashMap<>();
        mRoute.key = key;
        child.put("/" + key, mRoute);
        reference.updateChildren(child);
    }
}