package edu.ewu.team1.foodrescue.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Switch
import edu.ewu.team1.foodrescue.DataManager
import edu.ewu.team1.foodrescue.FoodEvent
import edu.ewu.team1.foodrescue.FoodEventAdapter
import edu.ewu.team1.foodrescue.R
import java.util.*

@SuppressLint("ValidFragment")
class EaterFragment(private val dataManager: DataManager) : Fragment() {
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		val view = inflater.inflate(R.layout.fragment_eater, container, false)

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
		view.findViewById<ListView>(R.id.listViewFoodEvents).adapter = FoodEventAdapter(events, view.context, inflater, dataManager)

		return view
	}

}