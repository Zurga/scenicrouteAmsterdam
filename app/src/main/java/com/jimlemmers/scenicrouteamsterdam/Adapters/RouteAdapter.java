package com.jimlemmers.scenicrouteamsterdam.Adapters;

import android.content.Context;
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
import com.jimlemmers.scenicrouteamsterdam.Async.RouteGetter;
import com.jimlemmers.scenicrouteamsterdam.Models.Route;
import com.jimlemmers.scenicrouteamsterdam.Interfaces.RouteReceived;
import com.jimlemmers.scenicrouteamsterdam.Interfaces.RouteItemSelected;
import com.jimlemmers.scenicrouteamsterdam.R;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by jim on 1/24/17.
 */

public class RouteAdapter extends ArrayAdapter {
    private String TAG = "RouteAdapter";
    public Context mContext;
    public RouteItemSelected mListener;
    public RouteReceived routeListener;
    static public ArrayList<Route> routes = new ArrayList<>();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private DatabaseReference mDatabase;

    public RouteAdapter(Context context, ArrayList<Route> routes, DatabaseReference ref,
                        RouteItemSelected listener, RouteReceived routeListener) {
        super(context, 0, routes);
        mContext = context;
        mDatabase = ref;

        mListener = listener;
        this.routeListener = routeListener;

        if (user != null) {
            Query query = mDatabase.orderByChild("mostUsed").limitToFirst(10);
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    addRoute(dataSnapshot);
                    RouteAdapter.routes.add(dataSnapshot.getValue(Route.class));
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    updateRoute(dataSnapshot);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    removeRoute(dataSnapshot.getKey());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void addRoute(DataSnapshot dataSnapshot) {
        Route route = dataSnapshot.getValue(Route.class);
        /*
        route.key = dataSnapshot.getKey();
        mostUsed.put(route.key, route);
        adapter.clear();
        adapter.addAll(mostUsed.values());
        */
        this.add(route);
        //Log.d("FAVOURITE", dataSnapshot.getValue().toString());
        sortItems();
        notifyDataSetChanged();
    }

    public void updateRoute(DataSnapshot dataSnapshot) {
        removeRoute(dataSnapshot.getKey());
        addRoute(dataSnapshot);
    }

    public void removeRoute(String key){
        for (int i = 0; i < this.getCount(); i++) {
            Route route = (Route) this.getItem(i);
            if (route.key == key)
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
                new RouteGetter(route.from, route.fromName, route.to, route.toName,
                        route.cycling, route.timesUsed, route.key, "", routeListener);
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
