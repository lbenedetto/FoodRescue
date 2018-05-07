package edu.ewu.team1.foodrescue.utilities

import android.content.SharedPreferences

class DataManager(private val sharedPrefs: SharedPreferences) {
	companion object {
		const val FOOD_EVENTS_KEY = "food_events"
		const val NOTIFICATION_ID_KEY = "notification_id"
		const val USERNAME_KEY = "username"
		const val TOKEN_KEY = "token"
		const val NOTIFICATIONS_ENABLED_KEY = "notifications_enabled"
		const val NO_USERNAME = "NoUsername"
		const val NO_TOKEN = "NoToken"
	}

	fun addFoodEvent(foodEvent: FoodEvent) {
		val events = getAllFoodEvents()
		events.add(foodEvent.toString())
		saveAllFoodEvents(events)
	}

	fun removeFoodEvent(foodEvent: FoodEvent) {
		val events = HashSet<String>(getAllFoodEvents())
		events.remove(foodEvent.toString())
		saveAllFoodEvents(events)
	}

	private fun saveAllFoodEvents(events: Set<String>) {
		val editor = sharedPrefs.edit()
		editor.putStringSet(FOOD_EVENTS_KEY, events)
		editor.apply()
	}

	fun getAllFoodEvents(): MutableSet<String> {
		return sharedPrefs.getStringSet(FOOD_EVENTS_KEY, HashSet<String>())
	}

	fun clearAllFoodEvents() {
		saveAllFoodEvents(HashSet())
	}

	fun getNextNotificationID(): Int {
		val id = sharedPrefs.getInt(NOTIFICATION_ID_KEY, Math.random().toInt())
		val editor = sharedPrefs.edit()
		editor.putInt(NOTIFICATION_ID_KEY, id + 1)
		editor.apply()
		return id
	}

	fun getUsername(): String {
		return sharedPrefs.getString(USERNAME_KEY, NO_USERNAME)
	}

	fun getToken(): String {
		return sharedPrefs.getString(USERNAME_KEY, NO_USERNAME)
	}

	fun saveUsernameAndToken(username: String, token: String) {
		val editor = sharedPrefs.edit()
		editor.putString(USERNAME_KEY, username)
		editor.putString(TOKEN_KEY, token)
		editor.apply()
	}

	fun areNotificationsEnabled(): Boolean {
		return sharedPrefs.getBoolean(NOTIFICATIONS_ENABLED_KEY, false)
	}

	fun setNotificationsEnabledState(state: Boolean) {
		val editor = sharedPrefs.edit()
		editor.putBoolean(NOTIFICATIONS_ENABLED_KEY, state)
		editor.apply()
	}

	fun clearAll() {
		val editor = sharedPrefs.edit()
		editor.remove(USERNAME_KEY)
		editor.remove(TOKEN_KEY)
		editor.remove(NOTIFICATIONS_ENABLED_KEY)
		editor.remove(FOOD_EVENTS_KEY)
		editor.apply()
	}

}