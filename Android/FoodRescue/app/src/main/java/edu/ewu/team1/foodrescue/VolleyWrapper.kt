package edu.ewu.team1.foodrescue

import android.content.Context
import android.util.Log

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

object VolleyWrapper {

	fun post(context: Context, url: String, params: Map<String, String>, response: Response.Listener<String>) {
		val queue = Volley.newRequestQueue(context)
		val postRequest = object : StringRequest(Request.Method.POST, url,
				response,
				Response.ErrorListener { error -> Log.d("Error.Response", error.message) }
		) {
			override fun getParams(): Map<String, String> {
				return params
			}
		}
		queue.add(postRequest)
	}
}
