package com.example.tahfiz.aed;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.tahfiz.aed.Home.HomeActivity;
import com.example.tahfiz.aed.Nearby.NearbyRepo;
import com.example.tahfiz.aed.Nearby.PlaceData;

/**
 * Created by tahfiz on 15/5/2016.
 */
public class AlarmService extends IntentService {
    private static final String TAG = AlarmService.class.getSimpleName();
    private final NearbyRepo nearbyRepo;
    private NotificationManager alarmNotificationManager;


    public AlarmService() {
        super("AlarmService");
        nearbyRepo = new NearbyRepo(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        PlaceData data = nearbyRepo.getNearestPlace();

        String msg = "Nearest hospital: " + data.getPlaceName() + ",in " + data.getDistance() + " from your current location";

        sendNotification(msg);
    }

    private void sendNotification(String msg) {
        Log.d(TAG,"Preparing to send notification");
        alarmNotificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this,0,new Intent(this, HomeActivity.class),0);

        NotificationCompat.Builder alarmNotificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Nearby Emergency")
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

        alarmNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(1, alarmNotificationBuilder.build());
        Log.d(TAG,"Notification sent");
    }
}
