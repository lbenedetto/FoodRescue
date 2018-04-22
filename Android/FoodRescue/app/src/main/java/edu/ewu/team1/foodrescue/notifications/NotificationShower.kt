package edu.ewu.team1.foodrescue.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat
import edu.ewu.team1.foodrescue.utilities.DataManager
import edu.ewu.team1.foodrescue.utilities.FoodEvent
import edu.ewu.team1.foodrescue.MainActivity
import edu.ewu.team1.foodrescue.R


class NotificationShower {
	companion object {
		/**
		 * Shows a notification with the specified message
		 *
		 * @param foodEvent the event
		 */
		fun show(foodEvent: FoodEvent, dataManager: DataManager, context: Context) {

			if (dataManager.areNotificationsEnabled()) {
				dataManager.addFoodEvent(foodEvent)
				val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

				val notification = configureNotificationSettings(notificationManager, context, foodEvent)

				val id = dataManager.getNextNotificationID()
				notificationManager.notify(id, notification)

				scheduleNotificationClear(context, id, foodEvent.duration)

			}

		}

		private fun configureNotificationSettings(notificationManager: NotificationManager, context: Context, foodEvent: FoodEvent): Notification {
			val notificationIntent = Intent(context, MainActivity::class.java)
			notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
			val pi = PendingIntent.getActivity(context, 0,
					notificationIntent, 0)

			//Android O specific settings
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				val notificationChannel = NotificationChannel(FoodNotificationService.NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH)
				// Configure the notification channel.
				notificationChannel.description = "Notifications about the availability of free food on campus"
				notificationChannel.enableLights(true)
				notificationChannel.lightColor = Color.RED
				notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
				notificationChannel.enableVibration(true)
				notificationManager.createNotificationChannel(notificationChannel)
			}

			val notificationBuilder = NotificationCompat.Builder(context, FoodNotificationService.NOTIFICATION_CHANNEL_ID)

			notificationBuilder.setAutoCancel(true)
					.setDefaults(Notification.DEFAULT_ALL)
					.setWhen(System.currentTimeMillis())
					.setSmallIcon(R.drawable.burger_bell)
					.setAutoCancel(true)
					.setContentIntent(pi)
					.setPriority(Notification.PRIORITY_MAX)
					.setContentTitle("Free food at ${foodEvent.title}")
					.setContentText(foodEvent.body)

			val notification = notificationBuilder.build()

			notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
			return notification
		}

		private fun scheduleNotificationClear(context: Context, id: Int, duration: Int) {
			//https://stackoverflow.com/questions/23874203/create-an-android-notification-with-expiration-date
			val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
			val intent = Intent(context, NotificationClearer::class.java)
			intent.action = NotificationClearer.packageName
			intent.putExtra("notification_id", id)
			val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
			alarmManager.set(
					AlarmManager.RTC_WAKEUP,
					System.currentTimeMillis() + (duration * 60000),
					pendingIntent)
		}
	}
}