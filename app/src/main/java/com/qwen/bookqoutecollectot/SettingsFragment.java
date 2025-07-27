package com.qwen.bookqoutecollectot;

import android.os.Bundle;
import android.util.Log;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import androidx.appcompat.app.AppCompatDelegate;

import com.qwen.quoteunquote.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String TAG = "SettingsFragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        SwitchPreferenceCompat darkModePreference = findPreference("dark_mode");
        if (darkModePreference != null) {
            int currentMode = AppCompatDelegate.getDefaultNightMode();
            boolean isSwitchChecked;

            Log.d(TAG, "Persisted AppCompatDelegate mode on startup: " + modeToString(currentMode));

            if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
                isSwitchChecked = true;
            } else if (currentMode == AppCompatDelegate.MODE_NIGHT_NO) {
                isSwitchChecked = false;
            } else {
                isSwitchChecked = false;
            }

            darkModePreference.setChecked(isSwitchChecked);
            Log.d(TAG, "Dark mode switch initialized to: " + isSwitchChecked);



            darkModePreference.setOnPreferenceChangeListener((preference, newValue) -> {

                boolean isDarkModeEnabled = (Boolean) newValue;
                Log.d(TAG, "Dark mode switch toggled to: " + isDarkModeEnabled);

                if (isDarkModeEnabled) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    Log.d(TAG, "AppCompatDelegate set to MODE_NIGHT_YES (Force Dark)");
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    Log.d(TAG, "AppCompatDelegate set to MODE_NIGHT_FOLLOW_SYSTEM");

                }
                return true;
            });
        } else {
            Log.w(TAG, "Dark mode preference not found in root_preferences.xml");
        }
    }

    private String modeToString(int mode) {
        return switch (mode) {
            case AppCompatDelegate.MODE_NIGHT_YES -> "MODE_NIGHT_YES";
            case AppCompatDelegate.MODE_NIGHT_NO -> "MODE_NIGHT_NO";
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> "MODE_NIGHT_FOLLOW_SYSTEM";
            case AppCompatDelegate.MODE_NIGHT_UNSPECIFIED -> "MODE_NIGHT_UNSPECIFIED";
            default -> "UNKNOWN_MODE (" + mode + ")";
        };
    }
}