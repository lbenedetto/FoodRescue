package edu.ewu.team1.foodrescue.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Response
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import edu.ewu.team1.foodrescue.MainActivity
import edu.ewu.team1.foodrescue.R
import edu.ewu.team1.foodrescue.notifications.NotificationShower
import edu.ewu.team1.foodrescue.utilities.ConfirmDialog
import edu.ewu.team1.foodrescue.utilities.DataManager
import edu.ewu.team1.foodrescue.utilities.FoodEvent
import edu.ewu.team1.foodrescue.utilities.VolleyWrapper
import java.util.*

class FeederFragment : Fragment() {
	//https://developers.google.com/maps/documentation/android-api/groundoverlay
	private lateinit var mapView: MapView
	private var map: GoogleMap? = null
	private lateinit var names: Array<String>
	private lateinit var spinnerLocation: Spinner
	private lateinit var spinnerExpiry: Spinner
	private var hasLocationAccess = false
	private lateinit var location: TextView
	private lateinit var dataManager: DataManager
	private lateinit var editText: EditText

	private val crosshairLocation: LatLng?
		get() = map?.cameraPosition?.target

	companion object {
		const val gpsPermissionsRequestCode = 456
		const val CUSTOM_MESSAGE = "MESSAGE"
		const val SELECTED_LOCATION = "LOCATION"
		const val SELECTED_EXPIRY = "EXPIRY"
		const val LAT = "LAT"
		const val LNG = "LNG"
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		super.onCreateView(inflater, container, savedInstanceState)
		Log.e("onCreateView()", "called")
		retainInstance
		return inflater.inflate(R.layout.fragment_feeder, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		Log.e("onViewCreated()", "called")
		dataManager = (activity as MainActivity).dataManager
		location = view.findViewById(R.id.textViewLatLng)
		editText = view.findViewById(R.id.editTextMessage)
		loadMap(view, savedInstanceState)
		populateLocationMenu(view)
		populateExpiryMenu(view)
		defineSubmitButtonBehavior(view)
		defineShowButtonBehavior(view)
		//Restore instance state
		if (savedInstanceState != null) {
			val msg = savedInstanceState.getString(CUSTOM_MESSAGE)
			editText.setText(msg.toCharArray(), 0, msg.length)
			spinnerLocation.setSelection(savedInstanceState.getInt(SELECTED_LOCATION))
			spinnerExpiry.setSelection(savedInstanceState.getInt(SELECTED_EXPIRY))
		}
	}

	override fun onSaveInstanceState(outState: Bundle) {
		Log.e("onSaveInstanceState()", "called")
		super.onSaveInstanceState(outState)
		outState.putString(CUSTOM_MESSAGE, editText.text.toString())
		outState.putInt(SELECTED_LOCATION, spinnerLocation.selectedItemPosition)
		outState.putInt(SELECTED_EXPIRY, spinnerExpiry.selectedItemPosition)
		outState.putDouble(LAT, crosshairLocation?.latitude ?: 47.491355)
		outState.putDouble(LNG, crosshairLocation?.longitude?: -117.582798)
	}

	//<editor-fold desc="onCreate() Helper Methods Section">
	/**
	 * Populates the dropdown menu with all the buildings and their locations
	 * Closest stored location to the user is duplicated and put at the top of the list
	 */
	private fun populateLocationMenu(view: View) {
		//Make sure we have permission to get the users location
		//The users location will be used to order the dropdown list
		if (ActivityCompat.checkSelfPermission(activity!!,
						Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity!!,
						Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), gpsPermissionsRequestCode)
		}

		//Populating the arrays we will use
		names = resources.getStringArray(R.array.locationNames)
		val lngs = resources.getStringArray(R.array.locationLats).iterator()
		val lats = resources.getStringArray(R.array.locationLngs).iterator()
		val latitudes = Array(names.size, {
			lngs.next().toDouble()
		})
		val longitudes = Array(names.size, {
			lats.next().toDouble()
		})

		//Copy the nearest stored location to the user to the top of the list
		val currLoc = currentLocation
		var smallest = java.lang.Double.MAX_VALUE
		var closest = 1
		var i = 2
		while (i < names.size) {
			val dist = Math.hypot(latitudes[i] - currLoc.latitude, longitudes[i] - currLoc.longitude)
			if (dist < smallest) {
				closest = i
				smallest = dist
			}
			i++
		}
		names[0] = names[closest]
		latitudes[0] = latitudes[closest]
		longitudes[0] = longitudes[closest]

		//Populate menu
		spinnerLocation = view.findViewById(R.id.spinnerLocation)
		spinnerLocation.adapter = ArrayAdapter(activity!!, android.R.layout.simple_list_item_1, names)
		spinnerLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
			override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
				Log.e("onItemSelected()", "called")
				//Position 1 is "Other"
				if (position != 1)
					moveMapToLocation(LatLng(latitudes[position], longitudes[position]))
			}

			override fun onNothingSelected(parentView: AdapterView<*>) {}
		}
	}

	private fun populateExpiryMenu(view: View) {
		spinnerExpiry = view.findViewById<Spinner>(R.id.spinnerExpiry)
		spinnerExpiry.adapter = ArrayAdapter(activity!!, android.R.layout.simple_list_item_1, arrayOf("<15 minutes", "<30 minutes", "<1 hour"))
		spinnerExpiry.setSelection(0)
	}

	private fun defineSubmitButtonBehavior(view: View) {
		//Define behavior of "Send Announcement" button
		view.findViewById<Button>(R.id.buttonSubmit).setOnClickListener {
			ConfirmDialog.confirmAction(Runnable {
				val editTextMessage = view.findViewById<EditText>(R.id.editTextMessage)
				val loc = crosshairLocation
				val locName = names[spinnerLocation.selectedItemPosition]
				val message = editTextMessage.text.toString()
				val duration = when (view.findViewById<Spinner>(R.id.spinnerExpiry).selectedItemPosition) {
					0 -> 15
					1 -> 30
					else -> 60
				}
				val url = MainActivity.SERVER_IP + MainActivity.SEND_NOTIFICATION

				val params = HashMap<String, String>()
				val token = dataManager.getToken()
				params["title"] = locName
				params["body"] = message
				params["lat"] = loc!!.latitude.toString()
				params["lng"] = loc.longitude.toString()
				params["expiry"] = duration.toString()

				VolleyWrapper.post(view.context, url, params, Response.Listener { response ->
					Log.e("post Response:", response)
					//TODO: Check if the server allowed the notification to be sent
					//If it didn't, the user should be notified of how to become an authorized feeder
					Toast.makeText(view.context, "Notification sent!", Toast.LENGTH_LONG).show()
				})
				editTextMessage.text.clear()
			}, R.string.confirm_send, context!!)

		}
	}

	private fun defineShowButtonBehavior(view: View) {
		view.findViewById<Button>(R.id.buttonShowLocal).setOnClickListener {
			ConfirmDialog.confirmAction(Runnable {
				val loc = crosshairLocation
				val locName = names[spinnerLocation.selectedItemPosition]
				val message = view.findViewById<EditText>(R.id.editTextMessage).text.toString()
				val duration = when (view.findViewById<Spinner>(R.id.spinnerExpiry).selectedItemPosition) {
					0 -> 15
					1 -> 30
					else -> 60
				}
				NotificationShower.show(FoodEvent(locName, message, loc?.latitude, loc?.longitude, duration, System.currentTimeMillis()), dataManager, view.context)
			}, R.string.confirm_send, context!!)
		}
	}

	private fun loadMap(view: View, savedInstanceState: Bundle?) {
		mapView = view.findViewById(R.id.mapView)
		mapView.onCreate(savedInstanceState)
		mapView.getMapAsync({
			map = it
			map!!.setOnCameraMoveListener({ this.displayCurrentLocation() })

			if (savedInstanceState != null) {
				moveMapToLocation(LatLng(
						savedInstanceState.getDouble(LAT),
						savedInstanceState.getDouble(LNG)
				))
			} else {
				moveMapToLocation(currentLocation)
			}
			displayCurrentLocation()
		})
	}
	//</editor-fold>

	//<editor-fold desc="Map Helper Methods Section">
	/**
	 * Gets the users current location as accurately as possible
	 * https://stackoverflow.com/questions/20210565/android-location-manager-get-gps-location-if-no-gps-then-get-to-network-provid
	 * @return LatLng
	 */
	private val currentLocation: LatLng
		@SuppressLint("MissingPermission")
		get() {
			var latLng = LatLng(47.491376, -117.582917)
			if (hasLocationAccess) {
				val lm = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
				val isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
				val isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

				var gpsLocation: Location? = null
				var networkLocation: Location? = null

				if (isGpsEnabled)
					gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
				if (isNetworkEnabled)
					networkLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

				if (gpsLocation != null && networkLocation != null) {
					//smaller the number more accurate result will be
					val finalLoc =
							if (gpsLocation.accuracy > networkLocation.accuracy)
								networkLocation
							else
								gpsLocation
					latLng = LatLng(finalLoc.latitude, finalLoc.longitude)
				}
			}

			return latLng
		}

	/**
	 * Displays the LatLng of the center of the crosshairs in a text view
	 */
	private fun displayCurrentLocation() {
		val loc = crosshairLocation
		location.text = loc.toString()
	}

	/**
	 * Centers the map on the specified location and resets the zoom level
	 *
	 * @param latLng The location to center the map on
	 */
	@SuppressLint("MissingPermission")
	private fun moveMapToLocation(latLng: LatLng) {
		if(map != null) {
			if (hasLocationAccess) map!!.isMyLocationEnabled = true
			map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
		}
	}
	//</editor-fold>

	//<editor-fold desc="Map Callbacks">
	/**
	 * When the map is loaded we configure some settings for it
	 *
	 * @param googleMap the map which is now ready to be configured and used
	 */

	/**
	 * Callback for requesting permissions
	 */
	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		if (requestCode == gpsPermissionsRequestCode) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
				hasLocationAccess = true
			}
		}
	}

	override fun onResume() {
		mapView.onResume()
		super.onResume()
	}


	override fun onPause() {
		super.onPause()
		mapView.onPause()
	}

	override fun onDestroy() {
		super.onDestroy()
		mapView.onDestroy()
	}

	override fun onLowMemory() {
		super.onLowMemory()
		mapView.onLowMemory()
	}
	//</editor-fold>
}
