package edu.ewu.team1.foodrescue.notifications

import android.content.BroadcastReceiver
import android.app.NotificationManager
import android.content.Context
import android.content.Intent

class NotificationClearer : BroadcastReceiver() {
	companion object {
		const val packageName = "edu.ewu.team1.foodrescue.action.CANCEL_NOTIFICATION"
	}
	override fun onReceive(context: Context, intent: Intent) {
		val action = intent.action
		if (packageName == action) {
			val id = intent.getIntExtra("notification_id", -1)
			if (id != -1) {
				val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
				notificationManager.cancel(id)
			}
		}
	}
}