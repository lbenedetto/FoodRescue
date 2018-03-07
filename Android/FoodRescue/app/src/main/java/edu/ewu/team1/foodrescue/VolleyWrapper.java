package edu.ewu.team1.foodrescue;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

public class VolleyWrapper {

    public static void POST(Context context, String url, Map<String, String> params) {
        POST(context, url, params, response -> Log.d("Response", response));
    }

    public static void POST(Context context, String url, Map<String, String> params, Response.Listener<String> responseHandler) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                responseHandler,
                error -> Log.d("Error.Response", error.getMessage())
        ) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        queue.add(postRequest);
    }
}
