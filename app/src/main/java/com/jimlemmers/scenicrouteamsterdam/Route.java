package com.jimlemmers.scenicrouteamsterdam;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jim on 1/12/17.
 *
 * The code for the Parcelable was taken from this stackoverflow post:
 * http://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents#2141166
 */

public class Route implements Parcelable{
    public ArrayList<Object> points = new ArrayList<>();

    private Route(Parcel in) {
        //TODO rebuild the route
    }

    public Route(JSONObject routeJSON){
        try {
            JSONArray pointsJSON = routeJSON.getJSONArray("route");
            if (pointsJSON != null) {
                for (int i=0; i < pointsJSON.length(); i++){
                    JSONObject pointJSON = (JSONObject) pointsJSON.get(i);
                    POI point = new POI(pointJSON.getString("lat"), pointJSON.getString("lng"),
                            "","","","","");
                    points.add(point);
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO serialize the route
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
