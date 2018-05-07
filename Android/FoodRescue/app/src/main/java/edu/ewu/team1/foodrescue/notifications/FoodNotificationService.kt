package edu.ewu.team1.foodrescue.notifications

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import edu.ewu.team1.foodrescue.utilities.DataManager
import edu.ewu.team1.foodrescue.utilities.FoodEvent
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
		if(remoteMessage != null){
			val title = remoteMessage.data["title"]
			val body = remoteMessage.data["body"]
			val lat = remoteMessage.data["lat"]
			val lng = remoteMessage.data["lng"]
			val expiry = remoteMessage.data["expiry"]
			val foodEvent = FoodEvent(title, body, lat, lng, expiry, System.currentTimeMillis())
			NotificationShower.show(foodEvent, DataManager(sharedPref), applicationContext)
		}
	}
}
