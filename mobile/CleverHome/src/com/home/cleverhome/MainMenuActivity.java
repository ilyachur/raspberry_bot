package com.home.CleverHome;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.*;

/**
 * Created by ichuraev on 01/09/2015.
 */
public class MainMenuActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.main_menu_activity);
    }

    public void onSettings(View view) {
        Intent myIntent = new Intent(MainMenuActivity.this, SettingsActivity.class);
        startActivity(myIntent);
    }

    public void onStatus(View view) {
        Intent myIntent = new Intent(MainMenuActivity.this, StatusActivity.class);
        startActivity(myIntent);
    }

    public void onExit(View view) {
        finish();
    }

    public void onOpportunities(View view) {
        Intent myIntent = new Intent(MainMenuActivity.this, OpportunitiesActivity.class);
        startActivity(myIntent);
    }

    public void onGPIO(View view) {
        //Intent myIntent = new Intent(MainMenuActivity.this, OpportunitiesActivity.class);
        //startActivity(myIntent);
    }

    public void onAbout(View view) {
        //Intent myIntent = new Intent(MainMenuActivity.this, OpportunitiesActivity.class);
        //startActivity(myIntent);
    }
}