package com.jimlemmers.scenicrouteamsterdam;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by jim on 1/12/17.
 */

public class POI {
    String name;
    String description;
    String uri;
    String information;
    String picture;
    LatLng location;

    public POI(String lat, String lon, String name,
               String description, String uri, String information, String picture) {
        this.description = description;
        this.uri = uri;
        this.information = information;
        this.name = name;
        this.picture = picture;
        this.location = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
    }

    public String generateHTML(){
        // TODO generate real html for the object.
        return "<html><h2>A name for a place</h2>" +
                "<img src='https://placeholdit.imgix.net/~text?txtsize=33&txt=350%C3%97150&w=350&h=150'/>"+
                "<p>The description for that certain place</p></html>";
    }
}
