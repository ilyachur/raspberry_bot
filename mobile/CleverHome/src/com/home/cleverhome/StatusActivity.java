package com.home.CleverHome;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ichuraev on 01/09/2015.
 */
public class StatusActivity extends Activity {

    private static final String subtitle = "Status";
    private static final String body = "GET_STATUS=True";
    private static final String STATUS_INFO = "status_info";

    private StatusTaskCompleter completer = new StatusTaskCompleter();
    private Context context;

    private String email_address;
    private String bot_email_address;
    private String email_password;
    private MenuItem menuItem;
    private TextView statusInfo;
    private SharedPreferences statusInformation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.status_activity);
        context = this;

        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        email_address = settings.getString("email_address", "");
        bot_email_address = settings.getString("bot_email_address", "");
        email_password = settings.getString("email_password", "");

        statusInfo = (TextView)findViewById(R.id.status_info);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);

        statusInformation = getSharedPreferences(STATUS_INFO, 0);
        updateInformation();
    }

    private void updateInformation() {
        String information = new String();
        information += "Last update: " + statusInformation.getString("last_update", "unknown") + "\n";
        information += "Last update status: " + statusInformation.getString("last_update_status", "failed") + "\n";
        information += statusInformation.getString("information", "");
        statusInfo.setText(information);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_menu, menu);


        menuItem = menu.findItem(R.id.menu_load);
        menuItem.setActionView(R.layout.progressbar);
        menuItem.expandActionView();
        MailWorkerAsync task = new MailWorkerAsync(this, completer, email_address, email_password, bot_email_address, subtitle, body);
        task.execute();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_load:
                menuItem = item;
                menuItem.setActionView(R.layout.progressbar);
                menuItem.expandActionView();
                MailWorkerAsync task = new MailWorkerAsync(this, completer, email_address, email_password, bot_email_address, subtitle, body);
                task.execute();
                break;
            case R.id.action_settings:
                Intent myIntent = new Intent(StatusActivity.this, SettingsActivity.class);
                startActivity(myIntent);
                break;
            default:
                break;
        }
        return true;
    }

    private class StatusTaskCompleter implements OnTaskCompleted{

        @Override
        public void onTaskCompleted(Object result) {
            menuItem.setActionView(null);

            if (result.toString().equals("")) {
                SharedPreferences.Editor editor = statusInformation.edit();
                editor.putString("last_update_status", "failed");
                Toast.makeText(context, getString(R.string.err_update), Toast.LENGTH_LONG).show();
                editor.commit();
                updateInformation();
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
            String currentDateandTime = sdf.format(new Date());

            SharedPreferences.Editor editor = statusInformation.edit();
            editor.putString("last_update_status", "passed");
            editor.putString("last_update", currentDateandTime);
            editor.putString("information", result.toString());

            editor.commit();

            updateInformation();
        }
    }
}