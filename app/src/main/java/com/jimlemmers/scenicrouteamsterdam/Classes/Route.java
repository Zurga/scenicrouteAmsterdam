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
    @Exclude
    private String pointsArrayString;
    public String key;
    public ArrayList<Point> points = new ArrayList<>();
    private ArrayList<POI> pois = new ArrayList<>();
    public int timesUsed = 0;


    public Route() {}

    public Route(String toInput, String toString, String fromInput, String fromString,
                 Boolean cyclingInput, String json){
        if (toInput != "" & fromInput != ""){
            to = toInput;
            toName = toString;
            from = fromInput;
            fromName = fromString;
            cycling = cyclingInput;
        }
        try {
            JSONObject routeJSON = new JSONObject(json);
            JSONArray pointsArray = routeJSON.getJSONArray("route");
            pointsArrayString = pointsArray.toString();
            //JSONArray poisArray = routeJSON.getJSONArray("pois");

            for (int i = 0; i < pointsArray.length(); i++) {
                JSONObject pointJSON = (JSONObject) pointsArray.get(i);
                points.add(new Point(pointJSON.toString()));
            }
                /*
                for (int i = 0; i < poisArray.length(); i++) {
                    JSONObject poiJSON = (JSONObject) poisArray.get(i);

                    pois.add(new POI(poiJSON.toString()));
                }
                */
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //this.execute();
    }

    /*
    public Route(JSONObject routeJSON){
        //TODO remove this when the server is ready.
        try {
            SONObject routeJSON
            JSONArray pointsJSON = routeJSON.getJSONArray("route");
            if (pointsJSON != null) {
                for (int i=0; i < pointsJSON.length(); i++){
                    JSONObject pointJSON = (JSONObject) pointsJSON.get(i);
                    POI point = new POI(pointJSON);
                    points.add(point);
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }*/


}