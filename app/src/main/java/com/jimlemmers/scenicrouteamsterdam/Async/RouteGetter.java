package com.jimlemmers.scenicrouteamsterdam.Async;

import android.os.AsyncTask;
import android.util.Log;

import com.goebl.david.Response;
import com.goebl.david.Webb;

import com.jimlemmers.scenicrouteamsterdam.Classes.Constants;
import com.jimlemmers.scenicrouteamsterdam.Models.Route;
import com.jimlemmers.scenicrouteamsterdam.Interfaces.RouteReceived;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jim on 1/24/17.
 * This will get a route from the server and call the onRouteReceived function in the calling
 * activity giving back a Route object.
 */

public class RouteGetter extends AsyncTask<String, String, String> {
    private URL server_url;
    private String TAG = "Route";
    private RouteReceived mListener;
    public String mFrom;
    public String mFromAddress;
    public String mTo;
    public String mToAddress;
    public Boolean mCycling;
    public int mTimesUsed;
    public String key;
    public Route route;

    /**
     * Constructor for the MainActivity where a route is created from just the basic information.
     * @param fromLatLng
     * @param fromString
     * @param toLatLng
     * @param toString
     * @param cyclingInput
     * @param listener
     */
    public RouteGetter(String fromLatLng, String fromString, String toLatLng, String toString,
                       Boolean cyclingInput, RouteReceived listener){
       this(fromLatLng, fromString, toLatLng, toString, cyclingInput, 0, null, "", listener);
    }

    /**
     * The constructor which is called when a new route is created from the most used address pairs.
     * @param fromLatLng
     * @param fromString
     * @param toLatLng
     * @param toString
     * @param cyclingInput
     * @param timesUsed
     * @param key
     * @param pointsJson
     * @param listener
     */
    public RouteGetter(String fromLatLng, String fromString, String toLatLng, String toString,
                       Boolean cyclingInput, int timesUsed, String key, String pointsJson,
                       RouteReceived listener) {
        mListener = listener;
        try {
            server_url = new URL(Constants.SERVER_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (toLatLng != "" & fromLatLng != ""){
            mFrom = fromLatLng;
            mFromAddress = fromString;
            mTo = toLatLng;
            mToAddress = toString;
            mCycling = cyclingInput;
            mTimesUsed = timesUsed;
            this.key = key;
        }
        if (pointsJson == "") {
            this.execute();
        } else {
            route = new Route(mFrom, mFromAddress, mTo, mToAddress, mCycling, mTimesUsed, key, pointsJson);
            mListener.onRouteReceived(route);
        }
    }

    /**
     * Create the Route object and calls the listener on the parent activity.
     * @param result
     */
    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            route = new Route(mFrom, mFromAddress, mTo, mToAddress, mCycling, mTimesUsed, key, result);
        }
        mListener.onRouteReceived(route);
    }

    /**
     * Creates a connection to the server and get the json needed to create a Route.
     * For testing purposes, a default route is used when the server is not reachable.
     * The code used is taken from here: https://github.com/hgoebl/DavidWebb
     * @param params
     * @return
     */
    @Override
    protected String doInBackground(String[] params) {
        JSONObject apiResult = null;
        try {
            Webb webb = Webb.create();
            Response<JSONObject> response = webb
                    .post(Constants.SERVER_URL)
                    .param("to", mTo.toString())
                    .param("from", mFrom.toString())
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
