package com.home.CleverHome;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends Activity {
    public static final String PREFS_NAME = "main_conf";
    private ImageView btn_enter;
    private EditText et_password;
    private String app_password;
    private int error_number = 0;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        setContentView(R.layout.main);

        et_password = (EditText)findViewById(R.id.password_place);

        btn_enter = (ImageView)findViewById(R.id.btn_enter);

        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (app_password.equals(et_password.getText().toString()) && !app_password.equals("")) {
                    //Intent myIntent = new Intent(MainActivity.this, MenuActivity.class);
                    Intent myIntent = new Intent(MainActivity.this, MainMenuActivity.class);
                    //myIntent.putExtra("FIRST_RUN", true); //Optional parameters
                    MainActivity.this.startActivity(myIntent);
                    finish();
                } else {
                    et_password.setText("");
                    et_password.setHintTextColor(Color.RED);
                    et_password.setHint(getString(R.string.hint_incorrect_password));
                    error_number++;
                    if (error_number > 2) {
                        finish();
                    }
                }
            }
        });
    }

    public void onResume() {
        super.onResume();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("app_password", "aaa");

        editor.putString("bot_email_address", "AAAAAAAAAAAAAAA");
        editor.putString("email_address", "ilyachur@gmail.com");
        editor.putString("email_password", "AAAA");

        editor.commit();

        app_password = settings.getString("app_password", "");
        if (app_password.equals("")) {
            Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
            myIntent.putExtra("FIRST_RUN", true); //Optional parameters
            MainActivity.this.startActivity(myIntent);
            finish();
        }
    }
}
