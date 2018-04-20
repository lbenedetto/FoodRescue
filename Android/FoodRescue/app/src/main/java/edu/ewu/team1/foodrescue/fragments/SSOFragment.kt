package edu.ewu.team1.foodrescue.fragments

import android.content.Context
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

class SSOFragment : Fragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		val view = inflater.inflate(R.layout.fragment_sso, container, false)

		val buttonSignIn = view.findViewById<Button>(R.id.buttonSignIn)
		buttonSignIn.setOnClickListener {
			val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(MainActivity.CAS + MainActivity.AUTH_PAGE))
			startActivity(browserIntent)
		}
		view.findViewById<Button>(R.id.buttonSignInDev)
				.setOnClickListener {
					context!!
							.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
							.edit()
							.putString(MainActivity.USERNAME_KEY, "Developer")
							.putString(MainActivity.TOKEN_KEY, "DeveloperKey")
							.apply()
					(activity as MainActivity).finalizeSignIn()
				}
		return view
	}

}
