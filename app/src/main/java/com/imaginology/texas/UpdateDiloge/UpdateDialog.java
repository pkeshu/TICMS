package com.imaginology.texas.UpdateDiloge;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.imaginology.texas.BuildConfig;

import java.util.HashMap;
import java.util.Map;

public class UpdateDialog extends Application {
    private String TAG = "com.imaginology.texas.UpdateDiloge.UpdateDialog";
    private FirebaseRemoteConfig firebaseRemoteConfig;
    public static final String VERSION_CODE_KEY = "com.imaginology.texas.UpdateDiloge.versionKey";

    @Override
    public void onCreate() {
        super.onCreate();

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        Map<String, Object> defaultValue = new HashMap<>();
        defaultValue.put(VERSION_CODE_KEY, getCurrentVersionCode());
        firebaseRemoteConfig.setDefaults(defaultValue);
        firebaseRemoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build());
        firebaseRemoteConfig.fetch(5)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        firebaseRemoteConfig.activateFetched();
                        if (task.isSuccessful()) {
                            firebaseRemoteConfig.activateFetched();
                            Log.d(TAG, "Someting went wrong please try again");
                            //calling function to check if new version is available or not
                            checkForUpdate();
                        } else
                            Log.d(TAG, "Default value: " + firebaseRemoteConfig.getString(VERSION_CODE_KEY));
                    }
                });
        Log.d(TAG, "Default value: " + firebaseRemoteConfig.getString(VERSION_CODE_KEY));

    }

    private void checkForUpdate() {
        int latestAppVersion = (int) firebaseRemoteConfig.getDouble(VERSION_CODE_KEY);
        if (latestAppVersion > getCurrentVersionCode()) {
            new AlertDialog.Builder(this).setTitle("Please Update the UpdateDialog")
                    .setMessage("A new version of this app is available. Please update it").setPositiveButton(
                    "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast
                                    .makeText(getApplicationContext(), "Take user to Google Play Store", Toast.LENGTH_SHORT)
                                    .show();
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.imaginology.texas"));
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), "proceed", Toast.LENGTH_SHORT).show();
                        }

                    }).setCancelable(false).show();
        } else {
            Log.d(TAG, "This app is already upto date");
        }
    }

    private int getCurrentVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
