package com.imaginology.texas.util;

import android.content.Context;

import com.imaginology.texas.RoomDatabase.UserTeamResponse.TeamEntity;

import java.util.ArrayList;
import java.util.List;

public class CheckTeamForConselling {
    public static boolean CheckCounselling(Context context) {
        GetLoginInstanceFromDatabase getLoginInstanceFromDatabase = new GetLoginInstanceFromDatabase(context);
//        List<TeamEntity> teamEntity=getLoginInstanceFromDatabase.getTeamInstance();
        List<TeamEntity> teamEntity = new ArrayList<>();
        teamEntity.addAll(getLoginInstanceFromDatabase.getTeamInstance());
        boolean isTrue = false;
        if (!teamEntity.isEmpty()) {
            for (TeamEntity teamEntity1 : teamEntity) {
                if (teamEntity1.getType().equalsIgnoreCase("Counselling")) {
                    isTrue = true;
                    break;

                } else {
                    isTrue = false;
//                    break;
                }

            }
        } else {
            isTrue = false;
        }
        return isTrue;

    }

    public boolean CheckTeam(Context context) {
        GetLoginInstanceFromDatabase getLoginInstanceFromDatabase = new GetLoginInstanceFromDatabase(context);
        List<TeamEntity> teamEntity = new ArrayList<>();
        teamEntity.addAll(getLoginInstanceFromDatabase.getTeamInstance());
        boolean isTrue = false;
        if (teamEntity.isEmpty()) {
            isTrue = true;
        }
        return isTrue;


    }


}
