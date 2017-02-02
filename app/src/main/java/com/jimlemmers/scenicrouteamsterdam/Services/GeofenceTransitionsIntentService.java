package com.jimlemmers.scenicrouteamsterdam.Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.jimlemmers.scenicrouteamsterdam.Activities.InformationActivity;
import com.jimlemmers.scenicrouteamsterdam.Activities.MapsActivity;
import com.jimlemmers.scenicrouteamsterdam.Classes.GeofenceErrorMessages;
import com.jimlemmers.scenicrouteamsterdam.Models.POI;
import com.jimlemmers.scenicrouteamsterdam.R;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jim on 1/27/17.
 * This is taken from: https://github.com/googlesamples/android-play-location/
 * and adapted to accommodate a list of POIs which contains the data to add to the notification.
 */

public class GeofenceTransitionsIntentService extends IntentService {
    protected static final String TAG = "GeofenceTransitionsIS";
    private ArrayList<POI> pois;

    public GeofenceTransitionsIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        pois = (ArrayList<POI>) Parcels.unwrap(intent.getParcelableExtra("pois"));
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }
        Log.d(TAG, "On HandleIntent");

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            ArrayList<POI> geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );

            // Send notification and log the transition details.
            for (POI poi : geofenceTransitionDetails) {
                sendNotification(poi);
            }
            Log.i(TAG, geofenceTransitionDetails.toString());
        } else {
            // Log the error.
            Log.e(TAG, getString(R.string.geofence_transition_invalid_type,
                    geofenceTransition));
        }
    }

    private ArrayList<POI> getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesPOIsList= new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            int id = Integer.valueOf(geofence.getRequestId());
            triggeringGeofencesPOIsList.add(pois.get(id));
        }

        return triggeringGeofencesPOIsList;
    }

    private void sendNotification(POI poi) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), InformationActivity.class);
        notificationIntent.putExtra("poi", Parcels.wrap(poi));
        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MapsActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.drawable.marker)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.marker))
                .setColor(Color.RED)
                .setContentTitle("You are close to a point of interest.")
                .setContentText(poi.name)
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                //return getString(R.string.geofence_transition_entered);
                return "Entered";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "Exited";
                //return getString(R.string.geofence_transition_exited);
            default:
                return "Unknown";
                //return getString(R.string.unknown_geofence_transition);
        }
    }
}
