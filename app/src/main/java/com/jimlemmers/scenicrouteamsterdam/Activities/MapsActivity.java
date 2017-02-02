package com.jimlemmers.scenicrouteamsterdam.Activities;

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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.jimlemmers.scenicrouteamsterdam.Models.POI;
import com.jimlemmers.scenicrouteamsterdam.Utils.PermissionUtils;
import com.jimlemmers.scenicrouteamsterdam.Models.Point;
import com.jimlemmers.scenicrouteamsterdam.R;
import com.jimlemmers.scenicrouteamsterdam.Models.Route;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This activity will display the map and the route to the user. The geofences are also created in
 * this activity. It receives a route to display from the calling activity.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
       {

    private String TAG = "MapsActivity";
    private GoogleMap mMap;
    private Route mRoute;
    private ArrayList<Route> routes = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    private Button saveRouteButton;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference mRef;

    // Location and Geofencing related variables.
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private boolean routeDrawn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        mRoute = Parcels.unwrap(intent.getParcelableExtra("route"));
        routes = Parcels.unwrap(intent.getParcelableExtra("routes"));
        mRef = database.getReference(intent.getStringExtra("reference")).child(user.getUid());

        saveRouteButton = (Button) findViewById(R.id.save_route);
        saveRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = database.getReference("my_routes")
                        .child(user.getUid());
                String key = reference.push().getKey();
                Map<String, Object> child = new HashMap<>();
                mRoute.key = key;
                child.put("/" + key, mRoute);
                reference.updateChildren(child);
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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        drawRoute();
        saveRouteButton.setVisibility(View.VISIBLE);
        mMap.setOnInfoWindowClickListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.350, 4.9), 12));
        enableMyLocation();
        saveOrUpdate();
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

    /**
     * Updates the mostUsed attribute of the route in firebase based on the key provided.
     * @param key Firebase object key.
     */
    public void updateRoute(String key){
        Log.e(TAG, "updating route" + key);
        mRoute.timesUsed += 1;
        mRef.child(key).setValue(mRoute);
    }

    /**
     * Decides whether to save the route to a new object or to update an existing one.
     */
    public void saveOrUpdate() {
        if (mRoute.key != null) {
            Log.d(TAG, mRoute.key);
            updateRoute(mRoute.key);
        } else {
            saveRoute(mRef);
        }
    }

    /**
     * Draws the route from the server on the map, creates the markers for all the points of interest
     * and adds geofences for these points of interest.
     */
    public void drawRoute(){
        if (mMap == null) {
            return;
        }
        if (mRoute.points.size() < 2) {
            Toast.makeText(this, "Could not show you the route, perhaps the server is down",
                    Toast.LENGTH_SHORT).show();
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
            marker.setTag(poi);
        }

        mMap.addPolyline(options);
    }

    /**
     * A function which allows me to create a custom onclick for the InfoWindow.
     * @param marker
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(this, InformationActivity.class);
        intent.putExtra("poi", Parcels.wrap(marker.getTag()));
        startActivity(intent);
    }

    /**
     * Enables the location by asking the user whether he or she would like to.
     * Taken from here: http://tinyurl.com/j2e5wkr
     */
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

    /**
     * Taken from here http://tinyurl.com/j2e5wkr
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
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

    /**
     * Called when the mGoogleApiClient is connected. This will draw the route on the map once.
     * If the mGoogleApiClient is not connected, the Geofences cannot be created.
     * @param bundle
     */
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
     * Saves the route, but checks first whether it already exists in the database
     * @param reference
     */
    public void saveRoute(DatabaseReference reference) {
        // Check if the route is already there and do an update instead.
        for (Route route: routes) {
            if (route.fromName.equals(mRoute.fromName) && route.toName.equals(mRoute.toName)) {
                updateRoute(route.key);
                return;
            }
        }
        String key = reference.push().getKey();
        Map<String, Object> child = new HashMap<>();
        mRoute.key = key;
        child.put("/" + key, mRoute);
        reference.updateChildren(child);
    }
}