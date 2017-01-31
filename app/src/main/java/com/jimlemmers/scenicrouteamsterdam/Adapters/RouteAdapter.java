package com.jimlemmers.scenicrouteamsterdam.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.jimlemmers.scenicrouteamsterdam.Activities.MapsActivity;
import com.jimlemmers.scenicrouteamsterdam.Classes.Route;
import com.jimlemmers.scenicrouteamsterdam.Interfaces.MostUsedItemSelected;
import com.jimlemmers.scenicrouteamsterdam.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jim on 1/24/17.
 */

public class RouteAdapter extends ArrayAdapter {
    private String TAG = "RouteAdapter";
    private Context mContext;
    private MostUsedItemSelected mListener;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private List<String> routeIds = new ArrayList<>();
    //private List<Route> routes = new ArrayList<>();

    public RouteAdapter(Context context, ArrayList<Route> routes, DatabaseReference ref,
                        MostUsedItemSelected listener) {
        super(context, 0, routes);
        mContext = context;
        mDatabase = ref;

        mListener = listener;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null) {
            Query query = mDatabase.orderByChild("mostUsed").limitToFirst(10);
            Log.d("USER IN FAVORITES", user.getUid());
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d("Added", dataSnapshot.getValue().toString());
                    addMostUsed(dataSnapshot);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d("Changed", dataSnapshot.getValue().toString());
                    updateMostUsed(dataSnapshot);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    removeMostUsed(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void addMostUsed(DataSnapshot dataSnapshot) {
        Route route = dataSnapshot.getValue(Route.class);
        /*
        route.key = dataSnapshot.getKey();
        mostUsed.put(route.key, route);
        adapter.clear();
        adapter.addAll(mostUsed.values());
        */
        this.add(route);
        Log.d("FAVOURITE", dataSnapshot.getValue().toString());
        sortItems();
        notifyDataSetChanged();
    }

    public void updateMostUsed(DataSnapshot dataSnapshot) {
        for (int i = 0; i < this.getCount(); i++) {
            Route route = (Route) this.getItem(i);
            if (route.key == dataSnapshot.getKey()) {
                this.remove(route);
                Log.d(TAG, "Keys match");
            }
        }
        addMostUsed(dataSnapshot);
    }

    public void removeMostUsed(DataSnapshot dataSnapshot){
        for (int i = 0; i < this.getCount(); i++) {
            Route route = (Route) this.getItem(i);
            if (route.key == dataSnapshot.getKey())
                this.remove(route);
        }
        notifyDataSetChanged();
    }

    public void sortItems() {
        this.sort(new Comparator<Route>() {
            @Override
            public int compare(Route o1, Route o2) {
                return o2.timesUsed - o1.timesUsed;
            }
        });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Route route = (Route) getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.route_item, parent, false);
        }
        convertView.setTag(route);
        TextView fromTextView = (TextView) convertView.findViewById(R.id.route_item_from);
        TextView toTextView = (TextView) convertView.findViewById(R.id.route_item_to);

        fromTextView.setText(route.fromName);
        toTextView.setText(route.toName);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Route route = (Route) v.getTag();
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("key", route.key);
                mContext.startActivity(intent);
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Route route = (Route) v.getTag();
                mListener.addItemSelected(route.key);
                v.setBackgroundColor(0xAA000000);
                return true;
            }
        });

        return convertView;
    }

}
