package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen);

        ImageView ivFlag = findViewById(R.id.ivFlag);
        TextView tvHalo = findViewById(R.id.tvHalo);

        Locale locale = Locale.getDefault();
        String lang = locale.getLanguage();   // en, fr, it, de
        String country = locale.getCountry(); // US, CA, FR, DE, IT, dll

        int flagRes = R.drawable.flag_id;
        String helloText = "Halo";

        // Logika khusus per kombinasi bahasa + negara
        if (lang.equals("en") && country.equals("CA")) {
            flagRes = R.drawable.flag_ca;
            helloText = "Hello";
        } else if (lang.equals("fr") && country.equals("CA")) {
            flagRes = R.drawable.flag_ca;
            helloText = "Bonjour";
        } else if (lang.equals("en")) {
            flagRes = R.drawable.flag_us;
            helloText = "Hello";
        } else if (lang.equals("fr")) {
            flagRes = R.drawable.flag_fr;
            helloText = "Bonjour";
        } else if (lang.equals("it")) {
            flagRes = R.drawable.flag_it;
            helloText = "Ciao";
        } else if (lang.equals("de")) {
            flagRes = R.drawable.flag_de;
            helloText = "Hallo";
        }

        ivFlag.setImageResource(flagRes);
        tvHalo.setText(helloText);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 2500);
    }
}
