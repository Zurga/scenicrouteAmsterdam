package com.jimlemmers.scenicrouteamsterdam.Async;

import android.os.AsyncTask;
import android.util.Log;

import com.goebl.david.Response;
import com.goebl.david.Webb;
import com.google.firebase.database.Exclude;

import com.jimlemmers.scenicrouteamsterdam.Classes.Constants;
import com.jimlemmers.scenicrouteamsterdam.Classes.POI;
import com.jimlemmers.scenicrouteamsterdam.Classes.Point;
import com.jimlemmers.scenicrouteamsterdam.Classes.Route;
import com.jimlemmers.scenicrouteamsterdam.Interfaces.OnTaskCompleted;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

/**
 * Created by jim on 1/24/17.
 */

public class RouteGetter extends AsyncTask<String, String, String> {
    private URL server_url;
    private String TAG = "Route";
    private OnTaskCompleted mListener;
    public String mTo;
    public String mFrom;
    public String mToName;
    public String mFromName;
    public Boolean mCycling;
    public int mTimesUsed;
    public String key;
    public Route route;

    public RouteGetter(String toInput, String toString, String fromInput, String fromString,
                       Boolean cyclingInput, OnTaskCompleted listener){
       this(toInput, toString, fromInput, fromString, cyclingInput, 0, null, "", listener);
    }

    public RouteGetter(String toInput, String toString, String fromInput, String fromString,
                       Boolean cyclingInput, String pointsJson, OnTaskCompleted listener) {
        this(toInput, toString, fromInput, fromString, cyclingInput, 0, null, pointsJson, listener);
    }
    public RouteGetter(String toInput, String toString, String fromInput, String fromString,
                       Boolean cyclingInput, int timesUsed, String key, String pointsJson,
                       OnTaskCompleted listener) {
        mListener = listener;
        try {
            server_url = new URL(Constants.SERVER_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (toInput != "" & fromInput != ""){
            mTo = toInput;
            mToName = toString;
            mFrom = fromInput;
            mFromName = fromString;
            mCycling = cyclingInput;
            mTimesUsed = timesUsed;
            this.key = key;
        }
        if (pointsJson == "") {
            this.execute();
        } else {
            route = new Route(mFrom, mFromName, mTo, mToName, mCycling, mTimesUsed, "", pointsJson);
        }
    }
    @Override
    @Exclude
    protected void onPostExecute(String result) {
        if (result != null) {
            route = new Route(mFrom, mFromName, mTo, mToName, mCycling, mTimesUsed, key, result);
        }
        mListener.onTaskCompleted(route);
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
                    .param("to", mTo)
                    .param("from", mFrom)
                    .param("cycling", mCycling.toString())
                    .ensureSuccess()
                    .asJsonObject();

            apiResult = response.getBody();
            Log.d(TAG, apiResult.toString());
        }
        catch (Exception a) {
            try {
                apiResult = new JSONObject(Constants.TEST_ROUTE);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return apiResult.toString();
    }
}
