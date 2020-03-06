package com.imaginology.texas.Notification.OfflineNotification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.imaginology.texas.RoomDatabase.RoutineEntity.RoutineEntity;
import com.imaginology.texas.RoomDatabase.RoutineEntity.RoutineDao;
import com.imaginology.texas.RoomDatabase.TicmsRoomDatabase;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RoutineNotificationService extends Service {
    public static boolean isServiceStopRequired = false;
    public static boolean isServiceRunForFirstTie = true;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        isServiceStopRequired = false;
        runNotificationScheduler();
        return START_STICKY;
    }

    private void runNotificationScheduler() {

        GetLoginInstanceFromDatabase getLoginInstanceFromDatabase = new GetLoginInstanceFromDatabase(this);
        UserLoginResponseEntity loginInstance = getLoginInstanceFromDatabase.getLoginInstance();
        if (loginInstance == null) {
            stopSelf();
        }

        if (loginInstance != null)
            Log.d("In Service: ", loginInstance.getUserName());

        TicmsRoomDatabase ticmsRoomDatabase = TicmsRoomDatabase.getDatabaseInstance(this);
        RoutineDao routineDao = ticmsRoomDatabase.getRoutineDao();
        final List<RoutineEntity> routineList = new ArrayList<>();
        Runnable runnable = () -> {
            Calendar calendar = Calendar.getInstance();
            List<RoutineEntity> freshRoutine = new ArrayList<>();

            int i = 0;
            while (!isServiceStopRequired) {
                freshRoutine.clear();
                freshRoutine = routineDao.getAllRoutineFromDatabase();
                if (freshRoutine != null && !freshRoutine.isEmpty()) {
                    Log.d("Routine Lis : ", "not null");
                    routineList.clear();
                    routineList.addAll(freshRoutine);
                }

                if (isServiceRunForFirstTie) {
                    Calendar repetingTime = Calendar.getInstance();
                    repetingTime.add(Calendar.DAY_OF_WEEK, 1);
                    repetingTime.set(Calendar.HOUR_OF_DAY, 5);
                    repetingTime.set(Calendar.MINUTE, 0);
                    repetingTime.set(Calendar.SECOND, 0);

                    Intent repetingIntent = new Intent(this, MorningRoutineServiceStartReceiver.class);
                    PendingIntent broadcast = PendingIntent.getBroadcast
                            (RoutineNotificationService.this, i, repetingIntent, 0);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    if (alarmManager != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, repetingTime.getTimeInMillis(), broadcast);
                        } else {
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, repetingTime.getTimeInMillis(), broadcast);
                        }
                    }

                }


                for (RoutineEntity notificationRoutineCheckDTO : routineList) {
                    Log.d("DAy", notificationRoutineCheckDTO.getDay());
                    Log.d("System day", String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)));
                    if (calendar.get(Calendar.DAY_OF_WEEK) == getDayByName(notificationRoutineCheckDTO.getDay())) {
                        long routineTime = getMillisFromTime(notificationRoutineCheckDTO.getStartTime(), notificationRoutineCheckDTO.getDay());
                        long triggerTime = routineTime - 10 * 60 * 1000; // minute*second*millisecond
                        long currentTime = System.currentTimeMillis();
                        Log.d("Trigger Time: ", String.valueOf(triggerTime));
                        Log.d("System Time: ", String.valueOf(currentTime));
                        if (currentTime <= triggerTime) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                Log.d("Notification service: ", "Triggered");

                                Intent intent = new Intent(RoutineNotificationService.this, RoutineNotificationReceiver.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("Course", notificationRoutineCheckDTO.getCourse());
                                bundle.putString("Semester", notificationRoutineCheckDTO.getSemester());
                                bundle.putString("StartTime", notificationRoutineCheckDTO.getStartTime());
                                bundle.putString("Subject", notificationRoutineCheckDTO.getSubject());
                                bundle.putString("Teacher", loginInstance.getFirstName());
                                intent.putExtras(bundle);

                                PendingIntent broadcast = PendingIntent.getBroadcast
                                        (RoutineNotificationService.this, i, intent, 0);
                                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                if (alarmManager != null) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, broadcast);
                                    } else {
                                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, broadcast);
                                    }
                                }

                                i++;
                                if (i == 24)
                                    i = 0;
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(24 * 60 * 60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            (RoutineNotificationService.this).stopSelf();
        };
        Thread t = new Thread(runnable);
        t.start();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private int getDayByName(String dayName) {
        int x = 0;
        switch (dayName) {
            case "Sunday":
                x = 1;
                break;
            case "Monday":
                x = 2;
                break;
            case "Tuesday":
                x = 3;
                break;
            case "Wednesday":
                x = 4;
                break;
            case "Thursday":
                x = 5;
                break;
            case "Friday":
                x = 6;
                break;
            case "Saturday":
                x = 7;
                break;
        }
        return x;
    }

    private Long getMillisFromTime(String startTime, String day) {
        String[] time = startTime.split(":");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
        calendar.set(Calendar.MINUTE, Integer.valueOf(time[1]));
        calendar.set(Calendar.DAY_OF_WEEK, getDayByName(day));

        return calendar.getTimeInMillis();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("RoutineNotiServer: ", "Destroyed");
    }
}
