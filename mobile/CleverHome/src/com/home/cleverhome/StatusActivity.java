package com.home.CleverHome;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by ichuraev on 01/09/2015.
 */
public class StatusActivity extends Activity {

    private static final String subtitle = "Status";

    private StatusTaskCompleter completer = new StatusTaskCompleter();

    private String email_address;
    private String bot_email_address;
    private String email_password;
    private MenuItem menuItem;
    private TextView statusInfo;
    private boolean isSendingStatus = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.status_activity);

        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        email_address = settings.getString("email_address", "");
        bot_email_address = settings.getString("bot_email_address", "");
        email_password = settings.getString("email_password", "");

        statusInfo = (TextView)findViewById(R.id.status_info);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_load:
                menuItem = item;
                menuItem.setActionView(R.layout.progressbar);
                menuItem.expandActionView();
                isSendingStatus = true;
                MailWorkerAsync task = new MailWorkerAsync(this, completer, email_address, email_password, bot_email_address, subtitle);
                task.execute();
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
            statusInfo.setText(result.toString());
        }
    }
}