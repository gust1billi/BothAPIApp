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
    private static String code;

    public static String doLogin(Context ctx, String email, String password) {
        String url = "https://tmiapi-dev.mitraindogrosir.co.id/api/login_member_api";
        Log.e("Location", "hit");

        RequestQueue queue = Volley.newRequestQueue( ctx );

        StringRequest request = new StringRequest( Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Assign Code --> code = "bruh";
                            JSONObject loginObject = new JSONObject(response);
                            String loginResult = loginObject.getString("message");

                            Utils.showToast( ctx, loginResult );

                            Log.e("Access", loginResult) ;
                            // Log.e("Code", code);
                            if ( loginResult.equals( "Berhasil masuk" ) ){
                                JSONObject responseObject = loginObject.getJSONObject("user_data");
                                String access_token = responseObject.getString("access_token");

//                                Log.e("access code", access_token);
                                code = access_token;
                            }

                        } catch (Exception e){
                            Log.e("JSON OBJ VOLLEY ERROR", e.toString() );
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
