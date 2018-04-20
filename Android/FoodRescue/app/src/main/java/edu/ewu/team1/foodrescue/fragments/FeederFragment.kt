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
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import edu.ewu.team1.foodrescue.MainActivity
import edu.ewu.team1.foodrescue.R
import edu.ewu.team1.foodrescue.VolleyWrapper
import kotlinx.android.synthetic.main.fragment_feeder.*
import java.util.*

class FeederFragment : Fragment(), OnMapReadyCallback {
	//https://developers.google.com/maps/documentation/android-api/groundoverlay
	private lateinit var mapView: MapView
	private lateinit var map: GoogleMap
	private lateinit var names: Array<String>
	private lateinit var lats: Array<Double>
	private lateinit var lngs: Array<Double>
	private lateinit var spinner: Spinner
	private var hasLocationAccess = false
	//    private lateinit var view: View
	private lateinit var editText: EditText
	private lateinit var location: TextView

	companion object {
		const val gpsPermissionsRequestCode = 456
	}

	/**
	 * Gets the users current location as accurately as possible
	 *
	 * @return LatLng
	 */
	private//https://stackoverflow.com/questions/20210565/android-location-manager-get-gps-location-if-no-gps-then-get-to-network-provid
	//smaller the number more accurate result will be
	val currentLocation: LatLng
		@SuppressLint("MissingPermission")
		get() {
			var latLng = LatLng(47.491376, -117.582917)
			if (hasLocationAccess) {
				val isGpsEnabled: Boolean
				val isNetworkEnabled: Boolean

				val lm = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

				isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
				isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

				lateinit var networkLocation: Location
				lateinit var gpsLocation: Location
				lateinit var finalLoc: Location

				if (isGpsEnabled)
					gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
				if (isNetworkEnabled)
					networkLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

				finalLoc =
						if (gpsLocation.accuracy > networkLocation.accuracy)
							networkLocation
						else
							gpsLocation

				latLng = LatLng(finalLoc.latitude, finalLoc.longitude)
			}

			return latLng
		}

	/**
	 * Gets the LatLng of the center of the map
	 *
	 * @return LatLng
	 */
	private val crosshairLocation: LatLng
		get() = map.cameraPosition.target

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_feeder, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		location = view.findViewById(R.id.textViewLatLng)
		populateDropdownMenu()
		editTextMessage
		//Map
		mapView = view.findViewById(R.id.mapView)
		mapView.onCreate(savedInstanceState)
		mapView.getMapAsync(this)

		//Define behavior of "Send Announcement" button
		editText = view.findViewById(R.id.editTextMessage)
		buttonSubmit.setOnClickListener {
			val loc = crosshairLocation
			val locName = names[spinner.selectedItemPosition]
			val message = editText.text.toString()

			val url = MainActivity.SERVER_IP + MainActivity.SEND_NOTIFICATION

			val params = HashMap<String, String>()
			val token = view.context
					.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
					.getString(MainActivity.TOKEN_KEY, MainActivity.NO_TOKEN)
			params["title"] = locName
			params["body"] = message
			params["data"] = loc.latitude.toString() + "," + loc.longitude.toString()
			params["auth"] = token

			VolleyWrapper.post(view.context, url, params, Response.Listener { response ->
				if (response.isEmpty()) Log.e("post Response:", "No response")
				//TODO: Check if the server allowed the notification to be sent
				//If it didn't, the user should be notified of how to become an authorized feeder
			})
		}
	}

	/**
	 * When the map is loaded we configure some settings for it
	 *
	 * @param googleMap the map which is now ready to be configured and used
	 */
	override fun onMapReady(googleMap: GoogleMap) {
		map = googleMap
		moveMapToLocation(currentLocation)
		displayCurrentLocation()
		map.setOnCameraMoveListener({ this.displayCurrentLocation() })
	}

	/**
	 * Displays the LatLng of the center of the crosshairs in a text view
	 */
	private fun displayCurrentLocation() {
		val loc = crosshairLocation
		location.text = loc.toString()
	}

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


	/**
	 * Centers the map on the specified location and resets the zoom level
	 *
	 * @param latLng The location to center the map on
	 */
	@SuppressLint("MissingPermission")
	private fun moveMapToLocation(latLng: LatLng) {
		if (hasLocationAccess) map.isMyLocationEnabled = true
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f))
	}

	/**
	 * Populates the dropdown menu with all the buildings and their locations
	 * Closest stored location to the user is duplicated and put at the top of the list
	 */
	private fun populateDropdownMenu() {
		//Make sure we have permission to get the users location
		//The users location will be used to order the dropdown list
		if (ActivityCompat.checkSelfPermission(activity!!,
						Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity!!,
						Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), gpsPermissionsRequestCode)
		}

		//Populating the arrays we will use
		names = resources.getStringArray(R.array.locationNames)
		val longitudes = resources.getStringArray(R.array.locationLats).iterator()
		val latitudes = resources.getStringArray(R.array.locationLngs).iterator()
		lats = Array(names.size + 1, { ix ->
			if (ix == 0)
				0.0//Overwritten later
			else
				longitudes.next().toDouble()
		})
		lngs = Array(names.size + 1, { ix ->
			if (ix == 0)
				0.0//Overwritten later
			else
				latitudes.next().toDouble()
		})

		//Copy the nearest stored location to the user to the top of the list
		val currLoc = currentLocation
		var smallest = java.lang.Double.MAX_VALUE
		var closest = 1
		var i = 2
		while (i < names.size) {
			val dist = Math.hypot(lats[i] - currLoc.latitude, lngs[i] - currLoc.longitude)
			if (dist < smallest) {
				closest = i
				smallest = dist
			}
			i++
		}
		names[0] = names[closest]
		lats[0] = lats[closest]
		lngs[0] = lngs[closest]

		//Populate menu
		spinner = view!!.findViewById(R.id.spinner)
		spinner.adapter = ArrayAdapter(activity!!, android.R.layout.simple_list_item_1, names)
		spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
			override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
				//Position 1 is "Other"
				if (position != 1)
					moveMapToLocation(LatLng(lats[position], lngs[position]))
			}

			override fun onNothingSelected(parentView: AdapterView<*>) {
				// nothing
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
}// Required empty public constructor
