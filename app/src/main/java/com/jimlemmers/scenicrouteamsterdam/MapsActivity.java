package com.jimlemmers.scenicrouteamsterdam;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private Route route;
    private boolean cycling;
    private boolean preview;
    private WebView mWebView;
    private String testRouteJSON = "{'route':[{'lat': 52.3556845, 'lng': 4.9545822}," +
            " {'lat': 52.3559067, 'lng': 4.9549596}," +
            " {'lat': 52.3562829, 'lng': 4.9545162}," +
            " {'lat': 52.3544275, 'lng': 4.951395}," +
            " {'lat': 52.3443541, 'lng': 4.9345173}," +
            " {'lat': 52.3475559, 'lng': 4.9293173}," +
            " {'lat': 52.34595909999999, 'lng': 4.921158399999999}," +
            " {'lat': 52.3478944, 'lng': 4.919024299999999}," +
            " {'lat': 52.360085, 'lng': 4.9088033}," +
            " {'lat': 52.3611022, 'lng': 4.908108599999999}," +
            " {'lat': 52.36228879999999, 'lng': 4.9074203}," +
            " {'lat': 52.3662128, 'lng': 4.905046899999999}," +
            " {'lat': 52.3675905, 'lng': 4.904230099999999}," +
            " {'lat': 52.3683752, 'lng': 4.9040911}," +
            " {'lat': 52.3695367, 'lng': 4.9012271}," +
            " {'lat': 52.3720978, 'lng': 4.9004373}," +
            " {'lat': 52.3721442, 'lng': 4.8994889}," +
            " {'lat': 52.3699287, 'lng': 4.897421}," +
            " {'lat': 52.3702281, 'lng': 4.8958461}," +
            " {'lat': 52.3704123, 'lng': 4.8952648}]}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        preview = intent.getExtras().getBoolean("preview");
        Log.d("Maps Activity", String.valueOf(preview));

        if (!preview) {
            Button startRoute = (Button) findViewById(R.id.start_route);
            Button regenerateRoute = (Button) findViewById(R.id.regenerate_route);
            startRoute.setVisibility(View.INVISIBLE);
            regenerateRoute.setVisibility(View.INVISIBLE);
        }
        mWebView = new WebView(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);

        //
        //;
        try {
            route = new Route(new JSONObject(testRouteJSON));
            drawRoute(route);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mMap.setOnInfoWindowClickListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.350, 4.9), 12));

    }

    public void acceptRoute(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        //intent.putExtra("Route", route);
        intent.putExtra("preview", false);
        this.preview = false;
        Button startRoute = (Button) findViewById(R.id.start_route);
        Button regenerateRoute = (Button) findViewById(R.id.regenerate_route);
        startRoute.setVisibility(View.INVISIBLE);
        regenerateRoute.setVisibility(View.INVISIBLE);
    }

    public JSONObject getRoute(Location from, Location to, Boolean cycling){
        // TODO add the serer side stuff.
        String test_json = "";
        return new JSONObject();
    }

    public void getUserLocation(){ //Location getUserLocation(){
        //TODO make sure that the location is returned
    }

    /*
     * I got this from here: https://developers.google.com/maps/documentation/android-api/current-places-tutorial

    private void getDeviceLocation() {
    /*
     * Before getting the device location, you must check location
     * permission, as described earlier in the tutorial. Then:
     * Get the best and most recent location of the device, which may be
     * null in rare cases when a location is not available.
     * Also request regular updates about the device location.

        if (mLocationPermissionGranted) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }
        */

    public void drawRoute(Route route){
        if ( mMap == null){
            return;
        }
        if (route.points.size() < 2){
            return;
        }
        PolylineOptions options = new PolylineOptions();

        options.color(Color.parseColor("#CC2222DD"));
        options.width(5);
        options.visible(true);

        for (int i=0; i < route.points.size(); i++){
            POI point = (POI) route.points.get(i);
            options.add(point.location);
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(point.location)
                    //.alpha(0.5f)
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                    .title("Test marker" + String.valueOf(i)));
            marker.setTag(point);
        }
        mMap.addPolyline(options);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        viewPOI((POI) marker.getTag());
        Log.d("Mapsactivity", "clicked a marker");
    }

    public void viewPOI(POI poi){
        if (poi.uri != "") {
            mWebView.loadUrl(poi.uri);
        }
        else {
            String HTML = poi.generateHTML();
            Log.d("Maps", HTML);
            mWebView.loadData(HTML, "text/html", null);
        }
        setContentView(mWebView);
    }

    /*
     * This code was taken from this stackoverflow post:
     * http://stackoverflow.com/questions/6077141/how-to-go-back-to-previous-page-if-back-button-is-pressed-in-webview#6077173
     * It allows users to go back to the MapsActivity when pressing the back button.
     * If this is not implemented, the user will be thrown back to the MainActivity.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}