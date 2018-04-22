package edu.ewu.team1.foodrescue.notifications

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.messaging.FirebaseMessaging

class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {

	/**
	 * Refresh this devices firebase token and subscribe to the topic
	 */
	override fun onTokenRefresh() {
		// Get updated InstanceID token.
		val refreshedToken = FirebaseInstanceId.getInstance().token
		Log.d(TAG, "Token: " + refreshedToken!!)
		FirebaseMessaging.getInstance().subscribeToTopic("edu.ewu.team1.foodrescue")
	}
}
