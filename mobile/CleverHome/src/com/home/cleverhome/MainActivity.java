package com.home.CleverHome;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class MainActivity extends ExpandableListActivity {
    private MainMenuListAdapter menuListAdapter;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        menuListAdapter = new MainMenuListAdapter(this);

        setListAdapter(menuListAdapter);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View view, int group, int child, long id) {
        MainMenuListAdapter.SampleItem sample = menuListAdapter.getChild(group, child);

        Intent intent = null;

        try {
            intent = new Intent(MainActivity.this, Class.forName(sample.className));
        } catch (ClassNotFoundException e) {
            showToast("Something went wrong...");
        }

        startActivity(intent);

        return super.onChildClick(parent, view, group, child, id);
    }

    private void showToast(String title) {
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
    }
}
