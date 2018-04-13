package edu.ewu.team1.foodrescue;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.HashMap;
import java.util.Map;

import edu.ewu.team1.foodrescue.fragments.EaterFragment;
import edu.ewu.team1.foodrescue.fragments.FeederFragment;
import edu.ewu.team1.foodrescue.fragments.SSOFragment;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavView;
    private boolean feederIsActive = false;
    private SharedPreferences sharedPref;
    public static final String USERNAME_KEY = "username";
    public static final String NO_USERNAME = "NoUsername";
    public static final String TOKEN_KEY = "token";
    public static final String NO_TOKEN = "NoToken";

    public static final String SERVER_IP = "146.187.135.29";
    public static final String CAS = "https://login.ewu.edu/cas/login?service=";
    public static final String AUTH_PAGE = "https://" + SERVER_IP + "/android/login";
    //    public static final String TOKEN_INVALIDATE = "/FoodRescue/invalidateToken.php";
    public static final String SEND_NOTIFICATION = "/sender.php";//TODO: Ask brad where this is
    /**
     * This is called when the user uses one of the two buttons on the bottom nav bar to switch to
     * a different view. It checks to make sure the user isn't trying to switch to the currently
     * active fragment, which would look really silly
     */
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = item -> {
        if (!getUsername().equals(NO_USERNAME)) {
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
     * exit animation for the current fragment, and the entrance animation for the new fragment
     *
     * @param target            the fragment to switch to
     * @param entranceAnimation the entrance animation to use
     * @param exitAnimation     the exit animation to use
     */
    private void setFragment(Fragment target, int entranceAnimation, int exitAnimation) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(entranceAnimation, exitAnimation)
                .replace(R.id.fragment_container, target, "fragment")
                //.addToBackStack(null)
                .commit();
    }

    /**
     * Switch the fragment in the fragment_container to the specified target with no animation
     *
     * @param target the fragment to switch to
     */
    private void setFragment(Fragment target) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, target, "fragment")
                //.addToBackStack(null)
                .commit();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Log.d("focus", "touchevent");
                v.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_main);
        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        //Populating the bottom navigation bar
        bottomNavView = findViewById(R.id.bottomNavigationView);
        bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Authentication process
        String username = getUsername();
        if (username.equals(NO_USERNAME)) {//If the user has not signed in before
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String token = extras.getString("token");
                username = extras.getString("uid");

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(USERNAME_KEY, username);
                editor.putString(TOKEN_KEY, token);
                editor.apply();
                Toast.makeText(this, "logged in as " + username, Toast.LENGTH_LONG).show();
                finalizeSignIn();
            } else {
                setFragment(new SSOFragment());
                bottomNavView.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(this, "logged in as " + username, Toast.LENGTH_LONG).show();
            finalizeSignIn();
        }

        checkPlayServices();
    }

    /**
     * Gets the users current logged users username from Shared Preferences
     * If they have not yet logged in, return "NoUsername"
     *
     * @return String
     */
    private String getUsername() {
        return sharedPref.getString(USERNAME_KEY, NO_USERNAME);
    }

    /**
     * Gets the users current logged users username from Shared Preferences
     * If they have not yet logged in, return "NoToken"
     *
     * @return String
     */
    private String getAuthToken() {
        return sharedPref.getString(TOKEN_KEY, NO_TOKEN);
    }

    /**
     * Sets the fragment to the Eater Fragment, removes the sign in option from the bottom nav menu
     * Called whether or not CAS was actually contacted to sign in (ie, if user was already signed in)
     */
    private void finalizeSignIn() {
        setFragment(new EaterFragment());
        selectMenuItem(R.id.navigation_eater);
    }

    /**
     * Sets the specified item to be visually selected in the bottom nav bar
     *
     * @param itemID the ID to select
     */
    private void selectMenuItem(int itemID) {
        bottomNavView.setVisibility(View.VISIBLE);
        bottomNavView.setSelectedItemId(itemID);
        bottomNavView.getMenu().findItem(itemID).setChecked(true);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_about:
                //TODO: Put something here, maybe
                return true;
            case R.id.action_logout:
                logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        String token = getAuthToken();
        if (!token.equals(NO_TOKEN)) {
            //Invalidate the exist auth token
//            String url = MainActivity.SERVER_IP + MainActivity.TOKEN_INVALIDATE;
//            Map<String, String> params = new HashMap<>();
//            params.put("token", token);
//            VolleyWrapper.POST(this, url, params);

            //clear the username and auth token from local storage
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(USERNAME_KEY, NO_USERNAME);
            editor.putString(TOKEN_KEY, NO_TOKEN);
            editor.apply();
        }

        setFragment(new SSOFragment());
        bottomNavView.setVisibility(View.GONE);
    }
}
