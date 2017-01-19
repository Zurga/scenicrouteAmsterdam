package com.jimlemmers.scenicrouteamsterdam;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
    private URL server_url;
    private String TAG = "Route";

    public Route(String to, String from, Boolean cycling){
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
                JSONArray pointsJSON = routeJSON.getJSONArray("route");
                if (pointsJSON != null) {
                    for (int i = 0; i < pointsJSON.length(); i++) {
                        JSONObject pointJSON = (JSONObject) pointsJSON.get(i);
                        POI point = new POI(pointJSON);
                        /*
                        POI point = new POI(pointJSON.getString("lat"), pointJSON.getString("lng"),
                                pointJSON.getString("name"), "", "", "", "");
                        */
                        points.add(point);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * Taken from here:https://developer.android.com/training/basics/network-ops/connecting.html
     */
    @Override
    protected Object doInBackground(Object[] params) {
        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;
        try {
            server_url = new URL("https://jimlemmers.com/scenicroute");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            connection = (HttpsURLConnection) server_url.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(3000);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(3000);
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod("GET");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);
            connection.setRequestProperty("to", to);
            connection.setRequestProperty("from", from);
            connection.setRequestProperty("cycling", cycling.toString());
            // Open communications link (network traffic occurs here).
            Log.d(TAG, connection.toString());
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();

            if (stream != null) {
                // Converts Stream to String with max length of 500.
                Scanner s = new Scanner(stream).useDelimiter("\\A");
                result = s.hasNext() ? s.next() : "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }
}