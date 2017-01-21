package com.jimlemmers.scenicrouteamsterdam;

import android.os.AsyncTask;
import android.util.Log;

import com.goebl.david.Response;
import com.goebl.david.Webb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by jim on 1/12/17.
 * This class will get a route from the server when instantiated with a to and from coordinate.
 * The reason to have this class extend AsyncTask is to have as few boilerplate code as possible in
 * the MapsActivity and keep the route creation in one class.
 */
public class Route extends AsyncTask{
    public String to;
    public String from;
    public Boolean cycling;
    public ArrayList<Object> points = new ArrayList<>();
    public ArrayList<Object> pois = new ArrayList<>();
    private URL server_url;
    private String TAG = "Route";
    private OnTaskCompleted mListener;

    public Route(String to, String from, Boolean cycling, OnTaskCompleted listener){
        mListener = listener;
        try {
            server_url = new URL(Constants.SERVER_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (to != "" & from != ""){
            this.to = to;
            this.from = from;
            this.cycling = cycling;
        }
        this.execute();

    }

    public Route(JSONObject routeJSON){
        //TODO remove this when the server is ready.
        try {
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
    }

    @Override
    protected void onPostExecute(Object o) {
        if (o != null) {
            String jsonString = o.toString();
            try {
                JSONObject routeJSON = new JSONObject(jsonString);
                points = instantiateFromJSON(routeJSON.getJSONArray("route"), "poi");
                //pois = instantiateFromJSON(routeJSON.getJSONArray("pois"), "pois");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mListener.onTaskCompleted(this);
    }

    private ArrayList<Object> instantiateFromJSON(JSONArray array, String type) {
        ArrayList<Object> resultArray = new ArrayList<>();
        try {
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject pointJSON = (JSONObject) array.get(i);
                    switch (type) {
                        case "point": resultArray.add(new Point(pointJSON));
                            break;

                        case "poi": resultArray.add(new POI(pointJSON));
                            break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultArray;
    }
    /*
     * Taken from here:https://developer.android.com/training/basics/network-ops/connecting.html
     */
    @Override
    protected Object doInBackground(Object[] params) {
        Webb webb = Webb.create();
        Response<JSONObject> response = webb
                .post(Constants.SERVER_URL)
                .param("to", to)
                .param("from", from)
                .param("cycling", cycling.toString())
                .ensureSuccess()
                .asJsonObject();

        JSONObject apiResult = response.getBody();
        Log.d(TAG, apiResult.toString());
        Log.d(TAG, to.toString());
        Log.d(TAG, from.toString());
        Log.d(TAG, cycling.toString());
        return apiResult;
    }
}