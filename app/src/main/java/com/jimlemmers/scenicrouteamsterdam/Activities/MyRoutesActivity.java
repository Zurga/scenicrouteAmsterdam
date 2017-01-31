package com.jimlemmers.scenicrouteamsterdam.Activities;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jimlemmers.scenicrouteamsterdam.Interfaces.RouteItemSelected;
import com.jimlemmers.scenicrouteamsterdam.R;
import com.jimlemmers.scenicrouteamsterdam.Classes.Route;
import com.jimlemmers.scenicrouteamsterdam.Adapters.SavedRoutesAdapter;

import java.util.ArrayList;

public class MyRoutesActivity extends AppCompatActivity implements RouteItemSelected {
    String TAG = "MyRoutesActivity";
    SavedRoutesAdapter adapter;
    FirebaseUser user;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_routes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myRef = database.getReference("my_routes").child(user.getUid());
        adapter = new SavedRoutesAdapter(MyRoutesActivity.this, new ArrayList<Route>(),
                myRef, this);
        ListView listView = (ListView) findViewById(R.id.saved_routes_list);
        listView.setAdapter(adapter);
    }

    public void addItemSelected(String key) {

    }
}
