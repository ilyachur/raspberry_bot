package com.home.CleverHome;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends ExpandableListActivity {
    private MainMenuListAdapter menuListAdapter;
    public static final String PREFS_NAME = "main_conf";
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);

        menuListAdapter = new MainMenuListAdapter(this);

        setListAdapter(menuListAdapter);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View view, int group, int child, long id) {
        MainMenuListAdapter.SampleItem sample = menuListAdapter.getChild(group, child);

        Intent intent = null;

        try {
            intent = new Intent(MenuActivity.this, Class.forName(sample.className));
        } catch (ClassNotFoundException e) {
            showToast("Something went wrong...");
        }

        startActivity(intent);

        return super.onChildClick(parent, view, group, child, id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Операции для выбранного пункта меню
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent myIntent = new Intent(MenuActivity.this, SettingsActivity.class);
                startActivity(myIntent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void showToast(String title) {
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
    }
}
