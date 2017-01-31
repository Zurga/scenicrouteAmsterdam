package com.jimlemmers.scenicrouteamsterdam.Async;

import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.jimlemmers.scenicrouteamsterdam.Classes.Constants;
import com.jimlemmers.scenicrouteamsterdam.Classes.Route;

import java.util.HashMap;

/**
 * Created by jim on 1/24/17.
 */

public class ReadMostUsed {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    ArrayAdapter adapter;
    private HashMap<String, Route> mostUsed = new HashMap<>();

    public ReadMostUsed(final ArrayAdapter adapter){
        this.adapter = adapter;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.FIREBASE_ROUTE);
            Query query = mDatabase.child(user.getUid()).orderByChild("mostUsed").limitToFirst(10);
            Log.d("USER IN FAVORITES", user.getUid());
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d("Added", dataSnapshot.getValue().toString());
                    updateMostUsed(dataSnapshot, previousChildName);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d("Changed", dataSnapshot.getValue().toString());
                    updateMostUsed(dataSnapshot, previousChildName);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    public void updateMostUsed(DataSnapshot dataSnapshot, String previousChildName) {
        Route route = dataSnapshot.getValue(Route.class);
        route.key = dataSnapshot.getKey();
        mostUsed.put(route.key, route);
        adapter.clear();
        adapter.addAll(mostUsed.values());
        adapter.add(route);
        Log.d("FAVOURITE", dataSnapshot.getValue().toString());
        adapter.notifyDataSetChanged();
    }
}