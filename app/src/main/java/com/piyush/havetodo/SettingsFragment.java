package com.piyush.havetodo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import static com.piyush.havetodo.AppConstants.DARK_THEME;
import static com.piyush.havetodo.AppConstants.LIGHT_THEME;

/**
 Created by Piyush on 2/21/2018.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private AppPreferences appPref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appPref = new AppPreferences(getActivity());
        addPreferencesFromResource(R.xml.settings_layout);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.dark_mode_pref_key))){
            appPref.recreateActivity(true);
            CheckBoxPreference preference = (CheckBoxPreference) findPreference(getString(R.string.dark_mode_pref_key));
            if (preference.isChecked())
                appPref.saveTheme(DARK_THEME);
            else
                appPref.saveTheme(LIGHT_THEME);
            getActivity().recreate();
        } else if (key.equals(getString(R.string.vibration_pref_key))){
            CheckBoxPreference preference = (CheckBoxPreference) findPreference(getString(R.string.vibration_pref_key));
            if (preference.isChecked())
                appPref.setVibration(true);
            else
                appPref.setVibration(false);
        }
    }
}
