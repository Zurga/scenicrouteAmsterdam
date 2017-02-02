package com.jimlemmers.scenicrouteamsterdam.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.jimlemmers.scenicrouteamsterdam.Adapters.RouteAdapter;
import com.jimlemmers.scenicrouteamsterdam.Models.Route;
import com.jimlemmers.scenicrouteamsterdam.Interfaces.RouteReceived;
import com.jimlemmers.scenicrouteamsterdam.Interfaces.RouteItemSelected;
import com.jimlemmers.scenicrouteamsterdam.R;

import org.parceler.Parcels;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class BaseActivity extends AppCompatActivity implements
        OnConnectionFailedListener, RouteItemSelected, RouteReceived {
    public String TAG = "BaseActivity";
    public GoogleApiClient mGoogleApiClient;
    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;
    public FirebaseUser user;
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public Dialog dialog;
    public ArrayList<String> itemsSelected = new ArrayList<>();
    public Toolbar toolbar;
    public boolean firstTime;
    public SharedPreferences settings;
    public RouteAdapter adapter;
    public Target showCaseTarget;
    public String showCaseText;
    public String showCaseTitle;
    public String mRefString;

    private ProgressDialog mProgressDialog;


    Button buttonOk;
    Button buttonCancel;
    EditText email;
    EditText password;

    /**
     * Taken from android base Activity
     */
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    /**
     * Taken from android base Activity
     */
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * Taken from android base Activity
     */
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * Sets up the Firebase authenticator, GoogleApiClient and the SharedPreferences.
     */
    public void setupApis() {
        mAuth = FirebaseAuth.getInstance();
        settings = getSharedPreferences("ScenicRoutePrefs", 0);
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
        mAuth.addAuthStateListener(mAuthListener);

        if (mAuth.getCurrentUser() == null) {
            signInAnonymously();
        } else {
            user = mAuth.getCurrentUser();
        }
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
    }

    /**
     * Set up the action when a user presses a menu item.
     * @param item
     * @return
     */
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
                signInAnonymously();
                return true;
            case R.id.menu_delete:
                deleteFromRouteList();
                setSupportActionBar(toolbar);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Sets the listeners on the buttons of the dialogs does this by creating a callable of the
     * createAccount and logUserIn and passing it to the dialogOnClickListener method.
     * @param action
     */
    public void setDialogListeners(String action) {
        buttonOk = (Button) dialog.findViewById(R.id.button_ok);
        buttonCancel = (Button) dialog.findViewById(R.id.button_cancel);
        email = (EditText) dialog.findViewById(R.id.txtUsername);
        password = (EditText) dialog.findViewById(R.id.txtPassword);

        if (action == "login") {
            dialog.setTitle("Log in with your email and password.");
            buttonOk.setText("Login");
            buttonOk.setOnClickListener(dialogOnclickListener(
                    new Callable<Boolean>() {
                        public Boolean call() { return logUserIn();} // The method to be called.
                    }));

        } else {
            buttonOk.setText("Sign up");
            dialog.setTitle("Create an account with your email and password.");
            buttonOk.setOnClickListener(dialogOnclickListener(
                    new Callable<Boolean>() {
                public Boolean call() { return createAccount();} // The method to be called.
            }));
        }
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Make dialog box visible.
        dialog.show();
    }

    /**
     * Creates an OnClickListener for the buttons in the dialog.
     * @param accountAction A method wrapped in a Calloble.
     * @return OnClickListener for a button.
     */
    private View.OnClickListener dialogOnclickListener(Callable<Boolean> accountAction) {
        final Callable action = accountAction;
        return new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
            if (emailText.length() > 0 && passwordText.length() > 0) {
                try {
                    action.call();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        "Please enter email and password", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    /**
     * Create an account by linking it to the anonymous account.
     * @return
     */
    private boolean createAccount() {
        final String emailText = email.getText().toString().trim();
        final String passwordText = password.getText().toString().trim();
        AuthCredential credential = EmailAuthProvider.getCredential(emailText, passwordText);
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(BaseActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(BaseActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(BaseActivity.this, "Account created.",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
        return true;
    }

    /**
     * Log the user in with email and password.
     * @return boolean.
     */
    private boolean logUserIn() {
        final String emailText = email.getText().toString().trim();
        final String passwordText = password.getText().toString().trim();
        mAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail", task.getException());
                            Toast.makeText(BaseActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(BaseActivity.this, "Logged in.",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
        user = mAuth.getCurrentUser();
        return true;
    }

    /**
     * Sign the user in anonymously, from the Firebase documentation.
     */
    public void signInAnonymously(){
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInAnonymously", task.getException());
                            Toast.makeText(BaseActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        user = mAuth.getCurrentUser();
    }

    /**
     * Checks if a selected item is not marked as selected and stores it.
     * @param key
     */
    public void addItemSelected(String key) {
        if (itemsSelected.contains(key)) {
            itemsSelected.remove(key);
        } else {
            itemsSelected.add(key);
            setSupportActionBar(toolbar);
        }
    }

    /**
     * Deletes a route from the adapter and the listView.
     */
    public void deleteFromRouteList(){
        if (user != null) {
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference()
                .child(mRefString).child(user.getUid());
            for (String key: itemsSelected) {
                myRef.child(key).removeValue();
                adapter.removeRoute(key);
            }
        }
        itemsSelected.clear();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, getText(R.string.checkInternet), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Hide menu items that are not needed during different states of app interaction.
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logIn = menu.findItem(R.id.login_menu_item);
        MenuItem createAccount = menu.findItem(R.id.create_account_menu_item);
        MenuItem signOut = menu.findItem(R.id.sign_out_menu_item);
        MenuItem delete_most_used = menu.findItem(R.id.menu_delete);

        if (user != null && !user.isAnonymous()) {
            logIn.setVisible(false);
            signOut.setVisible(true);
            createAccount.setVisible(false);
        }
        else {
            logIn.setVisible(true);
            signOut.setVisible(false);
            createAccount.setVisible(true);
        }
        if (itemsSelected.size() > 0) {
            delete_most_used.setVisible(true);
        }
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        adapter.clear();
    }

    /**
     * Start the maps activity when the Routegetter has finished the server request
     * and start the MapsActivity.
     * @param route
     */
    @Override
    public void onRouteReceived(Route route) {
        Intent intent = new Intent(getBaseContext(), MapsActivity.class);
        intent.putExtra("route", Parcels.wrap(route));
        intent.putExtra("routes", Parcels.wrap(adapter.routes));
        intent.putExtra("reference", mRefString);
        hideProgressDialog();
        startActivity(intent);
    }

    /**
     * Activates the showCase for the activity with the parameters set in that Activity.
     */
    public void activateShowcase() {
        new ShowcaseView.Builder(this)
                .setTarget(showCaseTarget)
                .setContentTitle(showCaseTitle)
                .setContentText(showCaseText)
                .hideOnTouchOutside()
                .build();
    }
}