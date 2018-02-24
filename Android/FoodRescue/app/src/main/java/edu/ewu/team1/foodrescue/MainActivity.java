package edu.ewu.team1.foodrescue;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
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

public class MainActivity extends AppCompatActivity implements
        SSOFragment.OnFragmentInteractionListener,
        FeederFragment.OnFragmentInteractionListener,
        EaterFragment.OnFragmentInteractionListener {
    private Pattern pattern = Pattern.compile("<cas:user>(.*?)</cas:user>");
    private BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = item -> {
        if (!getUsername().equals("NoUUID")) {
            switch (item.getItemId()) {
                //TODO: (Easy) Only switch fragments if its not the currently active fragment
                case R.id.navigation_feeder:
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                    ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                    ft.replace(R.id.fragment_container, new FeederFragment(), "fragment");
                    ft.addToBackStack(null);
                    // Start the animated transition.
                    ft.commit();
                    return true;
                case R.id.navigation_eater:
                    FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();

                    ft2.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                    ft2.replace(R.id.fragment_container, new EaterFragment(), "fragment");
                    ft2.addToBackStack(null);
                    // Start the animated transition.
                    ft2.commit();
                    return true;
            }
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_main);

        navigation = findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        String UUID = getUsername();
        if (UUID.equals("NoUUID")) {//If the user has not signed in before
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new SSOFragment()).commit();
            navigation.setSelectedItemId(R.id.navigation_sso);
            navigation.getMenu().findItem(R.id.navigation_sso).setChecked(true);

            login();
        } else {
            Toast.makeText(this, "logged in as " + UUID, Toast.LENGTH_LONG).show();
            finalizeSignIn();
        }

        checkPlayServices();
    }

    private String getUsername() {
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String defaultValue = "NoUUID";
        return sharedPref.getString("UUID", defaultValue);
    }

    private void login() {
        //Authentication code
        Intent intent = getIntent();
        String url = intent.getDataString();
        if (url != null) {
            //Extract ticket
            String ticket = url.substring(url.indexOf("ticket="), url.length());
            String validateURL = "https://login.ewu.edu/cas/serviceValidate?" + ticket + "&service=https://foodrescue.ewu.edu/login_redirect";

            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest getRequest = new StringRequest(Request.Method.GET, validateURL,
                    response -> {
                        Log.d("Response", response);
                        Matcher m = pattern.matcher(response);
                        if (m.find()) {
                            String username = m.group(1);
                            SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("UUID", username);
                            editor.apply();
                            Toast.makeText(this, "logged in as " + username, Toast.LENGTH_LONG).show();
                        }
                        Log.d("CAS Auth Failure", "Response did not contain error code");
                    },
                    error -> Log.d("Error.Response", error.toString())
            );
            queue.add(getRequest);

            finalizeSignIn();
        }
    }

    private void finalizeSignIn() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new EaterFragment()).commit();
        navigation.setSelectedItemId(R.id.navigation_eater);
        navigation.getMenu().findItem(R.id.navigation_eater).setChecked(true);

        navigation.getMenu().removeItem(R.id.navigation_sso);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

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
    public void onFragmentInteraction(Uri uri) {

    }
}
