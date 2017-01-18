package com.jimlemmers.scenicrouteamsterdam;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status>{

    private String TAG = "MapsActivity";
    private GoogleMap mMap;
    private Route route;
    private boolean cycling;
    private boolean preview;
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<Geofence> mGeofenceList = new ArrayList<>();
    private Route test_route;

    // Location and Geofencing related variables.
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }
    private String testRouteJSON = Constants.TEST_ROUTE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        String from = intent.getExtras().getString("from");
        String to = intent.getExtras().getString("to");
        cycling = intent.getExtras().getBoolean("cycling", true);
        //test_route = new Route(from, to, cycling);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //TODO remove the testing stuff.
        try {
            route = new Route(new JSONObject(testRouteJSON));
            drawRoute(route);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public void acceptRoute(View view){
        toggleMapButtons();
        //TODO start the navigation part and do other stuff.
    }

    public void toggleMapButtons() {
        Button startRoute = (Button) findViewById(R.id.start_route);
        Button regenerateRoute = (Button) findViewById(R.id.regenerate_route);
        if (this.preview) {
            startRoute.setVisibility(View.INVISIBLE);
            regenerateRoute.setVisibility(View.INVISIBLE);
            this.preview = false;
        }
        else {
            startRoute.setVisibility(View.VISIBLE);
            regenerateRoute.setVisibility(View.VISIBLE);
            this.preview = true;
        }
    }

    public void drawRoute(Route route) {
        if (mMap == null) {
            return;
        }
        if (route.points.size() < 2) {
            return;
        }
        PolylineOptions options = new PolylineOptions();

        options.color(Color.parseColor("#CC2222DD"));
        options.width(5);
        options.visible(true);

        for (int i = 0; i < route.points.size(); i++) {
            POI point = (POI) route.points.get(i);
            options.add(point.location);
            if (point.uri != null | point.name != "") {
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(point.location)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.poi_marker))
                        .anchor(0.5f, 0.5f)
                        .title("Test marker" + String.valueOf(i)));
                        //TODO set the name of the POI in the information-box.
                marker.setTag(point);
                createGeofence(point);
            }
        }
        mMap.addPolyline(options);
    }

    public void createGeofence(POI point) {
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(point.name)
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
        if (poi.uri != "") {
            intent.putExtra("url", poi.uri);
        }
        else {
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
     *
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
}