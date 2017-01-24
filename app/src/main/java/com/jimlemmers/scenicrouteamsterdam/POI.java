package com.jimlemmers.scenicrouteamsterdam;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jim on 1/12/17.
 * This class holds all the information about a Point of interest
 */

public class POI extends Point{
    public String name;
    public String description;
    public String uri;
    public String information;
    public String picture;

    public POI() {}

    public POI(String pointJSONString){
        super(pointJSONString);

        try {
            JSONObject pointJSON = new JSONObject(pointJSONString);
            this.name = pointJSON.has("name") ? pointJSON.getString("name") : null;
            this.description = pointJSON.has("description") ? pointJSON.getString("description") : null;
            this.uri = pointJSON.has("uri") ? pointJSON.getString("uri") : null;
            this.information = pointJSON.has("information") ? pointJSON.getString("information") : null;
            this.picture = pointJSON.has("picture") ? pointJSON.getString("picture") : null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String generateHTML(){
        // TODO generate real html for the object.
        return "<html><h2>A name for a place</h2>" +
                "<img src='https://placeholdit.imgix.net/~text?txtsize=33&txt=350%C3%97150&w=350&h=150'/>"+
                "<p>The description for that certain place</p></html>";
    }
}
