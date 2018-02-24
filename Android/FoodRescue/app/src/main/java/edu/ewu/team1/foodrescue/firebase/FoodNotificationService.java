package edu.ewu.team1.foodrescue.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import edu.ewu.team1.foodrescue.R;

public class FoodNotificationService extends FirebaseMessagingService {
    public FoodNotificationService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        Log.d(TAG, "From: " + remoteMessage.getFrom());
//        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//        }
//        if (remoteMessage.getNotification() != null) {
//            String message = remoteMessage.getNotification().getBody();
//            Log.d(TAG, "Message Notification Body: " + message);
//        }

        // TODO: (Easy) Only display messages if enabled in notification settings
        //Read from shared preferences. See EaterFragment.java for keys to use
        // TODO: (Hard) Add notification info to list in EaterFragment

        if (remoteMessage.getNotification() != null) {
            showNotification(remoteMessage.getNotification().getBody());
        }
    }

    private void showNotification(String message) {
        // TODO: (Hard) Get the GPS coordinates from the data payload and use it to open a map
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "ewu.edu.team1.foodRescue.foodNotificationChannel";

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
                .setTicker("Hearty365")
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle("Free Food Available")
                .setContentText(message);

        assert notificationManager != null;
        //TODO: If each food event has a unique ID, notifications could be automatically cleared when the event is over
        notificationManager.notify(/*notification id*/1, notificationBuilder.build());
    }
}
