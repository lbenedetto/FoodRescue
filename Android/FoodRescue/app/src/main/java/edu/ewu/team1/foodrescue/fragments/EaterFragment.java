package edu.ewu.team1.foodrescue.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Collections;

import edu.ewu.team1.foodrescue.FoodEvent;
import edu.ewu.team1.foodrescue.FoodEventAdapter;
import edu.ewu.team1.foodrescue.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EaterFragment extends Fragment {

    public EaterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_eater, container, false);
        SharedPreferences sharedPref = view.getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

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

        String[] data = sharedPref.getString("foodEvents", "").split("\n");
        ArrayList<FoodEvent> events = new ArrayList<>();
        for (String d : data) {
            if (!d.isEmpty())
                events.add(new FoodEvent(d));
        }
        Collections.sort(events);
        ListView list = view.findViewById(R.id.listViewFoodEvents);
        list.setAdapter(new FoodEventAdapter(events, view.getContext()));

        return view;
    }

}
