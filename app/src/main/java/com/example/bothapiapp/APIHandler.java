package com.example.bothapiapp;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class APIHandler {
    static String code = "";

    public static String login(Context ctx, String email, String password) {
        String url = "https://tmiapi-dev.mitraindogrosir.co.id/api/login_member_api";
        Log.e("Here", "Hit");

        RequestQueue queue = Volley.newRequestQueue(ctx);

        StringRequest request = new StringRequest( Request.Method.POST, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        // Assign Code --> code = "bruh";
                        JSONObject loginObject = new JSONObject(response);
                        JSONObject responseObject = loginObject.getJSONObject("user_data");

                        code = loginObject.getString("access_token");

                        Log.e("Access", responseObject.getString("message"));
                        Log.e("Code", code);

//                        Log.e(AccessCodeTalker.TAG(),
//                                response.getJSONObject("user_data")
//                                        .getString("access_token") );
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utils.showToast(ctx, "Login Failed: " + error);
                    Log.e("Error POST VOLLEY", error.toString() );
                }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();

                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };

        queue.add(request);

        return code;
    }
}
