package edu.ewu.team1.foodrescue.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import edu.ewu.team1.foodrescue.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SSOFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SSOFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public SSOFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        WebView webView;
        View view = inflater.inflate(R.layout.fragment_sso, container, false);
        SharedPreferences sharedPref = getContext().getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String defaultValue = "NoUUID";
        String UUID = sharedPref.getString("UUID", defaultValue);
        if (UUID.equals(defaultValue)) {
            //User has never sucessfully signed into SSO before
            final Activity activity = getActivity();
            webView = view.findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
                    // Activities and WebViews measure progress with different scales.
                    // The progress meter will automatically disappear when we reach 100%
                    activity.setProgress(progress * 1000);
                }
            });
            webView.setWebViewClient(new WebViewClient() {
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Toast.makeText(activity, "Error: " + description, Toast.LENGTH_SHORT).show();
                }
            });
            webView.loadUrl("https://login.ewu.edu");
            //TODO: (Very Hard) Get UUID. This code should be async.
            // Also, might not even be supposed to load this in a web view.
            //I just really have no idea how SSO is supposed to work
            UUID = "NoUUID";
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("UUID", UUID);
            editor.apply();
            //TODO: (Easy, not yet possible) Remove Sign in option from bottom navigation view upon successful sign in
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // : Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
