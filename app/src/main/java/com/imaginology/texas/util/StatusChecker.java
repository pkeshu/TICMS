package com.imaginology.texas.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.imaginology.texas.MainActivity;
import com.imaginology.texas.RoomDatabase.RoutineEntity.RoutineDao;
import com.imaginology.texas.RoomDatabase.TicmsRoomDatabase;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseDao;
import com.imaginology.texas.RoomDatabase.UserTeamResponse.TeamDao;

public class StatusChecker {

    public static void statusCheck(Context context) {
        Context mContext;
        mContext = context;

        //Toast.makeText(mContext, "Your token has been expired and you need to login again.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(mContext, MainActivity.class);
        //Clearing sharedpreferences after token expires
        SharedPreferences sharedPref = mContext.getSharedPreferences("loginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", "");
        editor.putInt("id",0);
        editor.putString("loginType", "");
        editor.putString("firstname", "");
        editor.putString("lastname", "");
        editor.putString("email", "");
        editor.putString("userrole", "");
        editor.putString("username", "");
        editor.putString("profilePicture", "");
        editor.apply();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //Roomdatabase clearing code
        TicmsRoomDatabase ticmsRoomDatabase = TicmsRoomDatabase.getDatabaseInstance(context);
        UserLoginResponseDao userLoginResponseDao = ticmsRoomDatabase.userLoginResponseDao();
        RoutineDao routineDao = ticmsRoomDatabase.getRoutineDao();
        TeamDao teamDao=ticmsRoomDatabase.getTeamDao();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                userLoginResponseDao.deleteAllLoginInstances();
                routineDao.deleteAllRoutineFromDatabse();
                teamDao.deleteAllTeamFromDatabse();
                return null;
            }
        }.execute();
        mContext.startActivity(intent);
        ((Activity)mContext).finish();
    }

}
