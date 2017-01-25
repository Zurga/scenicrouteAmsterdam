package com.jimlemmers.scenicrouteamsterdam;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jim on 1/19/17.
 */

public class Point {
    public MyLatLng location;

    public Point() {}

    public Point(String pointJSONString) {
        try {
            JSONObject pointJSON = new JSONObject(pointJSONString);
            String lat = pointJSON.has("lat") ? pointJSON.getString("lat") : null;
            String lon = pointJSON.has("lng") ? pointJSON.getString("lng") : null;
            if (lat != null & lon != null) {
                this.location = new MyLatLng(Double.parseDouble(lat), Double.parseDouble(lon));
            } else {
                this.location = null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
