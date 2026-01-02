package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsMenuActivity extends AppCompatActivity {

    private LinearLayout menuRecycleBin, menuAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_menu);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }

        initViews();
        setupMenuClicks();
    }

    private void initViews() {
        menuRecycleBin = findViewById(R.id.menuRecycleBin);
        menuAbout = findViewById(R.id.menuAbout);
    }

    private void setupMenuClicks() {
        menuRecycleBin.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        menuAbout.setOnClickListener(v -> {
            // TODO: Implement About screen
            // For now, just show toast or do nothing
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}