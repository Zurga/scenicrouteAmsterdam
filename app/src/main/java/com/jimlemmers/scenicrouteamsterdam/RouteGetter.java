package com.jimlemmers.scenicrouteamsterdam;

import android.os.AsyncTask;
import android.util.Log;

import com.goebl.david.Response;
import com.goebl.david.Webb;
import com.google.firebase.database.Exclude;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jim on 1/24/17.
 */

public class RouteGetter extends AsyncTask<String, String, String> {
    private URL server_url;
    private String TAG = "Route";
    private OnTaskCompleted mListener;
    public String to;
    public String from;
    public String toName;
    public String fromName;
    public Boolean cycling;
    public Route route;

    public RouteGetter(String toInput, String toString, String fromInput, String fromString,
                 Boolean cyclingInput, OnTaskCompleted listener){
        mListener = listener;
        try {
            server_url = new URL(Constants.SERVER_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (toInput != "" & fromInput != ""){
            to = toInput;
            toName = toString;
            from = fromInput;
            fromName = fromString;
            cycling = cyclingInput;
        }
        this.execute();
    }
    @Override
    @Exclude
    protected void onPostExecute(String result) {
        if (result != null) {
            route = new Route(from, fromName, to, toName, cycling, result);
        }
        mListener.onTaskCompleted(this);
    }

    @Exclude
    private ArrayList<Object> instantiateFromJSON(JSONArray array, String type) {
        ArrayList<Object> resultArray = new ArrayList<>();
        try {
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject pointJSON = (JSONObject) array.get(i);
                    switch (type) {
                        case "point": resultArray.add(new Point(pointJSON.toString()));
                            break;

                        case "poi": resultArray.add(new POI(pointJSON.toString()));
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
    @Exclude
    protected String doInBackground(String[] params) {
        JSONObject apiResult = null;
        try {
            Webb webb = Webb.create();
            Response<JSONObject> response = webb
                    .post(Constants.SERVER_URL)
                    .param("to", to)
                    .param("from", from)
                    .param("cycling", cycling.toString())
                    .ensureSuccess()
                    .asJsonObject();

            apiResult = response.getBody();
            Log.d(TAG, apiResult.toString());
            Log.d(TAG, to.toString());
            Log.d(TAG, from.toString());
            Log.d(TAG, cycling.toString());
        }
        catch (Exception a) {
            try {
                apiResult = new JSONObject(Constants.TEST_ROUTE_2);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return apiResult.toString();
    }
}
