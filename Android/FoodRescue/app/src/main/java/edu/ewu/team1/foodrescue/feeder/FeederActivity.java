package edu.ewu.team1.foodrescue.feeder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;

import edu.ewu.team1.foodrescue.R;
import edu.ewu.team1.foodrescue.eater.EaterActivity;

public class FeederActivity extends AppCompatActivity implements OnMapReadyCallback {
    //https://developers.google.com/maps/documentation/android-api/groundoverlay
    MapView mapView;
    GoogleMap map;
    String[] names;
    Double[] lats;
    Double[] lngs;
    Spinner spinner;
    boolean hasLocationAccess = false;
    //Request codes
    private int gpsPermissionsRequestCode = 456;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeder);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Call ActivityCompat#requestPermissions here to request the missing permissions,
            // and then overriding
            //     public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
            // to handle the case where the user grants the permission.
            // See the documentation for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, gpsPermissionsRequestCode);
        }

        //Dropdown menu
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

        //copy nearest building to top of list
        LatLng currLoc = getCurrentLocation();
        double smallest = Double.MAX_VALUE;
        int closest = 1;
        for (i = 2; i < names.length; i++) {
            double dist = Math.hypot(lats[i] - currLoc.latitude, lngs[i] - currLoc.longitude);
            if (dist < smallest){
                closest = i;
                smallest = dist;
            }
        }
        names[0] = names[closest];
        lats[0] = lats[closest];
        lngs[0] = lngs[closest];

        //Populate menu
        spinner = findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names));

        //Map
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        //TODO: Provide visual feedback to the user by displaying the lat/lng of the center of their crosshair
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        moveMapToLocation(getCurrentLocation());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == gpsPermissionsRequestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                hasLocationAccess = true;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private LatLng getCurrentLocation() {
        //https://stackoverflow.com/questions/20210565/android-location-manager-get-gps-location-if-no-gps-then-get-to-network-provid
        LatLng latLng;
        if (hasLocationAccess) {
            boolean gps_enabled;
            boolean network_enabled;

            LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Location net_loc = null, gps_loc = null, finalLoc = null;

            if (gps_enabled)
                gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (network_enabled)
                net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (gps_loc != null && net_loc != null) {

                //smaller the number more accurate result will
                if (gps_loc.getAccuracy() > net_loc.getAccuracy())
                    finalLoc = net_loc;
                else
                    finalLoc = gps_loc;

                // I used this just to get an idea (if both avail, its upto you which you want to take as I've taken location with more accuracy)

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

    private LatLng getCrosshairLocation() {
        return null;
    }


    @SuppressLint("MissingPermission")
    private void moveMapToLocation(LatLng latLng) {
        if (hasLocationAccess) map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.switcher, menu);
        menu.findItem(R.id.itemFeeder).setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.itemFeeder) {
            return true;
        } else if (id == R.id.itemEater) {
            startActivity(new Intent(this, EaterActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
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
