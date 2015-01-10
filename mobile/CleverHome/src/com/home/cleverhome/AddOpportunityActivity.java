package com.home.CleverHome;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Map;

/**
 * Created by ichuraev on 01/10/2015.
 */
public class AddOpportunityActivity extends Activity {

    private SharedPreferences opportunitiesInformation;
    private boolean editor = false;
    private EditText etName;
    private EditText etSubtitle;
    private EditText etCommands;
    private TextView errorText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.add_opportunity_activity);

        etName = (EditText)findViewById(R.id.new_opportunity_name);
        etSubtitle = (EditText)findViewById(R.id.new_opportunity_subtitle);
        etCommands = (EditText)findViewById(R.id.new_opportunity_commands);
        errorText = (TextView)findViewById(R.id.error_view);
        errorText.setTextColor(Color.RED);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.containsKey("Editor")) {
                editor = b.getBoolean("Editor", false);
            }
        }

        opportunitiesInformation = getSharedPreferences(OpportunitiesActivity.OPPORTUNITIES_INFO, 0);

        if (editor) {
            String name = b.getString("Name");
            etName.setText(name.split("\\|")[0]);

            if (name.split("\\|").length > 1) {
                etSubtitle.setText(name.split("\\|")[1]);
            } else {
                etSubtitle.setText(name.split("\\|")[0]);
            }

            etCommands.setText(opportunitiesInformation.getString(name, ""));
        }
    }

    public void onSave(View view) {
        if (etName.getText().toString().equals("") || etCommands.getText().toString().equals("")) {
            errorText.setText(getString(R.string.err_not_all_fields));
            return;
        }
        if (!editor) {
            String name = etName.getText().toString();
            Map<String, ?> allEntries = opportunitiesInformation.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                if (entry.getKey().split("\\|")[0].equals(name)) {
                    errorText.setText(getString(R.string.err_name_exists));
                    return;
                }
            }
        }


        SharedPreferences.Editor editor = opportunitiesInformation.edit();
        String subtitle = etSubtitle.getText().toString();
        if (subtitle.equals("")) {
            subtitle = etName.getText().toString();
        }
        editor.putString(etName.getText().toString() + "|" + subtitle, etCommands.getText().toString());
        editor.commit();
        finish();
    }

    public void onCancel(View view) {
        finish();
    }
}