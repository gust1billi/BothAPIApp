package com.example.bothapiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextInputLayout emailLayout, passLayout;
    TextInputEditText emailEdit, passEdit;

    TextView loginHeader;

    Button loginButton;

    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testGradle();

        emailEdit   = findViewById(R.id.emailInputEdit);
        emailLayout = findViewById(R.id.emailInputLayout);
        passEdit    = findViewById(R.id.passwordInputEdit);
        passLayout  = findViewById(R.id.passwordInputLayout);
        loginButton = findViewById(R.id.loginButton);
        loginHeader = findViewById(R.id.login_header2);

        emailEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            { /* Ignore */ }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            { /* Ignore */ }

            @Override
            public void afterTextChanged(Editable editable) {
                emailLayout.setError("Please input an Email correctly");
                emailLayout.setErrorEnabled( !Utils.isEmail( editable.toString( ) ) );
            }
        });

        passEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            { /* Ignore */ }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            { /* Ignore */ }

            @Override
            public void afterTextChanged(Editable editable) {
                passLayout.setError("Please input a Password with at least 6 digits");
                passLayout.setErrorEnabled( !Utils.isPassword( editable.toString( ) ) );
            }
        });

        loginButton.setOnClickListener(view -> {
            String email = Objects.requireNonNull( emailEdit.getText( ) ).toString();
            String pass  = Objects.requireNonNull( passEdit.getText( ) ).toString();

            if ( emailEdit.length() == 0 || passEdit.length() == 0 ){
                Utils.showToast(MainActivity.this, "EMAIL OR  PASSWORD IS EMPTY");

            } else if ( emailLayout.isErrorEnabled( ) || passLayout.isErrorEnabled( ) ){
                Utils.showToast(MainActivity.this, "Please input email & password correctly");
            } else {
                checkLogin( email, pass );

            }
        });
    }

    public void doLogin(Context ctx, String email, String password) {
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

                                Log.e("access code", access_token);

                                setCode(access_token);

//                                loginHeader.setText(access_token);

                                nextActivity(access_token);
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
    }

    private void checkLogin(String email, String password) {
        Log.e("Values", email + " & " + password);

        doLogin(MainActivity.this, email, password);
    }

    private void nextActivity(String token) {
        Intent i = new Intent(MainActivity.this, ProductsViewActivity.class );
        i.putExtra("code", token);
        startActivity(i);
    }

    private void setCode(String access_code){
        code = access_code; Log.e("value", code);
    }

    private void testGradle() {
        Log.e(getResources().getString(R.string.app_name), "bruh");
    }
}