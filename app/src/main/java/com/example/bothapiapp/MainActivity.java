package com.example.bothapiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextInputLayout emailLayout, passLayout;
    TextInputEditText emailEdit, passEdit;

    TextView loginHeader;
    Button loginButton;

    String PREF_INTEGER = "Preference Integer";
    String LOGIN_INSTANCE = "Preference Login";
    String PREF_TOKEN = "Preference Token";
    String LOGIN_PREFERENCE = "LOGIN PREFERENCES";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.login_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences preferences = MainActivity.this.getSharedPreferences(LOGIN_PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String title = item.getTitle().toString();

        if ( title.equals( getString( R.string.write_preference ) ) ) {
            Log.e("PATH", "BRUH");
            int i = preferences.getInt(PREF_INTEGER, 0);
            i++;

            editor.putInt( PREF_INTEGER, i );
            Log.e( PREF_INTEGER, "amount " + i );
            editor.apply();
        } else if ( title.equals( getString( R.string.read_preferences ) ) ) {
            Log.e("PATH", "BRUHJ");
            Log.e("Data Preference",
                    "amount " + preferences.getInt("Preference Integer", 0 ) );
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences(LOGIN_PREFERENCE, MODE_PRIVATE);

        Log.e("Preference Instance",
                "" + preferences.getBoolean( LOGIN_INSTANCE, false ));
        Log.e("Preference Instance",
                "" + preferences.getString( PREF_TOKEN, "" ) );

        // For some reason, it doesn't work without this variable
        boolean autoLogin = preferences.getBoolean( LOGIN_INSTANCE, false );

        if ( autoLogin ) {
            nextActivity( preferences.getString( PREF_TOKEN, "" ) );
        }

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

    private void checkLogin(String email, String password) {
        Log.e("Values", email + " & " + password);

        APIHandler.doLogin(MainActivity.this, email, password);
    }

    public void nextActivity(String token) {
        Intent i = new Intent(MainActivity.this, ProductsViewActivity.class );
        i.putExtra("code", token);
        startActivity( i );
    }

}