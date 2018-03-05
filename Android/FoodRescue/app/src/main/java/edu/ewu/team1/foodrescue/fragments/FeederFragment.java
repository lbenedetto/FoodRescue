package edu.ewu.team1.foodrescue.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import edu.ewu.team1.foodrescue.R;
import edu.ewu.team1.foodrescue.cryptography.DiffieHelmanEncryptedMessage;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeederFragment extends Fragment implements OnMapReadyCallback {
    //https://developers.google.com/maps/documentation/android-api/groundoverlay
    private MapView mapView;
    private GoogleMap map;
    private String[] names;
    private Double[] lats;
    private Double[] lngs;
    private Spinner spinner;
    private boolean hasLocationAccess = false;
    private final int gpsPermissionsRequestCode = 456;
    private View view;
    private EditText editText;
    private TextView location;

    public FeederFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feeder, container, false);

        location = view.findViewById(R.id.textViewLatLng);
        populateDropdownMenu();

        //Map
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        //Define behavior of "Send Announcement" button
        editText = view.findViewById(R.id.editTextMessage);
        Button buttonSubmit = view.findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(view -> {
            LatLng loc = getCrosshairLocation();
            assert loc != null;
            String locName = names[spinner.getSelectedItemPosition()];
            String message = editText.getText().toString();

            RequestQueue queue = Volley.newRequestQueue(getContext());
            String url = "bradstephensoncode.org/FoodRescue/sendrest.php";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    response -> Log.d("Response", response),
                    error -> Log.d("Error.Response", error.getMessage())
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    //TODO: Include hashed username here
                    params.put("lat", String.valueOf(loc.latitude));
                    params.put("lng", String.valueOf(loc.longitude));
                    params.put("locName", locName);
                    params.put("message", message);

                    return params;
                }
            };
            queue.add(postRequest);

        });

        return view;
    }

    /**
     * When the map is loaded we configure some settings for it
     *
     * @param googleMap the map which is now ready to be configured and used
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        moveMapToLocation(getCurrentLocation());
        displayCurrentLocation();
        map.setOnCameraMoveListener(this::displayCurrentLocation);
    }

    /**
     * Displays the LatLng of the center of the crosshairs in a text view
     */
    private void displayCurrentLocation() {
        LatLng loc = getCrosshairLocation();
        location.setText(loc.toString());
    }

    /**
     * Callback for requesting permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == gpsPermissionsRequestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                hasLocationAccess = true;
            }
        }
    }

    /**
     * Gets the users current location as accurately as possible
     *
     * @return LatLng
     */
    @SuppressLint("MissingPermission")
    private LatLng getCurrentLocation() {
        //https://stackoverflow.com/questions/20210565/android-location-manager-get-gps-location-if-no-gps-then-get-to-network-provid
        LatLng latLng;
        if (hasLocationAccess) {
            boolean gps_enabled;
            boolean network_enabled;

            LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            assert lm != null;
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Location net_loc = null, gps_loc = null, finalLoc = null;

            if (gps_enabled)
                gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (network_enabled)
                net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (gps_loc != null && net_loc != null) {

                //smaller the number more accurate result will be
                if (gps_loc.getAccuracy() > net_loc.getAccuracy())
                    finalLoc = net_loc;
                else
                    finalLoc = gps_loc;

            } else {

                if (gps_loc != null) {
                    finalLoc = gps_loc;
                } else if (net_loc != null) {
                    finalLoc = net_loc;
                }
            }
            if (finalLoc == null) {
                latLng = new LatLng(47.491376, -117.582917);
            } else {
                latLng = new LatLng(finalLoc.getLatitude(), finalLoc.getLongitude());
            }
        }
        //TODO: Comment this out. It is for testing purposes
        //This way, the location will be centered on campus instead of your house
        latLng = new LatLng(47.491376, -117.582917);

        return latLng;
    }

    /**
     * Gets the LatLng of the center of the map
     *
     * @return LatLng
     */
    private LatLng getCrosshairLocation() {
        return map.getCameraPosition().target;
    }


    /**
     * Centers the map on the specified location and resets the zoom level
     *
     * @param latLng The location to center the map on
     */
    @SuppressLint("MissingPermission")
    private void moveMapToLocation(LatLng latLng) {
        if (hasLocationAccess) map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
    }

    /**
     * Populates the dropdown menu with all the buildings and their locations
     * Closest stored location to the user is duplicated and put at the top of the list
     */
    private void populateDropdownMenu() {
        //Make sure we have permission to get the users location
        //The users location will be used to order the dropdown list
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, gpsPermissionsRequestCode);
        }

        //Populating the arrays we will use
        names = getResources().getStringArray(R.array.locationNames);
        lats = new Double[names.length + 1];
        lngs = new Double[names.length + 1];

        int i = 1;
        for (String c : getResources().getStringArray(R.array.locationLats)) {
            lats[i++] = Double.parseDouble(c);
        }
        i = 1;
        for (String c : getResources().getStringArray(R.array.locationLngs)) {
            lngs[i++] = Double.parseDouble(c);
        }

        //Copy the nearest stored location to the user to the top of the list
        LatLng currLoc = getCurrentLocation();
        double smallest = Double.MAX_VALUE;
        int closest = 1;
        for (i = 2; i < names.length; i++) {
            double dist = Math.hypot(lats[i] - currLoc.latitude, lngs[i] - currLoc.longitude);
            if (dist < smallest) {
                closest = i;
                smallest = dist;
            }
        }
        names[0] = names[closest];
        lats[0] = lats[closest];
        lngs[0] = lngs[closest];

        //Populate menu
        spinner = view.findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, names));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if (position != 1)//Position 1 is "Other"
                    moveMapToLocation(new LatLng(lats[position], lngs[position]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
