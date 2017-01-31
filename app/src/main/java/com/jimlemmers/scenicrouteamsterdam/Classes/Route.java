package com.jimlemmers.scenicrouteamsterdam.Classes;

import com.google.firebase.database.Exclude;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jim on 1/12/17.
 * This class will get a route from the server when instantiated with a to and from coordinate.
 * The reason to have this class extend AsyncTask is to have as few boilerplate code as possible in
 * the MapsActivity and keep the route creation in one class.
 */

public class Route { //extends AsyncTask<String, String, String>{
    public String to;
    public String from;
    public String toName;
    public String fromName;
    public Boolean cycling;
    public String routeJSON;
    public String key;
    public ArrayList<Point> points = new ArrayList<>();
    public ArrayList<POI> pois = new ArrayList<>();
    public int timesUsed = 0;


    public Route() {}

    public Route(String toInput, String toString, String fromInput, String fromString,
                 Boolean cyclingInput, int tUsed, String key, String json){

        try {
            if (toInput != "" & fromInput != ""){
                to = toInput;
                toName = toString;
                from = fromInput;
                fromName = fromString;
                cycling = cyclingInput;
                timesUsed = tUsed;
                routeJSON = json;
                this.key = key;
            }
            JSONObject mRouteJSON = new JSONObject(routeJSON);
            JSONArray pointsArray = mRouteJSON.getJSONArray("route");
            JSONArray poisArray = mRouteJSON.getJSONArray("pois");

            for (int i = 0; i < pointsArray.length(); i++) {
                JSONObject pointJSON = (JSONObject) pointsArray.get(i);
                points.add(new Point(pointJSON.toString()));
            }

            for (int i = 0; i < poisArray.length(); i++) {
                JSONObject poiJSON = (JSONObject) poisArray.get(i);

                pois.add(new POI(poiJSON.toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}