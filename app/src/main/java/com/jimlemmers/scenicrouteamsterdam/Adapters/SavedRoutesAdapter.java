package com.jimlemmers.scenicrouteamsterdam.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.jimlemmers.scenicrouteamsterdam.Activities.MapsActivity;
import com.jimlemmers.scenicrouteamsterdam.Classes.Route;
import com.jimlemmers.scenicrouteamsterdam.Interfaces.RouteItemSelected;
import com.jimlemmers.scenicrouteamsterdam.R;

import java.util.ArrayList;

/**
 * Created by jim on 1/23/17.
 */

public class SavedRoutesAdapter extends RouteAdapter {
    public SavedRoutesAdapter(Context context, ArrayList<Route> routes, DatabaseReference ref,
                              RouteItemSelected listener) {
        super(context, routes, ref, listener);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Route route = (Route) getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.route_item, parent, false);
        }

        convertView.setTag(position);
        TextView fromTextView = (TextView) convertView.findViewById(R.id.route_item_from);
        TextView toTextView = (TextView) convertView.findViewById(R.id.route_item_to);

        fromTextView.setText(route.fromName);
        toTextView.setText(route.toName);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                Route route = (Route) getItem(position);
                Intent intent = new Intent(mContext, MapsActivity.class);
                intent.putExtra("from", route.from);
                intent.putExtra("fromName", route.fromName);
                intent.putExtra("to", route.to);
                intent.putExtra("toName", route.toName);
                intent.putExtra("preview", true);
                intent.putExtra("cycling", route.cycling);
                Log.d("MYROUTES", route.routeJSON);
                intent.putExtra("points", route.routeJSON);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }
}
