package com.home.CleverHome;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.w3c.dom.Text;

/**
 * Created by ichuraev on 06.11.14.
 */
public class SettingsActivity extends Activity {

    private EditText old_pass, new_pass, confirm_pass, email_address, email_password, bot_email_address;
    private TextView error_msg;
    private boolean first = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.containsKey("FIRST_RUN")) {
                first = b.getBoolean("FIRST_RUN", false);
            }
        }

        error_msg = (TextView)findViewById(R.id.error_view);
        error_msg.setTextColor(Color.RED);

        old_pass = (EditText)findViewById(R.id.old_password);
        new_pass = (EditText)findViewById(R.id.new_password);
        confirm_pass = (EditText)findViewById(R.id.confim_new_password);
        email_address = (EditText)findViewById(R.id.email_address);
        bot_email_address = (EditText)findViewById(R.id.bot_email_address);
        email_password = (EditText)findViewById(R.id.email_password);

        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);

        email_address.setText(settings.getString("email_address", ""));
        bot_email_address.setText(settings.getString("bot_email_address", ""));
        email_password.setText(settings.getString("email_password", ""));

        Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (canCancel()) {
                    if (first) {
                        Intent myIntent = new Intent(SettingsActivity.this, MenuActivity.class);
                        SettingsActivity.this.startActivity(myIntent);
                    }
                    finish();
                } else {
                    error_msg.setText(getString(R.string.err_not_all_fields));
                }
            }
        });

        Button btn_save = (Button)findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
                String err_msg = "";
                String app_password_str = settings.getString("app_password", "");
                if (!new_pass.getText().toString().equals("") || !confirm_pass.getText().toString().equals("")) {
                    if (new_pass.getText().toString().equals(confirm_pass.getText().toString())) {
                        if (!old_pass.getText().toString().equals(app_password_str)) {
                            err_msg = getString(R.string.err_incorrect_old_pwd);
                        }
                    } else {
                        err_msg = getString((R.string.err_confirm_failed));
                    }
                }

                if (email_address.getText().toString().equals("")) {
                    err_msg = getString(R.string.err_email_address_empty);
                }
                if (bot_email_address.getText().toString().equals("")) {
                    err_msg = getString(R.string.err_bot_email_address_empty);
                }

                if (email_password.getText().toString().equals("")) {
                    err_msg = getString(R.string.err_email_pwd_empty);
                }

                if (first && (new_pass.getText().toString().equals("") || confirm_pass.getText().toString().equals(""))){
                    err_msg = getString(R.string.err_first_app_run_pwd);
                }

                if (err_msg.equals("")) {
                    SharedPreferences.Editor editor = settings.edit();
                    if (!new_pass.getText().toString().equals("")) {
                        editor.putString("app_password", new_pass.getText().toString());
                    }

                    editor.putString("bot_email_address", bot_email_address.getText().toString());
                    editor.putString("email_address", email_address.getText().toString());
                    editor.putString("email_password", email_password.getText().toString());

                    editor.commit();


                    if (first) {
                        Intent myIntent = new Intent(SettingsActivity.this, MenuActivity.class);
                        SettingsActivity.this.startActivity(myIntent);
                    }
                    finish();
                } else {
                    error_msg.setText(err_msg);
                }
            }
        });

        if (first) {
            btn_cancel.setEnabled(false);
            old_pass.setEnabled(false);
            old_pass.setFocusable(false);
        }
    }

    boolean canCancel() {
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);

        String app_password = settings.getString("app_password", "");
        String email_address = settings.getString("email_address", "");
        String email_password = settings.getString("email_password", "");
        if (app_password.equals("") || email_address.equals("") || email_password.equals(""))
            return false;
        return true;
    }
}