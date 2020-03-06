package com.imaginology.texas.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.imaginology.texas.RoomDatabase.TicmsRoomDatabase;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseDao;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.RoomDatabase.UserTeamResponse.TeamDao;
import com.imaginology.texas.RoomDatabase.UserTeamResponse.TeamEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GetLoginInstanceFromDatabase {
    private Long userId;
    private  Context mContext;

    public GetLoginInstanceFromDatabase(Context mContext) {
        this.mContext=mContext;
    }

    public UserLoginResponseEntity getLoginInstance() {
        UserLoginResponseEntity loginResponseEntity = new UserLoginResponseEntity();
        try {
           loginResponseEntity= new LoginInstanceAsync().execute().get();
//           Log.d("getLoginInstance", loginResponseEntity.getUserName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return loginResponseEntity;
    }
    public List<TeamEntity> getTeamInstance(){
        List<TeamEntity> teamEntity=new ArrayList<>();

        try {
            teamEntity=new TeamInstanceAsync().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return teamEntity;

    }
    private class TeamInstanceAsync extends AsyncTask<Void,Void, List<TeamEntity>>{
        TicmsRoomDatabase ticmsRoomDatabase=TicmsRoomDatabase.getDatabaseInstance(mContext);
        TeamDao teamDao=ticmsRoomDatabase.getTeamDao();


        @Override
        protected List<TeamEntity> doInBackground(Void... voids) {
            return teamDao.getAllTeamFromDatabase();
        }

    }

    private class LoginInstanceAsync extends AsyncTask<Void, Void, UserLoginResponseEntity> {
        TicmsRoomDatabase ticmsRoomDatabase= TicmsRoomDatabase.getDatabaseInstance(mContext);
        UserLoginResponseDao userLoginResponseDao= ticmsRoomDatabase.userLoginResponseDao();

        @Override
        protected UserLoginResponseEntity doInBackground(Void... voids) {

//            Log.d("InBackground ", userLoginResponseDao.getLoginInstance().getUserName());
            return userLoginResponseDao.getLoginInstance();
        }
    }

}
