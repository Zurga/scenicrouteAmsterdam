package com.jimlemmers.scenicrouteamsterdam.Models;

import org.parceler.Parcel;

/**
 * Created by jim on 1/24/17.
 * Needed because the LatLng class cannot be sent to Firebase and back.
 */

@Parcel
public class MyLatLng {
    public Double latitude;
    public Double longitude;
    public MyLatLng() {}

    public MyLatLng(Double lat, Double longi) {
        this.latitude = lat;
        this.longitude = longi;
    }
}
