package edu.ewu.team1.foodrescue.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.DataSetObserver
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ListAdapter
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import edu.ewu.team1.foodrescue.R
import java.text.SimpleDateFormat
import java.util.*

class FoodEventAdapter(
		private val events: ArrayList<FoodEvent>,
		private val context: Context,
		private val mInflater: LayoutInflater,
		private val dataManager: DataManager) : ListAdapter {

	private val observers: ArrayList<DataSetObserver> = ArrayList()
	companion object {
		@SuppressLint("SimpleDateFormat")
		val sdf  = SimpleDateFormat("hh:mm a")
	}
	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
		//Inflate the view if it isn't already

		val view = convertView ?: mInflater.inflate(R.layout.notification_item, parent, false)

		val event = events[position]
		(view!!.findViewById<View>(R.id.textViewTitle) as TextView).text = event.title
		(view.findViewById<View>(R.id.textViewBody) as TextView).text = event.body
		(view.findViewById<View>(R.id.textViewTime) as TextView).text = sdf.format(Date(event.timestamp))
		(view.findViewById<View>(R.id.textViewAvailability) as TextView).text = String.format(
				view.resources.getString(R.string.availability),
				event.duration,
				if(event.duration == 1) "hours" else "minutes"
		)


		val loc = LatLng(event.lat, event.lng)
		//Map
		val mapView = view.findViewById<MapView>(R.id.mapViewEater)
		mapView.onCreate(Bundle())
		mapView.getMapAsync { googleMap ->
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 17.0f))
			val marker = googleMap.addMarker(MarkerOptions().position(loc).title(event.title))
			marker.showInfoWindow()
			mapView.onResume()
		}

		view.findViewById<View>(R.id.buttonClear).setOnClickListener {
			val toRemove = events.removeAt(position)
			dataManager.removeFoodEvent(toRemove)
			for (o in observers) {
				o.onChanged()
			}
		}

		view.findViewById<View>(R.id.buttonDirections).setOnClickListener {
			val intent = Intent(android.content.Intent.ACTION_VIEW,
					Uri.parse("http://maps.google.com/maps?daddr=" + event.lat + "," + event.lng))
			context.startActivity(intent)
		}
		return view
	}

	//region Useless garbage
	override fun areAllItemsEnabled(): Boolean {
		return true
	}

	override fun isEnabled(position: Int): Boolean {
		return true
	}

	override fun registerDataSetObserver(observer: DataSetObserver) {
		observers.add(observer)
	}

	override fun unregisterDataSetObserver(observer: DataSetObserver) {
		observers.remove(observer)
	}

	override fun getCount(): Int {
		return events.size
	}

	override fun getItem(position: Int): Any {
		return events[position]
	}

	override fun getItemId(position: Int): Long {
		return events[position].timestamp
	}

	override fun hasStableIds(): Boolean {
		return true
	}

	override fun getItemViewType(position: Int): Int {
		return Adapter.IGNORE_ITEM_VIEW_TYPE
	}

	override fun getViewTypeCount(): Int {
		return 1
	}

	override fun isEmpty(): Boolean {
		return false
	}
	//endregion
}
