package com.jimlemmers.scenicrouteamsterdam;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jim on 1/24/17.
 */

public class MostUsedAdapter extends ArrayAdapter {
    private String TAG = "MostUsedAdapter";
    private Context context;
    public MostUsedAdapter(Context contxt, ArrayList<Route> routes) {
        super(contxt, 0, routes);
        context = contxt;
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
                intent.putExtra("from", route.from);
                intent.putExtra("fromName", route.fromName);
                intent.putExtra("to", route.to);
                intent.putExtra("toName", route.toName);
                intent.putExtra("preview", true);
                intent.putExtra("cycling", route.cycling);
                intent.putExtra("key", route.key);
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
