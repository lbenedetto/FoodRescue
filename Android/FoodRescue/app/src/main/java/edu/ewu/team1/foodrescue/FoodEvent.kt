package edu.ewu.team1.foodrescue

class FoodEvent : Comparable<FoodEvent> {
	internal var title: String
	internal var body: String
	var lat: Double
	var lng: Double
	var duration: Int
	internal var timestamp: Long = 0

	constructor(title: String?, body: String?, data: String?, timestamp: Long) {
		this.title = title ?: ""
		this.body = body ?: ""
		val d = data?.split(",")?.toTypedArray() ?: arrayOf("47.491355", "-117.582798", "15")
		this.lat = d[0].toDouble()
		this.lng = d[1].toDouble()
		this.duration = d[2].toInt()
		this.timestamp = timestamp
	}

	constructor(parseMe: String) {
		val data = parseMe.split(",").toTypedArray()
		title = data[0]
		body = data[1]
		lat = data[2].toDouble()
		lng = data[3].toDouble()
		duration = data[4].toInt()
		timestamp = java.lang.Long.parseLong(data[5])
	}

	override fun toString(): String {
		return "$title,$body,$lat,$lng,$duration,$timestamp\n"
	}

	override fun compareTo(other: FoodEvent): Int {
		if (this.timestamp - other.timestamp == 0L) return 0
		return if (this.timestamp - other.timestamp < 0) -1 else 1
	}
}
