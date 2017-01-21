package com.jimlemmers.scenicrouteamsterdam;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jim on 1/19/17.
 */

public class Point {
    LatLng location;

    public Point(JSONObject pointJSON) {
        try {
            String lat = pointJSON.has("lat") ? pointJSON.getString("lat") : null;
            String lon = pointJSON.has("lng") ? pointJSON.getString("lng") : null;
            if (lat != null & lon != null) {
                this.location = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
            } else {
                this.location = null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
