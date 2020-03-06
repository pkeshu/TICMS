package com.imaginology.texas.util;


import android.support.v7.app.ActionBar;
import android.util.Log;

public class SupportActionBarInitializer {

    public static void setUpSupportActionBar(ActionBar supportActionBar, String title, boolean showBackArrow){
        if(supportActionBar!=null){
            supportActionBar.setDisplayHomeAsUpEnabled(showBackArrow);
            supportActionBar.setDisplayShowHomeEnabled(showBackArrow);
            supportActionBar.setTitle(title);
        }else
            Log.d("SupportActBarInit","Provided action bar is null");
    }

    public static void setSupportActionBarTitle(ActionBar supportActionBar,String title){
        if(supportActionBar!=null){
            supportActionBar.setTitle(title);
        }else
            Log.d("SupportActBarInit","Provided action bar is null");
    }
}
