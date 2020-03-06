package com.imaginology.texas.util;

import android.util.Log;

public class CheckError {

    public static String checkNullString(String text){
        if(text!=null && !text.equals("") && !text.isEmpty()){
            Log.d("Value is not null : ", text);
            return text;

        }else{
            Log.d("Value is: ", "Null");
            return "Not Available";
        }
    }
}
