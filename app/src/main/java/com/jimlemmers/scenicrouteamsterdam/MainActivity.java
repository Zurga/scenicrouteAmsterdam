package com.jimlemmers.scenicrouteamsterdam;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = "Mainactivity";
    private GoogleApiClient mGoogleApiClient;
    private String[] fromTo = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO add address autocompletion to the text fields.
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        addAutocompleteListeners();

        Button previewButton = (Button) findViewById(R.id.preview_route_button);
        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromTo.length == 2) {
                    Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                    intent.putExtra("from", fromTo[0]);
                    intent.putExtra("to", fromTo[1]);
                    intent.putExtra("preview", true);
                    startActivity(intent);
                }
                else {
                    Toast errorToast = Toast.makeText(getApplicationContext(),
                            "Please enter a start and end location",
                            Toast.LENGTH_SHORT);
                    errorToast.show();
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void addAutocompleteListeners() {
        int[] address_fields = {R.id.place_autocomplete_fragment_from,
                R.id.place_autocomplete_fragment_to};

        for (int i=0; i < address_fields.length; i++) {
            final int idx = i;
            PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                    getFragmentManager().findFragmentById(address_fields[i]);

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener()
            {
                @Override
                public void onPlaceSelected(Place place) {
                    // TODO: Get info about the selected place.
                    Log.i(TAG, "Place: " + place.getName());
                    fromTo[idx] = place.getLatLng().toString();
                }

                @Override
                public void onError(Status status) {
                    // TODO: Handle the error.
                    Log.i(TAG, "An error occurred: " + status);
                }
            });
        }
    }
}