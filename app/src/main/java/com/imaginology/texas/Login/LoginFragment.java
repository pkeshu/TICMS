package com.imaginology.texas.Login;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imaginology.texas.MainFragment;
import com.imaginology.texas.SnackBar.CustomSnackbar;
import com.imaginology.texas.Team.TeamDto;
import com.imaginology.texas.RoomDatabase.UserTeamResponse.TeamDao;
import com.imaginology.texas.RoomDatabase.UserTeamResponse.TeamEntity;
import com.imaginology.texas.Dashboard.DashboardFragment;
import com.imaginology.texas.Dashboard.Teacher.TeacherDashboard;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.TicmsRoomDatabase;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseDao;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;
import com.imaginology.texas.util.NavmenuHideShow;
import com.imaginology.texas.util.SupportActionBarInitializer;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginFragment extends Fragment {
    private EditText mEmail, mPassword;
    private Button mLogin;
    private AlertDialog mAlertDialog;
    private LinearLayout mainLayout;
    private NavigationView navigationView;
    private ProgressBar loginProgress;
    private TextView tvNoAcc;
    private LinearLayout linearLayout;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        navigationView = getActivity().findViewById(R.id.nav_view);
        SupportActionBarInitializer.setSupportActionBarTitle( ((AppCompatActivity) getActivity()).getSupportActionBar(),"Login");
        init(view);
        return view;


    }

    private void init(View view) {
        mEmail = view.findViewById(R.id.email);
        mPassword = view.findViewById(R.id.password);
        mLogin = view.findViewById(R.id.btnlogin);
        loginProgress = view.findViewById(R.id.login_progressBar);
        mAlertDialog = new SpotsDialog(getActivity(), "Logging in...");
        tvNoAcc= view.findViewById(R.id.tv_no_acc_msg);
        linearLayout=view.findViewById(R.id.loginLinearLayout);
        checkLogin();


        mainLayout = view.findViewById(R.id.loginLinearLayout);


        mLogin.setOnClickListener(v -> {
            loginProgress.setVisibility(View.VISIBLE);
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("DeviceToken", Context.MODE_PRIVATE);
            String deviceId = sharedPreferences.getString("token", "");
            Log.d("DeviceToken", deviceId);
            if (validate()) {
                LoginDto loginDto = new LoginDto(mPassword.getText().toString(), mEmail.getText().toString(), deviceId, "MOBILE");
                sendNetworkRequest(loginDto);
                mAlertDialog.dismiss();
            }
            loginProgress.setVisibility(View.GONE);
        });

        tvNoAcc.setOnClickListener(view1 -> {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(getActivity(),new String[]{
                        Manifest.permission.CALL_PHONE
                },0);

            }else {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + "+977014479017"));
                getContext().startActivity(intent);
            }

        });
    }

    private void checkLogin() {
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("loginDetails", Context.MODE_PRIVATE);
        if (!sharedPref.getString("token", "").equals("")) {
            NavmenuHideShow.showHideNavMenuAccordingToLoginRole(navigationView,sharedPref.getString("loginType",""),getContext());
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (sharedPref.getString("loginType", "").equalsIgnoreCase("teacher")) {
                ft.replace(R.id.content_frame, new TeacherDashboard());

            } else if (sharedPref.getString("loginType","").equalsIgnoreCase("student")){
                    ft.replace(R.id.content_frame, new MainFragment());
            }else{
                ft.replace(R.id.content_frame, new DashboardFragment());
            }

            GetLoginInstanceFromDatabase getLoginInstanceFromDatabase=new GetLoginInstanceFromDatabase(getContext());
            List<TeamEntity> teamEntity=new ArrayList<>();
            teamEntity.addAll(getLoginInstanceFromDatabase.getTeamInstance());
            if (teamEntity.isEmpty()){
                ft.replace(R.id.content_frame, new MainFragment());
            }

            ft.commit();
        }
    }

    private void sendNetworkRequest(LoginDto loginDto1) {
        mAlertDialog.show();
        Log.d("DeviceId----", loginDto1.getDeviceId());
        //Calling Api using Retrofit for Login
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<LoginDto> call = apiService.sendUsernameAndPassword(loginDto1, "texasintl.edu.np");
        call.enqueue(new Callback<LoginDto>() {
            @Override
            public void onResponse(Call<LoginDto> call, Response<LoginDto> response) {
                //Disable Progress Bar
                mAlertDialog.dismiss();


                //Executed when Response is Success
                if (response.isSuccessful()) {


                    LoginDto loginDto = response.body();

                    //Saving Token and User ID when successfully logged in
                    SharedPreferences sharedPref = getActivity().getSharedPreferences("loginDetails", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("token", loginDto.getUser().getToken());
                    editor.putLong("id", loginDto.getUser().getId());
                    editor.putLong("loginId", loginDto.getUser().getLoginId());

                    if (!sharedPref.getString("token", "").equals("")){
                        NavmenuHideShow.showHideNavMenuAccordingToLoginRole(navigationView, sharedPref.getString("loginType", ""),getContext());

                    }
                    if (!("SUPERADMIN".equals(loginDto.getUser().getUserRole()))) {
                        editor.putLong("customerId", loginDto.getUser().getCustomerId());
                    }

                    editor.putString("firstname", loginDto.getUser().getFirstName());
                    editor.putString("loginType", loginDto.getUser().getLoginType());
                    editor.putString("lastname", loginDto.getUser().getLastName());
                    editor.putString("email", loginDto.getUser().getEmail());
                    editor.putString("userrole", loginDto.getUser().getUserRole());
                    editor.putString("username", loginDto.getUser().getUsername());
                    editor.apply();

                    //save login Response to database for future use
                    saveLoginResponseToDatabase(loginDto.getUser());

                    if (loginDto.getUser().getProfilePicture() != null) {
                        SharedPreferences sharedPref1 = getActivity().getSharedPreferences("loginDetails", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = sharedPref1.edit();
                        editor1.putString("profilePicture", loginDto.getUser().getProfilePicture());
                        editor1.apply();
                    } else {
                        SharedPreferences sharedPref1 = getActivity().getSharedPreferences("loginDetails", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = sharedPref1.edit();
                        editor1.putString("profilePicture", "nopic");
                        editor1.apply();
                    }

                    //Show and hide the nav menu according to login type
                    NavmenuHideShow.showHideNavMenuAccordingToLoginRole(navigationView, loginDto.getUser().getLoginType(),getContext());


                    //Hide Keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);

                    //Starts Main Activity When Login Sucessful
                    loginProgress.setVisibility(View.GONE);
                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                    if (response.body().getUser().getLoginType().equalsIgnoreCase("teacher")) {
                        ft.replace(R.id.content_frame, new TeacherDashboard());
                    }
                    else if(response.body().getUser().getLoginType().equalsIgnoreCase("student")) {
                        ft.replace(R.id.content_frame, new MainFragment());
                    }else {
                        ft.replace(R.id.content_frame, new DashboardFragment());
                    }

                    ft.commit();
                    CustomSnackbar.showSuccessSnakeBar(linearLayout,"Login Success",getContext());


                } else {
                    //Maps the error message in ErrorMessageDto
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = null;
                    try {
                        mJson = parser.parse(response.errorBody().string());
                        Gson gson = new Gson();
                        ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                        mAlertDialog.dismiss();
                        loginProgress.setVisibility(View.GONE);
                        CustomSnackbar.showFailureSnakeBar(linearLayout,errorMessageDto.getMessage(),getContext());
//                        Toast.makeText(getContext(), errorMessageDto.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<LoginDto> call, Throwable t) {
                mAlertDialog.dismiss();
                loginProgress.setVisibility(View.GONE);
                CustomSnackbar.checkErrorResponse(mainLayout,getContext());
                t.printStackTrace();
            }
        });

    }


    private boolean validate() {
        boolean validated = false;
        if (TextUtils.isEmpty(mEmail.getText().toString())) {
            mEmail.setError("Required");
            validated = false;
        } else if (TextUtils.isEmpty(mPassword.getText().toString())) {
            mPassword.setError("Required");
            validated = false;
        } else {
            validated = true;
        }
        loginProgress.setVisibility(View.GONE);
        return validated;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();
        inflater.inflate(R.menu.main, menu);
        MenuItem showNotification = menu.findItem(R.id.act_notification);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("loginDetails", Context.MODE_PRIVATE);
        if (!("".equals(sharedPref.getString("token", "")))) {
            showNotification.setVisible(true);
        }else
            showNotification.setVisible(false);

    }


    ///Save login response to room database and its done in background thread because it may cause application crash while run in UI thread
    public void saveLoginResponseToDatabase(LoginDto.User user) {
        TicmsRoomDatabase ticmsRoomDatabase = TicmsRoomDatabase.getDatabaseInstance(getContext());
        UserLoginResponseDao userLoginResponseDao = ticmsRoomDatabase.userLoginResponseDao();

        UserLoginResponseEntity userLoginResponseEntity = new UserLoginResponseEntity(user.getId(), user.getLoginId(), user.getCustomerId(),
                user.getFirstName(), user.getLastName(), user.getEmail(), user.getToken(), user.getUserRole(), user.getLoginType(), user.getDeviceId(),
                user.getUsername(), user.getProfilePicture(), user.getStatus(), user.getMobileNumber(), user.getGender(), user.getCourseId(), user.getCourseName());
        InsertLoginResponse insertLoginResponse = new InsertLoginResponse(userLoginResponseDao);
        insertLoginResponse.execute(userLoginResponseEntity);

        //for team

        TeamDao teamDao=ticmsRoomDatabase.getTeamDao();

        List<TeamEntity> teamEntities=new ArrayList<>();
        for(TeamDto.Content teamDto :user.getTeamList()){

            TeamEntity entity=new TeamEntity();
            entity.setId(teamDto.getId());
            entity.setName(teamDto.getName());
            entity.setType(teamDto.getType());
            entity.setUserId(user.getId());

            teamEntities.add(entity);
        }


        InsertTeamResponse insertTeamResponse=new InsertTeamResponse(teamDao);
        insertTeamResponse.execute(teamEntities);



    }
    class InsertTeamResponse extends AsyncTask<List<TeamEntity>,Void,Void>{
        private TeamDao teamDao;


        InsertTeamResponse(TeamDao teamDao) {
            this.teamDao=teamDao;


        }

        @Override
        protected Void doInBackground(List<TeamEntity>... teamEntities) {
            teamDao.insertTeamToDatabase(teamEntities[0]);
            return null;
        }
    }


    class InsertLoginResponse extends AsyncTask<UserLoginResponseEntity, Void, Void> {
        private UserLoginResponseDao mUserLoginResponseDao;

        InsertLoginResponse(UserLoginResponseDao mUserLoginResponseDao) {
            this.mUserLoginResponseDao = mUserLoginResponseDao;
        }

        @Override
        protected Void doInBackground(UserLoginResponseEntity... userLoginResponseEntities) {
            mUserLoginResponseDao.insertUserDetail(userLoginResponseEntities[0]);
            return null;
        }
    }

}



