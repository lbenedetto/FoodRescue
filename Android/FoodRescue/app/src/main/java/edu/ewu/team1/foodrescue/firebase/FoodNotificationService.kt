package edu.ewu.team1.foodrescue.firebase

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.SystemClock
import android.support.v4.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import edu.ewu.team1.foodrescue.FoodEvent
import edu.ewu.team1.foodrescue.R

class FoodNotificationService : FirebaseMessagingService() {

	private lateinit var sharedPref: SharedPreferences

	companion object {
		const val NOTIFICATION_CHANNEL_ID = "ewu.edu.team1.foodRescue.foodNotificationChannel"
		const val NOTIFICATION_ID = "notification_id"
	}

	override fun onMessageReceived(remoteMessage: RemoteMessage?) {
		sharedPref = applicationContext.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
		val showNotification = sharedPref.getBoolean("receiveEventStartNotifications", false)

		if (showNotification) {
			if (remoteMessage!!.notification != null) {
				val n = remoteMessage.notification
				val title = n!!.title
				val body = n.body
				val data = remoteMessage.data["data"]
				val foodEvent = FoodEvent(title, body, data, System.currentTimeMillis())
				showNotification(foodEvent)
				saveEvent(foodEvent)
			}
		}
	}


	/**
	 * Saves the food event to be displayed later in EaterFragment
	 *
	 * @param foodEvent the event
	 */
	private fun saveEvent(foodEvent: FoodEvent) {
		val editor = sharedPref.edit()
		editor.putString(
				"foodEvents",
				"${sharedPref.getString("foodEvents", "")}\n$foodEvent"
		)
		editor.apply()
	}

	/**
	 * Shows a notification with the specified message
	 *
	 * @param foodEvent the event
	 */
	private fun showNotification(foodEvent: FoodEvent) {
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
				.setContentTitle("Free food at ${foodEvent.title}")
				.setContentText(foodEvent.body)

		val id = sharedPref.getInt(NOTIFICATION_ID, Math.random().toInt())
		notificationManager.notify(id, notificationBuilder.build())

		//https://stackoverflow.com/questions/23874203/create-an-android-notification-with-expiration-date
		val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
		val intent = Intent(applicationContext, NotificationClearer::class.java)
		intent.action = NotificationClearer.packageName
		intent.putExtra(NOTIFICATION_ID, id)
		val pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
		alarmManager.set(
				AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime() + foodEvent.duration * 60000,
				pendingIntent)

		sharedPref.edit().putInt(NOTIFICATION_ID, id + 1).apply()
	}
}
