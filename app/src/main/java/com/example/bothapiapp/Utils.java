package com.example.bothapiapp;

import android.content.Context;
import android.util.Patterns;
import android.widget.Toast;

public class Utils {
    public static void showToast(Context ctx, String text){
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }

    public static boolean isEmail(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPassword(String pass){
        return pass.length() >= 6;
    }
}
