package edu.ewu.team1.foodrescue.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

import edu.ewu.team1.foodrescue.FoodEvent
import edu.ewu.team1.foodrescue.R

class FoodNotificationService : FirebaseMessagingService() {
	private lateinit var sharedPref: SharedPreferences

	companion object {
		const val NOTIFICATION_CHANNEL_ID = "ewu.edu.team1.foodRescue.foodNotificationChannel"
	}

	override fun onMessageReceived(remoteMessage: RemoteMessage?) {
		sharedPref = applicationContext.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
		val showNotification = sharedPref.getBoolean("receiveEventStartNotifications", false)

		if (showNotification) {
			if (remoteMessage!!.notification != null) {
				val n = remoteMessage.notification
				val title = n!!.title
				val body = n.body
				showNotification(title, body)

				val data = remoteMessage.data["data"]
				saveEvent(title, body, data)
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
	private fun saveEvent(title: String?, body: String?, data: String?) {
		var events = sharedPref.getString("foodEvents", "")

		events += FoodEvent(title, body, data, System.currentTimeMillis()).toString()

		val editor = sharedPref.edit()
		editor.putString("foodEvents", events)
		editor.apply()
	}

	/**
	 * Shows a notification with the specified message
	 *
	 * @param title The location that has free food
	 * @param body  An optional description from the feeder
	 */
	private fun showNotification(title: String?, body: String?) {
		val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

		//Android O specific settings
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH)
			// Configure the notification channel.
			notificationChannel.description = "Notifications about the availability of free food on campus"
			notificationChannel.enableLights(true)
			notificationChannel.lightColor = Color.RED
			notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
			notificationChannel.enableVibration(true)
			notificationManager.createNotificationChannel(notificationChannel)
		}

		val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)

		notificationBuilder.setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_ALL)
				.setWhen(System.currentTimeMillis())
				.setSmallIcon(R.drawable.burger_bell)
				.setPriority(Notification.PRIORITY_MAX)
				.setContentTitle("Free food at " + title!!)
				.setContentText(body)

		//TODO: If each food event has a unique ID, notifications could be automatically cleared when the event is over. Ask Brad to add this feature
		notificationManager.notify(/*notification id*/1, notificationBuilder.build())
	}
}
