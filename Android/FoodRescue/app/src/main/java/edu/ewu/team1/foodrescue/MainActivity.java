package edu.ewu.team1.foodrescue;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import edu.ewu.team1.foodrescue.fragments.EaterFragment;
import edu.ewu.team1.foodrescue.fragments.FeederFragment;
import edu.ewu.team1.foodrescue.fragments.SSOFragment;

public class MainActivity extends AppCompatActivity implements
        SSOFragment.OnFragmentInteractionListener,
        FeederFragment.OnFragmentInteractionListener,
        EaterFragment.OnFragmentInteractionListener {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            //TODO: (Easy, but not yet possible) Only switch away from SSO Fragment if the user is signed in
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
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //TODO: (Easy, but not yet possible) Check if already logged in, load different fragment instead
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new SSOFragment()).commit();
        navigation.setSelectedItemId(R.id.navigation_sso);
        navigation.getMenu().findItem(R.id.navigation_sso).setChecked(true);
        checkPlayServices();
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
