package edu.ewu.team1.foodrescue.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.ewu.team1.foodrescue.FoodEvent
import edu.ewu.team1.foodrescue.FoodEventAdapter
import edu.ewu.team1.foodrescue.R
import kotlinx.android.synthetic.main.fragment_eater.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class EaterFragment : Fragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		val view = inflater.inflate(R.layout.fragment_eater, container, false)
		val sharedPref = view.context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

		//Set the state of the switches to the last state of the switch. If first time, set to true
		//Also, register a click listener to save the new state of the switch
		val state = sharedPref.getBoolean("receiveEventStartNotifications", false)

		switchEventStart.isChecked = state
		switchEventStart.setOnClickListener {
			val editor = sharedPref.edit()
			editor.putBoolean("receiveEventStartNotifications", switchEventStart.isChecked)
			editor.apply()
		}

		val data = sharedPref.getString("foodEvents", "").split("\n")
		val events = ArrayList<FoodEvent>()
		for (d in data) {
			if (!d.isEmpty())
				events.add(FoodEvent(d))
		}
		events.sort()
		listViewFoodEvents.adapter = FoodEventAdapter(events, view.context)

		return view
	}

}// Required empty public constructor
