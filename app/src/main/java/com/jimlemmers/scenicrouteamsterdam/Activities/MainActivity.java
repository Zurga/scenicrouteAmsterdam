package com.jimlemmers.scenicrouteamsterdam.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jimlemmers.scenicrouteamsterdam.Adapters.RouteAdapter;
import com.jimlemmers.scenicrouteamsterdam.Async.RouteGetter;
import com.jimlemmers.scenicrouteamsterdam.Classes.Route;
import com.jimlemmers.scenicrouteamsterdam.Classes.ShowCase;
import com.jimlemmers.scenicrouteamsterdam.Interfaces.OnTaskCompleted;
import com.jimlemmers.scenicrouteamsterdam.Interfaces.RouteItemSelected;
import com.jimlemmers.scenicrouteamsterdam.R;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements
        OnConnectionFailedListener, RouteItemSelected, OnTaskCompleted {
    private static final String TAG = "Mainactivity";
    private String[] fromTo = {"", ""};
    private String[] fromToNames = {"Test", "Tester"};
    private RouteAdapter routeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firstTime = settings.getBoolean("firstTime", true);

        if (firstTime) {
            ShowCase.activateMainActivityShowcase(this);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("firstTime", false);
        }

        addAutocompleteListeners();

        Button previewButton = (Button) findViewById(R.id.preview_route_button);
        final Switch transportSwitch = (Switch) findViewById(R.id.transport_mode);

        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromTo[0] != "" & fromTo[1] != "") {
                    new RouteGetter(fromTo[0], fromTo[1], fromToNames[0], fromToNames[1],
                            transportSwitch.isChecked(), MainActivity.this);
                } else {
                    Toast errorToast = Toast.makeText(getApplicationContext(),
                            "Please enter a start and end location",
                            Toast.LENGTH_SHORT);
                    errorToast.show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference()
                .child("routes").child(user.getUid());
        routeAdapter = new RouteAdapter(this, new ArrayList<Route>(), myRef, this, this);
        ListView listView = (ListView) findViewById(R.id.my_routes);
        listView.setAdapter(routeAdapter);
    }


    public void deleteFromMostUsed(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference()
                .child("routes").child(user.getUid());
            for (String key: itemsSelected) {
                myRef.child(key).removeValue();
            }
        }
        itemsSelected.clear();
    }

    private void addAutocompleteListeners() {
        int[] address_fields = {R.id.place_autocomplete_fragment_from,
                R.id.place_autocomplete_fragment_to};

        for (int i=0; i < address_fields.length; i++) {
            final int idx = i;
            PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                    getFragmentManager().findFragmentById(address_fields[i]);
            autocompleteFragment.setBoundsBias(new LatLngBounds(
                    new LatLng(52.35679, 4.90952),
                    new LatLng(52.40472, 4.75811)
            ));

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    Log.i(TAG, "Place: " + place.getName());
                    LatLng location = place.getLatLng();
                    fromTo[idx] = String.valueOf(location.latitude) + "," +
                        String.valueOf(location.longitude);
                    fromToNames[idx] = place.getName().toString();
                }

                @Override
                public void onError(Status status) {
                    Log.i(TAG, "An error occurred: " + status);
                }
            });
        }
    }
}