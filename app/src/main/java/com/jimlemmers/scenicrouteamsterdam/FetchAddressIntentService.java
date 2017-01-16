package com.jimlemmers.scenicrouteamsterdam;

import android.app.IntentService;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by jim on 1/13/17.
 */

public class FetchAddressIntentService extends IntentService {
    protected ResultReceiver mReceiver;

    public FetchAddressIntentService(String name, ResultReceiver mReceiver) {
        super(name);
        this.mReceiver = mReceiver;
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String address = intent.getStringExtra("to");
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List coordinates = geocoder.getFromLocationName(address, 1);

        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
}
