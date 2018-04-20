package edu.ewu.team1.foodrescue

import android.content.Context
import android.content.Intent
import android.database.DataSetObserver
import android.net.Uri
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
import java.util.*

class FoodEventAdapter(private val events: ArrayList<FoodEvent>, private val context: Context) : ListAdapter {
	private val observers: ArrayList<DataSetObserver> = ArrayList()

	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
		//Inflate the view if it isn't already
		val view =
				if (convertView == null) {
					val inflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
					inflater.inflate(R.layout.notification_item, parent)
				} else {
					convertView
				}

		val event = events[position]
		(view!!.findViewById<View>(R.id.textViewTitle) as TextView).text = event.title
		(view.findViewById<View>(R.id.textViewBody) as TextView).text = event.body
		val loc = LatLng(event.lat, event.lng)
		//Map
		val mapView = view.findViewById<MapView>(R.id.mapViewEater)
		mapView.getMapAsync { googleMap ->
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 17.0f))
			googleMap.addMarker(MarkerOptions().position(loc).title(event.title))
		}

		view.findViewById<View>(R.id.buttonClear).setOnClickListener {
			events.removeAt(position)
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
