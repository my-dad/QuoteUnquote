package com.qwen.bookqoutecollectot;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.qwen.quoteunquote.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings); // Create a simple layout or use default
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_container, new SettingsFragment()) // Define settings_container in layout
                    .commit();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Check if the selected item is the "Home" (Up) button
        if (item.getItemId() == android.R.id.home) {
            // Close the SettingsActivity and return to the previous activity (e.g., MainActivity)
            finish(); // This is the key line you were missing
            // Alternatively, you could use onBackPressed(); (deprecated in API 33+, but still functional)
            return true; // Indicate that the event was handled
        }
        // If the item is not the home button, let the superclass handle it (e.g., other menu items)
        return super.onOptionsItemSelected(item);
    }
}