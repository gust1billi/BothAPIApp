package com.example.bothapiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextInputLayout emailLayout, passLayout;
    TextInputEditText emailEdit, passEdit;

    Button loginButton;

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
            } else checkLogin( email, passEdit.getText( ).toString() );
        });
    }

    private void checkLogin(String email, String password) {
        Log.e("Values", email + " & " + password);

        try {
            String code = APIHandler.login(
                    MainActivity.this, email, password);
            // Log.e("Access Code", code);
            // nextActivity(code);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void nextActivity(String code) {
        Intent i = new Intent();
        i.putExtra("code", code);
        startActivity(i);
    }

    private void testGradle() {
        Log.e(getResources().getString(R.string.app_name), "bruh");
    }
}