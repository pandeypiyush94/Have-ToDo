package com.piyush.havetodo;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import static com.piyush.havetodo.AppConstants.DARK_THEME;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

          /*--------------Set Activity's Theme--------------*/
        AppPreferences appPref = new AppPreferences(this);
        int currentTheme;
        if (appPref.getTheme().equals(DARK_THEME))
            currentTheme = R.style.CustomStyle_DarkTheme;
        else
            currentTheme = R.style.CustomStyle_LightTheme;
        this.setTheme(currentTheme);

         /*--------------Set Activity's View--------------*/
        setContentView(R.layout.activity_settings);

         /*-------------Set Activity's Toolbar-------------*/
        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(R.string.action_settings);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /*-------------Set Fragment to Activity-------------*/
        getFragmentManager().beginTransaction().replace(R.id.container,new SettingsFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        return true;
    }
}
