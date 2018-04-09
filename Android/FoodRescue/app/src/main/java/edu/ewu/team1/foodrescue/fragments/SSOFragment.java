package edu.ewu.team1.foodrescue.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.ewu.team1.foodrescue.MainActivity;
import edu.ewu.team1.foodrescue.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SSOFragment extends Fragment {

    public SSOFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sso, container, false);

        Button buttonSignIn = view.findViewById(R.id.buttonSignIn);
        buttonSignIn.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MainActivity.CAS + MainActivity.AUTH_PAGE));
            startActivity(browserIntent);
        });
        return view;
    }

}
