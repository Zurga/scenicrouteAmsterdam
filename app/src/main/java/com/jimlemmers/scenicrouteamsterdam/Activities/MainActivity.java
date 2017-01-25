package com.jimlemmers.scenicrouteamsterdam.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.jimlemmers.scenicrouteamsterdam.Adapters.MostUsedAdapter;
import com.jimlemmers.scenicrouteamsterdam.R;
import com.jimlemmers.scenicrouteamsterdam.Async.ReadMostUsed;
import com.jimlemmers.scenicrouteamsterdam.Classes.Route;

import android.view.MenuItem;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements
        OnConnectionFailedListener {
    private static final String TAG = "Mainactivity";
    private GoogleApiClient mGoogleApiClient;
    private String[] fromTo = {"", ""};
    private String[] fromToNames = {"Test", "Tester"};
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Dialog dialog;
    private ArrayList<Route> mostUsed;
    private MostUsedAdapter mostUsedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        mostUsed = new ArrayList<>();
        mostUsedAdapter = new MostUsedAdapter(this, mostUsed);
        new ReadMostUsed(mostUsedAdapter);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        //TODO add address autocompletion to the text fields.
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        addAutocompleteListeners();

        Button previewButton = (Button) findViewById(R.id.preview_route_button);
        final Switch transportSwitch = (Switch) findViewById(R.id.transport_mode);

        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromTo[0] != "" & fromTo[1] != "") {
                    Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                    intent.putExtra("from", fromTo[0]);
                    intent.putExtra("fromName", fromToNames[0]);
                    intent.putExtra("to", fromTo[1]);
                    intent.putExtra("toName", fromToNames[1]);
                    intent.putExtra("preview", true);
                    intent.putExtra("cycling", transportSwitch.isChecked());
                    startActivity(intent);
                } else {
                    Toast errorToast = Toast.makeText(getApplicationContext(),
                            "Please enter a start and end location",
                            Toast.LENGTH_SHORT);
                    errorToast.show();
                }
            }
        });
        ListView listView = (ListView) findViewById(R.id.my_routes);
        listView.setAdapter(mostUsedAdapter);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.sign_in_dialog);
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.login_menu_item:
                setDialogListeners("login");
                return true;
            case R.id.create_account_menu_item:
                setDialogListeners("signup");
                return true;
            case R.id.user_routes_menu_item:
                Intent intent = new Intent(this, MyRoutesActivity.class);
                startActivity(intent);
                return true;
            case R.id.sign_out_menu_item:
                mAuth.signOut();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setDialogListeners(String action) {
        Button buttonOk = (Button) dialog.findViewById(R.id.button_ok);
        Button btnCancel = (Button) dialog.findViewById(R.id.button_cancel);
        final EditText email = (EditText) dialog.findViewById(R.id.txtUsername);
        final EditText password = (EditText) dialog.findViewById(R.id.txtPassword);

        if (action == "login") {
            dialog.setTitle("Log in with your email and password.");
            buttonOk.setText("Login");
            buttonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (email.getText().toString().trim().length() > 0 &&
                            password.getText().toString().trim().length() > 0) {
                        logUserIn(email.getText().toString(), password.getText().toString());
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Please enter email and password", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } else {
            buttonOk.setText("Sign up");
            dialog.setTitle("Create an account with your email and password.");
            buttonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (email.getText().toString().trim().length() > 0 &&
                            password.getText().toString().trim().length() > 0) {
                        createAccount(email.getText().toString(), password.getText().toString());
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Please enter email and password", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Make dialog box visible.
        dialog.show();
    }

    private void createAccount(String email, String password) {
        // Redirect to dashboard / home screen.
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Account created.",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
    }

    private void logUserIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logIn = menu.findItem(R.id.login_menu_item);
        MenuItem createAccount = menu.findItem(R.id.create_account_menu_item);
        MenuItem signOut = menu.findItem(R.id.sign_out_menu_item);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            logIn.setVisible(false);
            signOut.setVisible(true);
            createAccount.setVisible(false);
        }
        else {
            logIn.setVisible(true);
            signOut.setVisible(false);
            createAccount.setVisible(true);
        }
        return true;
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
                    // TODO: Get info about the selected place.
                    Log.i(TAG, "Place: " + place.getName());
                    LatLng location = place.getLatLng();
                    fromTo[idx] = String.valueOf(location.latitude) + "," +
                        String.valueOf(location.longitude);
                    fromToNames[idx] = place.getName().toString();
                }

                @Override
                public void onError(Status status) {
                    // TODO: Handle the error.
                    Log.i(TAG, "An error occurred: " + status);
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}