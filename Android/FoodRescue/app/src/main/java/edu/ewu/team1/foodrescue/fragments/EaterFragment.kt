package edu.ewu.team1.foodrescue.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Switch
import edu.ewu.team1.foodrescue.MainActivity
import edu.ewu.team1.foodrescue.R
import edu.ewu.team1.foodrescue.utilities.DataManager
import edu.ewu.team1.foodrescue.utilities.FoodEvent
import edu.ewu.team1.foodrescue.utilities.FoodEventAdapter
import java.util.*

class EaterFragment : Fragment() {
	private lateinit var dataManager: DataManager

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {
		super.onCreateView(inflater, container, savedInstanceState)
		// Inflate the layout for this fragment
		val view = inflater.inflate(R.layout.fragment_eater, container, false)
		dataManager = (activity as MainActivity).dataManager
		//Set the state of the switches to the last state of the switch. If first time, set to true
		//Also, register a click listener to save the new state of the switch
		val state = dataManager.areNotificationsEnabled()
		val switch = view.findViewById<Switch>(R.id.switchEventStart)
		switch.isChecked = state
		switch.setOnClickListener {
			dataManager.setNotificationsEnabledState(switch.isChecked)
		}
		val data = dataManager.getAllFoodEvents()
		val events = ArrayList<FoodEvent>()
		try {
			for (d in data) {
				if (!d.isEmpty()) {
					val fe = FoodEvent(d)
					if (fe.timestamp + (fe.duration * 60000) > System.currentTimeMillis())
						events.add(fe)
					else //Remove expired events from memory
						dataManager.removeFoodEvent(fe)
				}
			}
		} catch (e: Throwable) {
			//data is corrupted, delete it all
			dataManager.clearAllFoodEvents()
		}
		//TODO: Refresh button? Or detect changes to shared preferences?
		events.sort()
		val list = view.findViewById<ListView>(R.id.listViewFoodEvents)
		list.emptyView = view.findViewById(R.id.empty)
		list.adapter = FoodEventAdapter(events, view.context, inflater, dataManager)

		return view
	}

}
