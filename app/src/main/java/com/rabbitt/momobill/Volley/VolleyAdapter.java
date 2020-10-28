package com.rabbitt.momobill.Volley;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class VolleyAdapter {

    private static final String TAG = "kook";
    private Context context;

    public VolleyAdapter(Context context)
    {
        this.context = context;
    }

    public void insertData(final Map<String, String> user_data, String url, ServerCallback callback)
    {
        Log.i(TAG, "VolleyAdapter: "+user_data.toString());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "Responce.............." + response);
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "volley error.............................." + error.getMessage());
                        callback.onError(error.getMessage());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //Adding the parameters to the request
                // Map<String, String> user = new HashMap<>();
                // user.put("username", nameTxt.getText().toString());
                params = user_data;
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    public void getData(final Map<String, String> user_data, String url, ServerCallback callback)
    {
        Log.i(TAG, "VolleyAdapter: "+user_data.toString());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "Responce.............." + response);
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "volley error.............................." + error.getMessage());
                        callback.onError(error.getMessage());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                //Adding the parameters to the request
                params = user_data;
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);

    }

    public void getData(String url, final ServerCallback callback)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "Responce.............." + response);
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "volley error.............................." + error.getMessage());
                        callback.onError(error.getMessage());
                    }
                });

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }
}
