package com.imaginology.texas;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imaginology.texas.ClassRoutine.ClassRoutineFragment;
import com.imaginology.texas.Counseling.Counseling_Fragment;
import com.imaginology.texas.Courses.CourseListFragment;
import com.imaginology.texas.Dashboard.DashboardFragment;
import com.imaginology.texas.Dashboard.Teacher.TeacherDashboard;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.Login.LoginFragment;
import com.imaginology.texas.Logout.LogoutDto;
import com.imaginology.texas.NavigationViewItems.Contact.ContactFragment;
import com.imaginology.texas.NavigationViewItems.News.NewsFragment;
import com.imaginology.texas.Notification.OfflineNotification.RoutineNotificationReceiver;
import com.imaginology.texas.Notification.PushNotification.AllNotification;
import com.imaginology.texas.Notification.PushNotification.NotificationDto;
import com.imaginology.texas.Notification.sendNotification.SendNotificationActivity;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.Routine.Util.NotificationChannelCreator;
import com.imaginology.texas.SnackBar.CustomSnackbar;
import com.imaginology.texas.Students.StudentListFragment;
import com.imaginology.texas.Subjects.SubjectListFragment;
import com.imaginology.texas.Teachers.TeacherListFragment;
import com.imaginology.texas.Team.TeamListFragment;
import com.imaginology.texas.UpdateDiloge.UpdateDialog;
import com.imaginology.texas.Users.UserListFragment;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;
import com.imaginology.texas.util.ImageViewLoader;
import com.imaginology.texas.util.LoginChecker;
import com.imaginology.texas.util.NavmenuHideShow;
import com.imaginology.texas.util.StatusChecker;
import com.imaginology.texas.util.SupportActionBarInitializer;

import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private Fragment fragment;
    boolean doubleBackToExitPressedOnce = false;
    private TextView navTvUserName, navTvEmail, navTvLogin;
    private ImageView navIvUserDp;
    private ConstraintLayout nav_header;
    private ApiInterface apiInterface;
    private TextView notificationCountTxt;
    private ImageView notificationImage;
    private ImageView mUserProfile;
    private DrawerLayout drawer;
    private String navTvUsrUsername, navTvUserEmail, navTvUserImageUrl;
    private SharedPreferences sharedPref;
    private MenuItem showNotification, userProfile;
    private TextView titleNotiDialogView, messageNotiDialogView;
    private Button okNotiDialogBtn;
    private UserLoginResponseEntity loginInstance;
    private UpdateDialog app;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = new UpdateDialog();
//update check dialog
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        apiInterface = ApiClient.getRetrofit(this).create(ApiInterface.class);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        View mView = navigationView.getHeaderView(0);
        nav_header = mView.findViewById(R.id.nav_header);
        nav_header.setScrollContainer(true);
        nav_header.setOnClickListener(view -> {
            if (LoginChecker.IsLoggedIn(MainActivity.this)) {
                startActivity(new Intent(MainActivity.this, CurrentUserProfileActivity.class));
            } else {
                drawer.closeDrawer(Gravity.START);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, new LoginFragment());
                ft.addToBackStack("previous");
                ft.commit();
            }
        });

        navTvUserName = mView.findViewById(R.id.username1);
        navTvEmail = mView.findViewById(R.id.email1);
        navIvUserDp = mView.findViewById(R.id.image);
        navTvLogin = mView.findViewById(R.id.loginfirst);
        navigationView.setNavigationItemSelectedListener(this);
        //If started from the offline notification fragment then lunch Teacher dashboard
        Intent intent = getIntent();
        if (intent != null) {
            Log.d("Inside intent check", "True");
            onNewIntent(intent);
            Log.d("Inside intent check end", "True");

        } else {
            Log.d("Inside else intent chk", "True");
            displaySelectedScreen(R.id.nav_home);
        }
        sharedPref = getSharedPreferences("loginDetails", Context.MODE_PRIVATE);

        //Create Channel for Notification for android version equal or greater than Oreo
        NotificationChannelCreator.createNotificationChannel(this, getString(R.string.default_notification_channel_id));

        //Getting fcm token from firebase
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            String fcmToken = instanceIdResult.getToken();
            Log.e("NewFCMToken", "Generated FCM Token:" + fcmToken);

            SharedPreferences sharedPref = getSharedPreferences("DeviceToken", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("token", fcmToken);
            editor.apply();
        }).addOnFailureListener(this, e -> {
            Log.e("NewFCMToken", "Failed to Generate FCM Token");

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        NavmenuHideShow.showHideNavMenuAccordingToLoginRole(navigationView, sharedPref.getString("loginType", ""), this);

        navTvUsrUsername = sharedPref.getString("firstname", "") + " " + sharedPref.getString("lastname", "");
        navTvUserEmail = sharedPref.getString("email", "");
        navTvUserImageUrl = sharedPref.getString("profilePicture", "Not Available");


        if (LoginChecker.IsLoggedIn(this)) {
            if (navTvUserName.getVisibility() != View.VISIBLE || navTvEmail.getVisibility() != View.VISIBLE) {
                navTvUserName.setVisibility(View.VISIBLE);
                navTvEmail.setVisibility(View.VISIBLE);
            }
            navTvUserName.setText(navTvUsrUsername);
            navTvEmail.setText(navTvUserEmail);
            callServerForUnseenNotification();

        } else {
            if (navTvUserName.getVisibility() != View.GONE || navTvEmail.getVisibility() != View.GONE) {
                navTvUserName.setVisibility(View.GONE);
                navTvEmail.setVisibility(View.GONE);
            }
            if (navTvLogin.getVisibility() != View.VISIBLE) {
                navTvLogin.setVisibility(View.VISIBLE);
            }


        }
        ImageViewLoader.loadImage(MainActivity.this, navTvUserImageUrl, navIvUserDp);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        showNotification = menu.findItem(R.id.act_notification);
        userProfile = menu.findItem(R.id.profile);
        View showNotificationView = showNotification.getActionView();
        View userProfileView = userProfile.getActionView();
        notificationImage = showNotificationView.findViewById(R.id.notification_image);
        notificationCountTxt = showNotificationView.findViewById(R.id.notification_badge);
        mUserProfile = userProfileView.findViewById(R.id.userprofile);
        //Check if logged in or not and hide the notification icon if not logged in
        if (!("".equals(sharedPref.getString("token", "")))) {
            showNotification.setVisible(true);
            userProfile.setVisible(true);
            String profilePUrl = sharedPref.getString("profilePicture", "Not Available");
//            Intent intent=getIntent();
//            String profilePUrlFromCUPA=intent.getExtras().getString("imageUrl");
//            if(!profilePUrlFromCUPA.equals("notAvailable")){
//                profilePUrl=profilePUrlFromCUPA;
//            }
            ImageViewLoader.loadImage(this, profilePUrl/*sharedPref.getString("profilePicture", "Not Available")*/, mUserProfile);
            mUserProfile.setOnClickListener(view -> {
                Intent profileIntent = new Intent(this, CurrentUserProfileActivity.class);
                startActivity(profileIntent);
            });
            callServerForUnseenNotification();
        } else {
            showNotification.setVisible(false);

        }

        notificationImage.setOnClickListener(view -> {
            Intent notificationIntent = new Intent(MainActivity.this, AllNotification.class);
            startActivity(notificationIntent);
        });

        notificationCountTxt.setOnClickListener(view -> {
            Intent notificationIntent = new Intent(MainActivity.this, AllNotification.class);
            startActivity(notificationIntent);
        });

        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        displaySelectedScreen(item.getItemId());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if ((id == R.id.act_notification)) {

            Intent showNotifications = new Intent(this, AllNotification.class);
            startActivity(showNotifications);
        }
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Settings.class));
        }

        if (id == R.id.profile) {
            startActivity(new Intent(this, CurrentUserProfileActivity.class));

        }

        return true;
    }


    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_home:
                fragment = new MainFragment();
                SupportActionBarInitializer.setSupportActionBarTitle(getSupportActionBar(), "Home");
                break;
            case R.id.nav_dashboard:
                fragment = new DashboardFragment();
                SupportActionBarInitializer.setSupportActionBarTitle(getSupportActionBar(), "Dashboard");
                break;
            case R.id.nav_team:
                fragment = new TeamListFragment();
                SupportActionBarInitializer.setSupportActionBarTitle(getSupportActionBar(), "All Team");
                break;
            case R.id.nav_my_routine:
                fragment = new TeacherDashboard();
                SupportActionBarInitializer.setSupportActionBarTitle(getSupportActionBar(), "Your Routine");
                break;
            case R.id.nav_news:
                fragment = new NewsFragment();
                SupportActionBarInitializer.setSupportActionBarTitle(getSupportActionBar(), "News");
                break;
            case R.id.nav_send_notification:
                startActivity(new Intent(this, SendNotificationActivity.class));
                break;
            case R.id.nav_login:
                fragment = new LoginFragment();
                SupportActionBarInitializer.setSupportActionBarTitle(getSupportActionBar(), "Login");
                break;
            case R.id.nav_contact:
                fragment = new ContactFragment();
                SupportActionBarInitializer.setSupportActionBarTitle(getSupportActionBar(), "About");
                break;
            case R.id.nav_classRoutine:
                fragment = new ClassRoutineFragment();
                SupportActionBarInitializer.setSupportActionBarTitle(getSupportActionBar(), "Class Routine");
                break;
            case R.id.nav_counseling:
                fragment = new Counseling_Fragment();
                SupportActionBarInitializer.setSupportActionBarTitle(getSupportActionBar(), "Counselling");
                break;
//            case R.id.nav_about:
//                fragment = new AboutFragment();
//                SupportActionBarInitializer.setSupportActionBarTitle(getSupportActionBar(), "About Us");
//                break;
            case R.id.nav_logout:

                GetLoginInstanceFromDatabase loginInstanceAccessor = new GetLoginInstanceFromDatabase(MainActivity.this);
                UserLoginResponseEntity loginInstance = loginInstanceAccessor.getLoginInstance();

                String token = loginInstance.getToken();
                long loginId = loginInstance.getLoginId();

                LogoutDto logoutDto = new LogoutDto(token);

                Call<ResponseBody> call = apiInterface.logout(loginId, logoutDto);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        System.out.println("Response for logout====="+response.code());

                        if (response.isSuccessful()) {
                            StatusChecker.statusCheck(MainActivity.this);
                            int notif = 0;
                            NotificationBadger.setBadge(MainActivity.this, notif);

                            //Clear the user Id and token from shared preferences

                            //Cancel all scheduled routine notification
                            for (int i = 0; i < 25; i++) {
                                Intent intent = new Intent(MainActivity.this, RoutineNotificationReceiver.class);
                                PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, i, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                pendingIntent.cancel();
                                alarmManager.cancel(pendingIntent);
                            }


                        } else {

                            Toast.makeText(MainActivity.this, "Connection interrupted,Please try again", Toast.LENGTH_SHORT).show();
//                            //Maps the error message in ErrorMessageDto
//                            JsonParser parser = new JsonParser();
//                            JsonElement mJson = null;
//                            try {
//                                mJson = parser.parse(response.errorBody().string());
//                                Gson gson = new Gson();
//                                ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
//                                Toast.makeText(MainActivity.this, errorMessageDto.getMessage(), Toast.LENGTH_SHORT).show();
//                                StatusChecker.statusCheck(MainActivity.this);
//                            } catch (IOException ex) {
//                                ex.printStackTrace();
//                            }
//                            Toast.makeText(MainActivity.this, "Hello Surya.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Connection interrupted,Please try again", Toast.LENGTH_SHORT).show();
                    }
                });

                break;

            case R.id.nav_user:
                fragment = new UserListFragment();
                SupportActionBarInitializer.setSupportActionBarTitle(getSupportActionBar(), "Users");
                break;

            case R.id.nav_student:
                fragment = new StudentListFragment();
                SupportActionBarInitializer.setSupportActionBarTitle(getSupportActionBar(), "Students");
                break;

            case R.id.nav_teacher:
                fragment = new TeacherListFragment();
                SupportActionBarInitializer.setSupportActionBarTitle(getSupportActionBar(), "Teachers");
                break;

//            case R.id.nav_routine:
//                fragment = new ClassRoutineFragment();
//                SupportActionBarInitializer.setSupportActionBarTitle(getSupportActionBar(),"Routine");
//                break;

            case R.id.nav_courses:
                fragment = new CourseListFragment();
//                SupportActionBarInitializer.setSupportActionBarTitle(getSupportActionBar(), "Courses");
                break;

            case R.id.nav_subjects:
                fragment = new SubjectListFragment();
                SupportActionBarInitializer.setSupportActionBarTitle(getSupportActionBar(), "Subjects");
                break;


        }

        //replacing the fragment
        if (fragment != null)

        {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            /*if(!fragment.equals(MainFragment.class)){
                ft.addToBackStack("previous");
            }*/
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null && extras.containsKey("FromOfflineNotification")) {
            showMessage("Routine Alert!!!", extras.getString("NotificationBody", ""));
            fragment = new TeacherDashboard();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            SupportActionBarInitializer.setSupportActionBarTitle(getSupportActionBar(), "Your Routine");
            ft.commit();
        } else
            displaySelectedScreen(R.id.nav_login);
    }

    @Override
    public void onBackPressed() {
        //Checking for fragment count on backstack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        } else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        } else {
            super.onBackPressed();
            return;
        }
    }

    private void showMessage(String title, String message) {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_desigh_to_show_notification_detail, null);
        dialogBuilder.setView(dialogView);

        titleNotiDialogView = dialogView.findViewById(R.id.custom_alert_notificaton_title);
        messageNotiDialogView = dialogView.findViewById(R.id.custom_slert_notificaton_message);
        titleNotiDialogView.setText(title);
        messageNotiDialogView.setText(message);

        okNotiDialogBtn = dialogView.findViewById(R.id.edit_notification_button);
        final AlertDialog alertDialog = dialogBuilder.create();
        Objects.requireNonNull(alertDialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimationUpBottom;

        alertDialog.show();
        okNotiDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void showNotificationNumber(int totalUnseenNotification) {

        if (notificationCountTxt != null) {
            if (totalUnseenNotification == 0) {
                if (notificationCountTxt.getVisibility() != View.GONE) {
                    notificationCountTxt.setVisibility(View.GONE);
                }
            } else {
                notificationCountTxt.setText(String.valueOf(Math.min(totalUnseenNotification, 99)));
                if (notificationCountTxt.getVisibility() != View.VISIBLE) {
                    notificationCountTxt.setVisibility(View.VISIBLE);
                    notificationCountTxt.setText(String.valueOf(totalUnseenNotification));
                    int notif = totalUnseenNotification;
                    NotificationBadger.setBadge(this, notif);
                }
            }
        }

    }

    private void callServerForUnseenNotification() {
        //Getting loginInstance from database
        GetLoginInstanceFromDatabase loginInstanceAccessor = new GetLoginInstanceFromDatabase(MainActivity.this);
        UserLoginResponseEntity loginInstance = loginInstanceAccessor.getLoginInstance();

        Call<NotificationDto> call = apiInterface.listNotification(loginInstance.getLoginId(),
                loginInstance.getCustomerId(), loginInstance.getToken(), null, null, "id,asc");
        call.enqueue(new Callback<NotificationDto>() {
            @Override
            public void onResponse(Call<NotificationDto> call, Response<NotificationDto> response) {

                if (response.isSuccessful()) {
                    try {
                        NotificationDto notificationDto = response.body();
                        showNotificationNumber(notificationDto.getTotalUnseenNotifications());
                        int notif = notificationDto.getTotalUnseenNotifications();
                        NotificationBadger.setBadge(MainActivity.this, notif);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    //Maps the error message in ErrorMessageDto
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = null;
                    try {

                        mJson = parser.parse(response.errorBody().string());
                        Gson gson = new Gson();
                        ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
//                        Toast.makeText(MainActivity.this, errorMessageDto.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<NotificationDto> call, Throwable t) {
               // Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                CustomSnackbar.checkErrorResponse(notificationCountTxt, MainActivity.this);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}

