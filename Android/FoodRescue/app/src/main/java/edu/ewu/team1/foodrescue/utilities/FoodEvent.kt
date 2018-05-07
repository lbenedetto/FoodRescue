package edu.ewu.team1.foodrescue.utilities

import android.util.Base64
import java.io.*


class FoodEvent : Comparable<FoodEvent>, Serializable {
	internal var title: String
	internal var body: String
	var lat: Double
	var lng: Double
	var duration: Int
	internal var timestamp: Long = 0

	constructor(title: String?, body: String?, lat: String?, lng: String?, expiry: String?, timestamp: Long) {
		this.title = title ?: ""
		this.body = body ?: ""
		this.lat = (lat ?: "47.491355").toDouble()
		this.lng = (lng ?: "-117.582798").toDouble()
		this.duration = (expiry ?: "15").toInt()
		this.timestamp = timestamp
	}

	constructor(title: String?, body: String?, lat: Double?, lng: Double?, expiry: Int?, timestamp: Long) {
		this.title = title ?: ""
		this.body = body ?: ""
		this.lat = lat ?: 47.491355
		this.lng = lng ?: -117.582798
		this.duration = expiry ?: 15
		this.timestamp = timestamp
	}

	companion object {
		fun getFoodEvent(serialized: String): FoodEvent {
			val data = Base64.decode(serialized, Base64.DEFAULT)
			val ois = ObjectInputStream(
					ByteArrayInputStream(data))
			val o = ois.readObject()
			ois.close()
			return o as FoodEvent
		}
	}

	override fun toString(): String {
		val baos = ByteArrayOutputStream()
		val oos = ObjectOutputStream(baos)
		oos.writeObject(this)
		oos.close()
		return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT).trim()
	}

	override fun compareTo(other: FoodEvent): Int {
		if (this.timestamp - other.timestamp == 0L) return 0
		return if (this.timestamp - other.timestamp < 0) -1 else 1
	}

	override fun equals(other: Any?): Boolean {
		return other is FoodEvent && timestamp == other.timestamp
	}

	override fun hashCode(): Int {
		return timestamp.hashCode()
	}
}
