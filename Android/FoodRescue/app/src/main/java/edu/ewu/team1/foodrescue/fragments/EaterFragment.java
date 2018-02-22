package edu.ewu.team1.foodrescue.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import edu.ewu.team1.foodrescue.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EaterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class EaterFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    public EaterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_eater, container, false);
        SharedPreferences sharedPref = getContext().getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        //Set the state of the switches to the last state of the switch. If first time, set to true
        //Also, register a click listener to save the new state of the switch
        boolean state = sharedPref.getBoolean("receiveEventStartNotifications", false);
        Switch eStart = view.findViewById(R.id.switchEventStart);
        eStart.setChecked(state);
        eStart.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("receiveEventStartNotifications", eStart.isChecked());
            editor.apply();
        });


        state = sharedPref.getBoolean("receiveEventEndNotifications", false);
        Switch eEnd = view.findViewById(R.id.switchEventEnd);
        eEnd.setChecked(state);
        eEnd.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("receiveEventEndNotifications", eEnd.isChecked());
            editor.apply();
        });

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
