package com.imaginology.texas.util;

import android.text.TextUtils;
import android.widget.EditText;

public class DataValidityChecker {
    public static boolean isEditTextDataValid(EditText editText, String editTextLabel,boolean doTrim){
       if(doTrim){
           if(!TextUtils.isEmpty(editText.getText().toString().trim())){
               return true;
           }else {
               editText.setError(editTextLabel+" Required!!!");
               editText.requestFocus();
           }
       }else {
           if(!TextUtils.isEmpty(editText.getText().toString())){
               return true;
           }else {
               editText.setError(editTextLabel+" Required!!!");
               editText.requestFocus();
           }
       }

       return false;
    }
}
