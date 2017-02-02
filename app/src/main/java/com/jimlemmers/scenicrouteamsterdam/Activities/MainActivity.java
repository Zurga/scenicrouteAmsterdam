package com.jimlemmers.scenicrouteamsterdam.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jimlemmers.scenicrouteamsterdam.Adapters.RouteAdapter;
import com.jimlemmers.scenicrouteamsterdam.Async.RouteGetter;
import com.jimlemmers.scenicrouteamsterdam.Classes.Constants;
import com.jimlemmers.scenicrouteamsterdam.Models.Route;
import com.jimlemmers.scenicrouteamsterdam.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Users can create a new route from here. It also displays a list of most used address pairs.
 */

public class MainActivity extends BaseActivity {
    private static final String TAG = "Mainactivity";
    private HashMap<Integer, Place> fromTo = new HashMap<>(); //This is used to avoid duplicate code.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        showCaseTitle = getString(R.string.MainActivityShowCaseTitle);
        showCaseText = getString(R.string.MainActivityShowCaseText);
        setupApis();

        fromTo.put(R.id.place_autocomplete_fragment_from, null);
        fromTo.put(R.id.place_autocomplete_fragment_to, null);
        firstTime = settings.getBoolean("firstTime", true);

        showCaseTarget = new ViewTarget(R.id.route_list, this);

        if (firstTime) {
            activateShowcase();
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("firstTime", false);
            editor.apply();
        }

        addAutocompleteListeners();

        Button previewButton = (Button) findViewById(R.id.preview_route_button);
        final Switch transportSwitch = (Switch) findViewById(R.id.transport_mode);

        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Place from = fromTo.get(R.id.place_autocomplete_fragment_from);
                Place to = fromTo.get(R.id.place_autocomplete_fragment_to);

                if (from != null && to != null) {
                    String fromLatLng = from.getLatLng().latitude + "," +
                            from.getLatLng().longitude;
                    String toLatLng = to.getLatLng().latitude + "," +
                            to.getLatLng().longitude;

                    new RouteGetter(fromLatLng, from.getName().toString(),
                            toLatLng, to.getName().toString(),
                            transportSwitch.isChecked(), MainActivity.this);
                    showProgressDialog();
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
        if (user != null) {
            DatabaseReference myRef = database.getReference(Constants.FIREBASE_ROUTE).child(getUid());
            adapter = new RouteAdapter(this, new ArrayList<Route>(), myRef, this, this);
            ListView listView = (ListView) findViewById(R.id.route_list);
            listView.setAdapter(adapter);
        } else {
            signInAnonymously();
        }
    }

    private void addAutocompleteListeners() {
        for (int i : fromTo.keySet()) {
            final int idx = i;
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setCountry("NL")
                    .build();
            PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                    getFragmentManager().findFragmentById(idx);
            autocompleteFragment.setBoundsBias(new LatLngBounds(
                    new LatLng(52.35679, 4.90952),
                    new LatLng(52.40472, 4.75811)
            ));
            autocompleteFragment.setFilter(typeFilter);
            // Set the text color inside the autocompleteFragment
            EditText search_input = (EditText) autocompleteFragment.getView()
                    .findViewById(R.id.place_autocomplete_search_input);
            search_input.setHintTextColor(0x000000);
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    fromTo.put(idx, place);
                    Log.d(TAG, String.valueOf(idx) + place.getName());
                }
                @Override
                public void onError(Status status) {
                    Log.i(TAG, "An error occurred: " + status);
                }
            });
        }
    }
}