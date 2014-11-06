package com.home.CleverHome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/**
 * Created by ichuraev on 06.11.14.
 */
public class MainMenuListAdapter extends BaseExpandableListAdapter {
    private Context context;

    private SampleGroup[] sampleGroups = {
            SampleGroup.HOME,
            SampleGroup.TEST,
    };

    public MainMenuListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public SampleItem getChild(int group, int child) {
        return sampleGroups[group].samples[child];
    }

    @Override
    public long getChildId(int group, int child) {
        return group;
    }

    @Override
    public View getChildView(int group, int child, boolean isLastChild, View convertView, ViewGroup parent) {
        final View childView;

        if (convertView != null) {
            childView = convertView;
        }
        else {
            childView = LayoutInflater.from(context).inflate(R.layout.menu_list_item, null);
        }

        ((TextView)childView.findViewById(R.id.itemTitle)).setText(context.getResources().getString(this.getChild(group, child).titleId));

        return childView;
    }

    @Override
    public int getChildrenCount(int group) {
        return sampleGroups[group].samples.length;
    }

    @Override
    public SampleGroup getGroup(int group) {
        return sampleGroups[group];
    }

    @Override
    public int getGroupCount() {
        return sampleGroups.length;
    }

    @Override
    public long getGroupId(int group) {
        return group;
    }

    @Override
    public View getGroupView(int group, boolean isExpanded, View convertView, ViewGroup parent) {
        final View groupView;

        if (convertView != null) {
            groupView = convertView;
        }
        else {
            groupView = LayoutInflater.from(context).inflate(R.layout.menu_list_group, null);
        }

        ((TextView)groupView.findViewById(R.id.itemTitle)).setText(context.getResources().getString(getGroup(group).titleId));

        return groupView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public enum SampleItem
    {
        STATUS(R.string.HOME_STATUS, MainActivity.class.getName()),
        POWER(R.string.HOME_POWER, MainActivity.class.getName()),

        TEST(R.string.TEST_SUB, MainActivity.class.getName());

        public String className;
        public int titleId;

        private SampleItem(int titleId, String className) {
            this.className = className;
            this.titleId = titleId;
        }
    }

    public enum SampleGroup
    {
        HOME(R.string.HOME,
                SampleItem.STATUS,
                SampleItem.POWER),

        TEST(R.string.TEST,
                SampleItem.TEST);

        public int titleId;
        public SampleItem[] samples;

        private SampleGroup(int titleId, SampleItem ... samples) {
            this.titleId = titleId;
            this.samples = samples;
        }
    }
}
