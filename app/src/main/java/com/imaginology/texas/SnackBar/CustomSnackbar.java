package com.imaginology.texas.SnackBar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginology.texas.R;
import com.imaginology.texas.util.CheckInternet;

public class CustomSnackbar {
    public static void showSuccessSnakeBar(View view, String message, Context context){
        android.support.design.widget.Snackbar snackbar= android.support.design.widget.Snackbar.make(view,message, android.support.design.widget.Snackbar.LENGTH_SHORT);
        ViewGroup v= (ViewGroup) snackbar.getView();

        v.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        v.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        TextView textView = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(context.getResources().getColor(R.color.colorWhite));
        textView.setTextSize(16);
        snackbar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.setActionTextColor(context.getResources().getColor(R.color.colorfade));
        snackbar.show();

    }
    public static void showFailureSnakeBar(View view, String message, Context context){
        android.support.design.widget.Snackbar snackbar= android.support.design.widget.Snackbar.make(view,message, android.support.design.widget.Snackbar.LENGTH_SHORT);
        ViewGroup v= (ViewGroup) snackbar.getView();

        v.setBackgroundColor(context.getResources().getColor(R.color.colorfade));
        v.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        TextView textView = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(context.getResources().getColor(R.color.colorWhite));
        textView.setTextSize(16);
        snackbar.setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.setActionTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        snackbar.show();

    }
    public static void checkErrorResponse(View layoutView, final Context context) {
        CheckInternet checkInternet = new CheckInternet();
        if(context!=null){
            if (!checkInternet.isNetworkAvailable(context)) {
                Snackbar snackbar = Snackbar
                        .make(layoutView, R.string.wifi_off_msg, Snackbar.LENGTH_LONG)
                        .setActionTextColor(context.getResources().getColor(R.color.colorWhite))
                        .setAction(R.string.goto_wifi_setting, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ((Activity)context).startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            }
                        });
                snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
                snackbar.show();

            } else if (!(checkInternet.isOnline())) {
                Toast.makeText(context, R.string.no_internet_available, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.something_worng_with_server, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
