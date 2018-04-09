package edu.ewu.team1.foodrescue.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import edu.ewu.team1.foodrescue.FoodEvent;
import edu.ewu.team1.foodrescue.R;

public class FoodNotificationService extends FirebaseMessagingService {
    SharedPreferences sharedPref;

    public FoodNotificationService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean showNotification = sharedPref.getBoolean("receiveEventStartNotifications", false);

        if (showNotification) {
            if (remoteMessage.getNotification() != null) {
                RemoteMessage.Notification n = remoteMessage.getNotification();
                String title = n.getTitle();
                String body = n.getBody();
                showNotification(title, body);

                String[] data = remoteMessage.getData().get("data").split(",");
                saveEvent(title, body, data);
            }
        }
    }


    /**
     * Saves the food event to be displayed later in EaterFragment
     *
     * @param title title of event
     * @param body  body of event
     * @param data  gps loc of event
     */
    private void saveEvent(String title, String body, String[] data) {

        String events = sharedPref.getString("foodEvents", "");

        events += new FoodEvent(title, body, data[0], data[1], System.currentTimeMillis()).toString();

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("foodEvents", events);
        editor.apply();
    }

    /**
     * Shows a notification with the specified message
     *
     * @param title The location that has free food
     * @param body  An optional description from the feeder
     */
    private void showNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "ewu.edu.team1.foodRescue.foodNotificationChannel";

        //Android O specific settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);
            assert notificationManager != null;
            // Configure the notification channel.
            notificationChannel.setDescription("Notifications about the availability of free food on campus");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle("Free food at " + title)
                .setContentText(body);

        assert notificationManager != null;
        //TODO: If each food event has a unique ID, notifications could be automatically cleared when the event is over. Ask Brad to add this feature
        notificationManager.notify(/*notification id*/1, notificationBuilder.build());
    }
}
