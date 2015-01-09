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

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Операции для выбранного пункта меню
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent myIntent = new Intent(MainMenuActivity.this, SettingsActivity.class);
                startActivity(myIntent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    */
}