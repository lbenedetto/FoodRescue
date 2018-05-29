package edu.ewu.team1.foodrescue

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import edu.ewu.team1.foodrescue.fragments.EaterFragment
import edu.ewu.team1.foodrescue.fragments.FeederFragment
import edu.ewu.team1.foodrescue.fragments.SSOFragment
import edu.ewu.team1.foodrescue.utilities.ConfirmDialog
import edu.ewu.team1.foodrescue.utilities.DataManager

class MainActivity : AppCompatActivity() {
	private lateinit var bottomNavView: BottomNavigationView
	private var feederIsActive = false
	lateinit var dataManager: DataManager
	var username: String = DataManager.NO_USERNAME

	companion object {
		const val SERVER_IP = "146.187.135.29"
		const val CAS = "https://login.ewu.edu/cas/login?service="
		const val AUTH_PAGE = "https://$SERVER_IP/api/login/android"
		const val SEND_NOTIFICATION = "/api/announce/"
		const val TAG_SSO = "SSO"
		const val TAG_EATER = "Eater"
		const val TAG_FEEDER = "Feeder"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		dataManager = DataManager(getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE))
		//Populating the bottom navigation bar
		bottomNavView = findViewById(R.id.bottomNavigationView)
		bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
		username = dataManager.getUsername()
		//Authentication process
		when {
			username == DataManager.NO_USERNAME -> {//If the user has not signed in before
				val intent = intent
				val extras = intent.extras
				if (extras != null) {
					val token = extras.getString("auth_token") ?: DataManager.NO_TOKEN
					username = extras.getString("uid") ?: DataManager.NO_USERNAME
					if (username != DataManager.NO_USERNAME) {
						dataManager.saveUsernameAndToken(username, token)
						finalizeSignIn()
						return
					}
				}
				setFragment(TAG_SSO)
				bottomNavView.visibility = View.GONE
			}
			savedInstanceState == null -> finalizeSignIn()
		}

		checkPlayServices()
	}


	/**
	 * This is called when the user uses one of the two buttons on the bottom nav bar to switch to
	 * a different view. It checks to make sure the user isn't trying to switch to the currently
	 * active fragment, which would look really silly
	 */
	private val mOnNavigationItemSelectedListener = { item: MenuItem ->
		if (username != DataManager.NO_USERNAME) {
			when (item.itemId) {
				R.id.navigation_feeder -> {
					if (!feederIsActive) {
						setFragment(TAG_FEEDER, R.anim.slide_in_left, R.anim.slide_out_right)
						feederIsActive = true
					}
					true
				}
				R.id.navigation_eater -> {
					if (feederIsActive) {
						setFragment(TAG_EATER, R.anim.slide_in_right, R.anim.slide_out_left)
						feederIsActive = false
					}
					true
				}
				else -> false
			}
		} else {
			false
		}
	}

	/**
	 * Switches the fragment in the fragment_container to the specified target by playing the
	 * exit animation for the current fragment, and the entrance animation for the new fragment
	 *
	 * @param tag               the tag of the fragment to switch to
	 * @param entranceAnimation the entrance animation to use
	 * @param exitAnimation     the exit animation to use
	 */
	private fun setFragment(tag: String, entranceAnimation: Int, exitAnimation: Int) {
		supportFragmentManager.beginTransaction()
				.setCustomAnimations(entranceAnimation, exitAnimation)
				.replace(R.id.fragment_container, getFragmentInstance(tag), tag)
				//.addToBackStack(null)
				.commit()
	}

	/**
	 * Switch the fragment in the fragment_container to the specified target with no animation
	 *
	 * @param tag the tag of the fragment to switch to
	 */
	private fun setFragment(tag: String) {
		supportFragmentManager.beginTransaction()
				.replace(R.id.fragment_container, getFragmentInstance(tag), tag)
				//.addToBackStack(null)
				.commit()
	}

	override fun dispatchTouchEvent(event: MotionEvent): Boolean {
		if (event.action == MotionEvent.ACTION_DOWN) {
			val v = currentFocus
			if (v is EditText) {
				Log.d("focus", "touchevent")
				v.clearFocus()
				val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
				imm.hideSoftInputFromWindow(v.windowToken, 0)
			}
		}
		return super.dispatchTouchEvent(event)
	}

	/**
	 * Sets the fragment to the Eater Fragment, removes the sign in option from the bottom nav menu
	 * Called whether or not CAS was actually contacted to sign in (ie, if user was already signed in)
	 */
	fun finalizeSignIn() {
		setFragment(TAG_EATER, R.anim.slide_in_right, R.anim.slide_out_left)
		Toast.makeText(this, "logged in as $username", Toast.LENGTH_LONG).show()
		selectMenuItem(R.id.navigation_eater)
	}

	/**
	 * Sets the specified item to be visually selected in the bottom nav bar
	 *
	 * @param itemID the ID to select
	 */
	private fun selectMenuItem(itemID: Int) {
		bottomNavView.visibility = View.VISIBLE
		bottomNavView.selectedItemId = itemID
		bottomNavView.menu.findItem(itemID).isChecked = true
	}

	override fun onResume() {
		super.onResume()
		checkPlayServices()
	}

	/**
	 * Makes sure google player services is available, as our app will not work without it
	 */
	private fun checkPlayServices() {
		val apiAvailability = GoogleApiAvailability.getInstance()
		val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {
				apiAvailability.getErrorDialog(this, resultCode, 2404).show()
				apiAvailability.makeGooglePlayServicesAvailable(this)
			} else {
				Toast.makeText(this, "This device is not supported.", Toast.LENGTH_LONG).show()
				finish()
			}
		}
	}

	@SuppressLint("RestrictedApi")
	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		val id = item.itemId
		when (id) {
			R.id.action_logout -> {
				ConfirmDialog.confirmAction(Runnable {
					logout()
				}, R.string.confirm_logout, this)
				return true
			}
		}
		return super.onOptionsItemSelected(item)
	}

	private fun logout() {
		//clear the username and auth token from local storage
		dataManager.clearAll()
		setFragment(TAG_SSO)
		bottomNavView.visibility = View.GONE
	}

	private fun getFragmentInstance(tag: String): Fragment {
		var fragment = supportFragmentManager.findFragmentByTag(tag)
		if (fragment == null) {
			fragment = when (tag) {
				"Feeder" -> FeederFragment()
				"Eater" -> EaterFragment()
				else -> SSOFragment()
			}
		}
		return fragment
	}
}
