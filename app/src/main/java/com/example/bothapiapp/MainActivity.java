package com.example.bothapiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
            if ( emailEdit.length() != 0 && passEdit.length() != 0 ){
                Log.e("PATH", "EMAIL & PASSWORD IS NOT EMPTY");

            } else {
                Utils.showToast(MainActivity.this, "Please input email & password");
            }
        });
    }

    private void testGradle() {
        Log.e(getResources().getString(R.string.app_name), "bruh");
    }
}