package com.jimlemmers.scenicrouteamsterdam.Models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by jim on 1/12/17.
 * This class holds all the information about a Point of interest
 */
@Parcel
public class POI extends Point {
    public String name;
    public String description;
    public String uri;
    public String information;
    public String picture;

    public POI() {}

    public POI(String pointJSONString){
        super(pointJSONString);
        Log.d("POI", pointJSONString);


        try {
            JSONObject pointJSON = new JSONObject(pointJSONString);
            this.name = pointJSON.has("name") ? pointJSON.getString("name") : "Point of interest";
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
        return "<html><h2>"+ name + "</h2>" +
                "<img src='https://placeholdit.imgix.net/~text?txtsize=33&txt=350%C3%97150&w=350&h=150'/>"+
                "<p>" + description + "</p></html>";
    }



}
