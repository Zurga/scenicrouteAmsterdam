package com.jimlemmers.scenicrouteamsterdam.Models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by jim on 1/19/17.
 * Base class for points on the map.
 */
@Parcel
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
