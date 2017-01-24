package com.jimlemmers.scenicrouteamsterdam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jim on 1/23/17.
 */

public class SavedRoutesAdapter extends ArrayAdapter {
    public SavedRoutesAdapter(Context context, ArrayList<Route> routes) {
        super(context, 0, routes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Route route = (Route) getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.route_item, parent, false);
        }

        convertView.setTag(position);
        TextView fromTextView = (TextView) convertView.findViewById(R.id.saved_route_from);
        TextView toTextView = (TextView) convertView.findViewById(R.id.saved_route_to);

        fromTextView.setText(route.fromName);
        toTextView.setText(route.toName);

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = (Integer) v.getTag();
                Route route = (Route) getItem(position);
                return false;
            }
        });
        return convertView;
    }
}
