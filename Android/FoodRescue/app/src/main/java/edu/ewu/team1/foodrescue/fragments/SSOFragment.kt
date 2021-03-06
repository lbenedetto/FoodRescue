package edu.ewu.team1.foodrescue.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import edu.ewu.team1.foodrescue.MainActivity
import edu.ewu.team1.foodrescue.R
import edu.ewu.team1.foodrescue.utilities.DataManager

class SSOFragment : Fragment() {
	private lateinit var dataManager: DataManager

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		val view = inflater.inflate(R.layout.fragment_sso, container, false)
		dataManager = (activity as MainActivity).dataManager
		val buttonSignIn = view.findViewById<Button>(R.id.buttonSignIn)
		buttonSignIn.setOnClickListener {
			val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(MainActivity.CAS + MainActivity.AUTH_PAGE))
			startActivity(browserIntent)
		}
		view.findViewById<Button>(R.id.buttonSignInDev)
				.setOnClickListener {
					dataManager.saveUsernameAndToken("Developer", "DeveloperKey")
					val ma = activity as MainActivity
					ma.username = "Developer"
					ma.finalizeSignIn()
				}
		return view
	}

}
