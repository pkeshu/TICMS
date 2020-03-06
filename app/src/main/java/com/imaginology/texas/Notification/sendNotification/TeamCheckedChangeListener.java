package com.imaginology.texas.Notification.sendNotification;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.imaginology.texas.R;

import java.util.List;

public class TeamCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
    List<Long> teamIDs;
    CheckBox selectAllCheckBox;
    List<CheckBox> teamCheckBoxes;
    FloatingActionButton sendButton;
    Context mContext;

    public TeamCheckedChangeListener(Context mContext, List<Long> teamIDs, CheckBox selectAllCheckBox, List<CheckBox> teamCheckBoxes, FloatingActionButton sendButton) {
        this.mContext = mContext;
        this.teamIDs = teamIDs;
        this.selectAllCheckBox = selectAllCheckBox;
        this.teamCheckBoxes = teamCheckBoxes;
        this.sendButton = sendButton;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Long id = Long.valueOf(compoundButton.getId());
        if (isChecked) {
            teamIDs.add(id);
        } else {
            teamIDs.remove(id);
        }

        if (teamIDs.size()==0) {
            sendButton.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.color_light_grey)));
        } else {
            sendButton.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorAccent)));
        }

        System.out.println("called here");
        int count = 0;
        for (CheckBox checkBox : teamCheckBoxes) {
            if (!checkBox.isChecked()) {
                selectAllCheckBox.setChecked(false);
                count--;
            }else{
                count ++;
            }
        }
        System.out.println("called here2");
        if(count == teamCheckBoxes.size())
            selectAllCheckBox.setChecked(true);
        System.out.println("called here 3");
    }
}
