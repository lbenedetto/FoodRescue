package edu.ewu.team1.foodrescue;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.ewu.team1.foodrescue.fragments.EaterFragment;
import edu.ewu.team1.foodrescue.fragments.FeederFragment;
import edu.ewu.team1.foodrescue.fragments.SSOFragment;

public class MainActivity extends AppCompatActivity {
    private final Pattern pattern = Pattern.compile("<cas:user>(.*?)</cas:user>");
    private BottomNavigationView navigation;
    private boolean feederIsActive = false;

    /**
     * This is called when the user uses one of the two buttons on the bottom nav bar to switch to
     * a different view. It checks to make sure the user isn't trying to switch to the currently
     * active fragment, which would look really silly
     */
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = item -> {
        if (!getUsername().equals("NoUUID")) {
            switch (item.getItemId()) {
                case R.id.navigation_feeder:
                    if (!feederIsActive) {
                        setFragment(new FeederFragment(), R.anim.slide_in_left, R.anim.slide_out_right);
                        feederIsActive = true;
                    }
                    return true;
                case R.id.navigation_eater:
                    if (feederIsActive) {
                        setFragment(new EaterFragment(), R.anim.slide_in_right, R.anim.slide_out_left);
                        feederIsActive = false;
                    }
                    return true;
            }
        }
        return false;
    };

    /**
     * Switches the fragment in the fragment_container to the specified target by playing the
     * exit animation for the current fragment, and the entrace animation for the new fragment
     *
     * @param target            the fragment to switch to
     * @param entranceAnimation the entrance animation to use
     * @param exitAnimation     the exit animation to use
     */
    private void setFragment(Fragment target, int entranceAnimation, int exitAnimation) {
        FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
        ft2.setCustomAnimations(entranceAnimation, exitAnimation);
        ft2.replace(R.id.fragment_container, target, "fragment");
        ft2.addToBackStack(null);
        ft2.commit();
    }

    /**
     * Switch the fragment in the fragment_container to the specified target with no animation
     *
     * @param target the fragment to switch to
     */
    private void setFragment(Fragment target) {
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, target).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_main);

        //Populating the bottom navigation bar
        navigation = findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Authentication process

        String UUID = getUsername();
        if (UUID.equals("NoUUID")) {//If the user has not signed in before
            setFragment(new SSOFragment());
            selectMenuItem(R.id.navigation_sso);
            authenticateTicket();
        } else {
            Toast.makeText(this, "logged in as " + UUID, Toast.LENGTH_LONG).show();
            finalizeSignIn();
        }

        checkPlayServices();
    }

    /**
     * Gets the users current logged users username from Shared Preferences
     * If they have not yet logged in, return "NoUUID"
     *
     * @return String
     */
    private String getUsername() {
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String defaultValue = "NoUUID";
        return sharedPref.getString("UUID", defaultValue);
    }

    /**
     * If this app was started from the launcher icon, this method will do nothing
     * If this app was started from the callback URL, then it will extract the ticket from the URL
     * and contact the CAS server to get the username. The username is stored in shared preferences
     */
    private void authenticateTicket() {
        //Authentication code
        Intent intent = getIntent();
        String url = intent.getDataString();
        if (url != null) {
            //Extract ticket
            String ticket = url.substring(url.indexOf("ticket="), url.length());
            String validateURL = "https://authenticateTicket.ewu.edu/cas/serviceValidate?" + ticket + "&service=https://foodrescue.ewu.edu/login_redirect";

            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest getRequest = new StringRequest(Request.Method.GET, validateURL,
                    response -> {
                        Log.d("Response", response);
                        Matcher m = pattern.matcher(response);
                        if (m.find()) {
                            //TODO: (Easy) Use a custom hashing algorithm to hash the username
                            //The hashed username will be send along with requests to our server for validation
                            String username = m.group(1);
                            SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("UUID", username);
                            editor.apply();
                            Toast.makeText(this, "logged in as " + username, Toast.LENGTH_LONG).show();
                        } else {
                            Log.d("CAS Auth Failure", "Response did not contain error code");
                        }
                    },
                    error -> Log.d("Error.Response", error.toString())
            );
            queue.add(getRequest);

            finalizeSignIn();
        }
    }

    /**
     * Sets the fragment to the Eater Fragment, removes the sign in option from the bottom nav menu
     * and asks our backend for the usertype (feeder or eater)
     * Called whether or not CAS was actually contacted to sign in
     */
    private void finalizeSignIn() {
        setFragment(new EaterFragment());
        selectMenuItem(R.id.navigation_eater);

        navigation.getMenu().removeItem(R.id.navigation_sso);

        //TODO: request user type from Brad's backend (using Volley HTTP GET request)
        //Store the user type in shared preferences
        //When attempting to send a notification, check the stored user type.
        //If they are not a feeder, prompt them to send an email to the relevant authority to become an approved feeder
    }

    /**
     * Sets the specified item to be visually selected in the bottom nav bar
     *
     * @param itemID the ID to select
     */
    private void selectMenuItem(int itemID) {
        navigation.setSelectedItemId(itemID);
        navigation.getMenu().findItem(itemID).setChecked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    /**
     * Makes sure google player services is available, as our app will not work without it
     */
    private void checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 2404).show();
                apiAvailability.makeGooglePlayServicesAvailable(this);
            } else {
                Toast.makeText(this, "This device is not supported.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
