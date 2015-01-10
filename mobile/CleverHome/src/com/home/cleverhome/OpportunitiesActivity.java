package com.home.CleverHome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by ichuraev on 01/10/2015.
 */
public class OpportunitiesActivity extends Activity {

    private static final String TAG = "OpportunitiesActivity";
    public static final String OPPORTUNITIES_INFO = "opportunities_info";
    private String email_address;
    private String bot_email_address;
    private String email_password;

    private ListView opportunitiesList;
    private ArrayAdapter<String> opportunitiesAdapter;
    ArrayList<String> opportunities = new ArrayList<String>();

    private OpportunitiesTaskCompleter completer = new OpportunitiesTaskCompleter();
    private Context context;

    private SharedPreferences opportunitiesInformation;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.opportunities_activity);

        opportunitiesList = (ListView)findViewById(R.id.opportunitiesList);

        opportunitiesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, opportunities);
        opportunitiesAdapter.sort(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.toLowerCase().compareTo(rhs.toLowerCase());
            }
        });
        opportunitiesList.setAdapter(opportunitiesAdapter);
        registerForContextMenu(opportunitiesList);
        opportunitiesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) opportunitiesList.getItemAtPosition(position);

                Map<String, ?> allEntries = opportunitiesInformation.getAll();
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    if (entry.getKey().split("\\|")[0].equals(item)) {
                        item = entry.getKey();
                        break;
                    }
                }

                String subtitle;
                if (item.split("\\|").length > 1) {
                    subtitle = item.split("\\|")[1];
                } else {
                    subtitle = item.split("\\|")[0];
                }

                String body = opportunitiesInformation.getString(item, "");


                MailWorkerAsync task = new MailWorkerAsync(context, completer, email_address, email_password, bot_email_address, subtitle, body);
                task.execute();
            }
        });

        opportunitiesInformation = getSharedPreferences(OPPORTUNITIES_INFO, 0);
        if (opportunitiesInformation.getAll().isEmpty()) {
            SharedPreferences.Editor editor = opportunitiesInformation.edit();
            editor.putString("GPIO 1 ON|GPIO WORK", "GPIO = 1 ON");
            editor.putString("Update status|Status", "GET_STATUS=True");
            editor.putString("Light|Light", "GPIO = 1 ON\nGPIO = 3 ON\n");

            editor.commit();
        }

        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        email_address = settings.getString("email_address", "");
        bot_email_address = settings.getString("bot_email_address", "");
        email_password = settings.getString("email_password", "");
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateList();
    }

    private void updateList() {
        opportunitiesAdapter.clear();
        Map<String, ?> allEntries = opportunitiesInformation.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            opportunitiesAdapter.add(entry.getKey().split("\\|")[0]);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.opportunities_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent;
        switch (item.getItemId()) {
            case R.id.action_add:
                myIntent = new Intent(OpportunitiesActivity.this, AddOpportunityActivity.class);
                startActivity(myIntent);
                break;
            case R.id.action_settings:
                myIntent = new Intent(OpportunitiesActivity.this, SettingsActivity.class);
                startActivity(myIntent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(getString(R.string.select_action));
        menu.add(0, v.getId(), 0, getString(R.string.edit));
        menu.add(0, v.getId(), 0, getString(R.string.remove));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        if(item.getTitle().equals(getString(R.string.edit))){
            editOpportunity(index);
        }
        else if(item.getTitle().equals(getString(R.string.remove))){
            deleteOpportunity(index);
        }else{
            return false;
        }

        return true;
    }

    private void deleteOpportunity(int index) {
        String name = opportunitiesAdapter.getItem(index);


        Map<String, ?> allEntries = opportunitiesInformation.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().split("\\|")[0].equals(name)) {
                SharedPreferences.Editor editor = opportunitiesInformation.edit();
                editor.remove(entry.getKey());
                editor.commit();
            }
        }

        updateList();
    }

    private void editOpportunity(int index) {
        String name = opportunitiesAdapter.getItem(index);

        Map<String, ?> allEntries = opportunitiesInformation.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().split("\\|")[0].equals(name)) {
                name = entry.getKey();
                break;
            }
        }

        Intent myIntent = new Intent(OpportunitiesActivity.this, AddOpportunityActivity.class);
        myIntent.putExtra("Editor", true);
        myIntent.putExtra("Name", name);

        startActivity(myIntent);
    }

    private class OpportunitiesTaskCompleter implements OnTaskCompleted{

        @Override
        public void onTaskCompleted(Object result) {

            if (result.toString().equals("")) {
                Toast.makeText(context, getString(R.string.err_update), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, result.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
}