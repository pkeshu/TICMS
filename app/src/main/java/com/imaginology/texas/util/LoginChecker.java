package com.imaginology.texas.util;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginChecker {

    public static Boolean IsLoggedIn(Context mContext) {
        Boolean isLoggedIn=false;
        SharedPreferences sharedPref = mContext.getSharedPreferences("loginDetails", Context.MODE_PRIVATE);
        isLoggedIn = !sharedPref.getString("token", "").equals("");
        return isLoggedIn;
    }
}
