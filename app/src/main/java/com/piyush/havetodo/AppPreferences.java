package com.piyush.havetodo;

import android.content.Context;
import android.content.SharedPreferences;

import static com.piyush.havetodo.AppConstants.LIGHT_THEME;
import static com.piyush.havetodo.AppConstants.RECREATE_ACTIVITY;
import static com.piyush.havetodo.AppConstants.SET_ALARMOFF;
import static com.piyush.havetodo.AppConstants.SET_VIBRATION;
import static com.piyush.havetodo.AppConstants.SET_WALKTHROUGH;
import static com.piyush.havetodo.AppConstants.THEME_SAVED;
import static com.piyush.havetodo.AppConstants.THEME_PREFERENCES;

/**
 Created by Piyush on 2/21/2018.
 */

class AppPreferences {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    AppPreferences(Context context) {
        preferences = context.getSharedPreferences(THEME_PREFERENCES,Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.apply();
    }

    void saveTheme(String theme){
        editor.putString(THEME_SAVED,theme);
        editor.apply();
    }
    String getTheme(){return preferences.getString(THEME_SAVED,LIGHT_THEME);}

    void recreateActivity(boolean val){
        editor.putBoolean(RECREATE_ACTIVITY,val);
        editor.apply();
    }
    boolean isActivityRecreated(){return preferences.getBoolean(RECREATE_ACTIVITY,false);}

    void setVibration(boolean val){
        editor.putBoolean(SET_VIBRATION,val);
        editor.apply();
    }
    boolean isVibrationEnabled(){return preferences.getBoolean(SET_VIBRATION, true);}

    void setAlarmOff(boolean val){
        editor.putBoolean(SET_ALARMOFF, val);
        editor.apply();
    }
    boolean isAlarmOff(){return preferences.getBoolean(SET_ALARMOFF,false);}

    void setWalkThrough(boolean val){
        editor.putBoolean(SET_WALKTHROUGH, val);
        editor.apply();
    }
    boolean isWalkThroughed(){return preferences.getBoolean(SET_WALKTHROUGH,false);}
}
