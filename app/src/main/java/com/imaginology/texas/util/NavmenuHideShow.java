package com.imaginology.texas.util;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.view.Menu;
import android.view.MenuItem;

import com.imaginology.texas.Login.LoginDto;
import com.imaginology.texas.MainActivity;
import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.UserTeamResponse.TeamEntity;
import com.imaginology.texas.Users.UserDto;

import java.util.ArrayList;
import java.util.List;

public class NavmenuHideShow {

    public static void showHideNavMenuAccordingToLoginRole(NavigationView navigationView, String loginType, Context context) {
        Menu menu = navigationView.getMenu();
        MenuItem login = menu.findItem(R.id.nav_login);
        MenuItem logout = menu.findItem(R.id.nav_logout);
        MenuItem sendNotification = menu.findItem(R.id.nav_send_notification);
        MenuItem classRoutine = menu.findItem(R.id.nav_classRoutine);
        MenuItem myRoutine = menu.findItem(R.id.nav_my_routine);
        MenuItem counseling = menu.findItem(R.id.nav_counseling);
        MenuItem dashboard = menu.findItem(R.id.nav_dashboard);
        MenuItem user = menu.findItem(R.id.nav_user);
        MenuItem students = menu.findItem(R.id.nav_student);
        MenuItem teachers = menu.findItem(R.id.nav_teacher);
        MenuItem courses = menu.findItem(R.id.nav_courses);
        MenuItem subjects = menu.findItem(R.id.nav_subjects);
        MenuItem team=menu.findItem(R.id.nav_team);
        login.setVisible(false);
        logout.setVisible(true);

        if(CheckTeamForConselling.CheckCounselling(context))
            counseling.setVisible(true);


        switch (loginType){
            case "ADMIN":
                dashboard.setVisible(true);
                sendNotification.setVisible(true);
                classRoutine.setVisible(true);
                user.setVisible(true);
                students.setVisible(true);
                teachers.setVisible(true);
                courses.setVisible(true);
                subjects.setVisible(true);
                team.setVisible(true);
                //Invisible item to admin
                myRoutine.setVisible(false);
                break;
            case "SUPERADMIN":
                dashboard.setVisible(true);
                sendNotification.setVisible(true);
                classRoutine.setVisible(true);
                user.setVisible(true);
                students.setVisible(true);
                teachers.setVisible(true);
                courses.setVisible(true);
                subjects.setVisible(true);
                team.setVisible(true);
                //Invisible item to Super admin
                myRoutine.setVisible(false);
                break;

            case "TEACHER":
                myRoutine.setVisible(true);
                user.setVisible(false);
                students.setVisible(false);
                teachers.setVisible(false);
                courses.setVisible(true);
                subjects.setVisible(false);

                //Invisible item to admin
                dashboard.setVisible(false);
                sendNotification.setVisible(false);
                classRoutine.setVisible(false);

                break;

            case "STUDENT":
                classRoutine.setVisible(true);
                dashboard.setVisible(false);
                user.setVisible(false);
                students.setVisible(false);
                teachers.setVisible(false);
                courses.setVisible(true);
                subjects.setVisible(true);

                //Invisible item to admin
                myRoutine.setVisible(false);
                sendNotification.setVisible(false);
                break;
            case "USER":
                classRoutine.setVisible(true);
                dashboard.setVisible(true);
                user.setVisible(true);
                students.setVisible(true);
                teachers.setVisible(true);
                courses.setVisible(true);
                subjects.setVisible(true);
                team.setVisible(true);

                //Invisible item to admin
                myRoutine.setVisible(false);
                sendNotification.setVisible(false);
                break;
            case "CUSTOMER":
                classRoutine.setVisible(true);
                dashboard.setVisible(true);
                user.setVisible(true);
                students.setVisible(true);
                teachers.setVisible(true);
                courses.setVisible(true);
                subjects.setVisible(true);
                sendNotification.setVisible(true);
                team.setVisible(true);



                //for invisible item
                myRoutine.setVisible(false);
                break;
            case "":
                classRoutine.setVisible(false);
                dashboard.setVisible(false);
                myRoutine.setVisible(false);
                sendNotification.setVisible(false);
                login.setVisible(true);
                logout.setVisible(false);
                user.setVisible(false);
                students.setVisible(false);
                teachers.setVisible(false);
                courses.setVisible(false);
                subjects.setVisible(false);



        }

        CheckTeamForConselling checkTeamForConselling = new CheckTeamForConselling();
        if(checkTeamForConselling.CheckTeam(context)) {
            classRoutine.setVisible(false);
            dashboard.setVisible(false);
            user.setVisible(false);
            students.setVisible(false);
            teachers.setVisible(false);
            courses.setVisible(false);
            subjects.setVisible(false);
            team.setVisible(false);

            //Invisible item to admin
            myRoutine.setVisible(false);
            sendNotification.setVisible(false);
        }

    }
}
