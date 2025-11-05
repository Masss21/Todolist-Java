package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView flagImage = findViewById(R.id.flagImage);
        TextView helloText = findViewById(R.id.helloText);

        Locale locale = Locale.getDefault();
        String lang = locale.getLanguage();   // en, fr, it, de
        String country = locale.getCountry(); // US, CA, FR, etc.

        int flagRes = R.drawable.flag_id;
        String helloStr = getString(R.string.hello); // fallback ke string resources

        // logika kombinasi bahasa + negara
        if (lang.equals("en") && country.equals("CA")) {
            flagRes = R.drawable.flag_ca;
            helloStr = "Hello";
        } else if (lang.equals("fr") && country.equals("CA")) {
            flagRes = R.drawable.flag_ca;
            helloStr = "Bonjour";
        } else if (lang.equals("en")) {
            flagRes = R.drawable.flag_us;
            helloStr = "Hello";
        } else if (lang.equals("fr")) {
            flagRes = R.drawable.flag_fr;
            helloStr = "Bonjour";
        } else if (lang.equals("it")) {
            flagRes = R.drawable.flag_it;
            helloStr = "Ciao";
        } else if (lang.equals("de")) {
            flagRes = R.drawable.flag_de;
            helloStr = "Hallo";
        }

        // tampilkan hasilnya
        flagImage.setImageResource(flagRes);
        helloText.setText(helloStr);
    }
}
