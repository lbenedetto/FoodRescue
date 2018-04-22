package edu.ewu.team1.foodrescue.firebase

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import edu.ewu.team1.foodrescue.DataManager
import edu.ewu.team1.foodrescue.FoodEvent
import edu.ewu.team1.foodrescue.R

class FoodNotificationService : FirebaseMessagingService() {

	private lateinit var sharedPref: SharedPreferences

	companion object {
		const val NOTIFICATION_CHANNEL_ID = "ewu.edu.team1.foodRescue.foodNotificationChannel"
	}
	//TODO: Improve notifications and users settings
	//https://material.io/guidelines/patterns/notifications.html
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
				NotificationShower.show(foodEvent, DataManager(sharedPref), applicationContext)
			}
		}
	}
}
