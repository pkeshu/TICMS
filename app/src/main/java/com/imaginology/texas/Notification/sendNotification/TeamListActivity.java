package com.imaginology.texas.Notification.sendNotification;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.Notification.PushNotification.AllNotification;
import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.SnackBar.CustomSnackbar;
import com.imaginology.texas.Team.TeamDto;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;
import com.imaginology.texas.util.StatusChecker;
import com.imaginology.texas.util.SupportActionBarInitializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamListActivity extends AppCompatActivity {


    private ArrayList<String> teams = new ArrayList<>();
    private ArrayList<Long> teamid = new ArrayList<>();
    CheckBox checkBox;
    CheckBox selectAll;
    private MenuItem showNotiSend;
    View showNotiSendView;
    private ImageView sendNotification;

    private UserLoginResponseEntity loginInstance;
    private SendNotificationDTO sendNotificationDTO;
    private ApiInterface apiInterface;
    private Long loginId;
    private Long customerId;
    private String token, messageToBeSent, title;
    LinearLayout lllayout;
    List<CheckBox> teamCheckBoxes = new ArrayList<>();
    private ArrayList<Long> teamIDs = new ArrayList<>();

    FloatingActionButton sendButton;

    int messageCounter;

    /*public TeamListActivity(ApiInterface apiInterface,
                            Long loginId, Long customerId, String token,
                            String messageToBeSent, String title) {
        this.apiInterface = apiInterface;
        this.loginId = loginId;
        this.customerId = customerId;
        this.token = token;
        this.messageToBeSent = messageToBeSent;
        this.title = title;
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SupportActionBarInitializer.setUpSupportActionBar(getSupportActionBar(), "Select Team", true);

        sendButton = findViewById(R.id.sendButtonID);
        GetLoginInstanceFromDatabase loginInstanceAccessor = new GetLoginInstanceFromDatabase(this);
        loginInstance = loginInstanceAccessor.getLoginInstance();
        apiInterface = ApiClient.getRetrofit(TeamListActivity.this).create(ApiInterface.class);
        selectAll = findViewById(R.id.select_all_team_checkbox);

        Intent intent = getIntent();
        messageToBeSent = intent.getStringExtra("message");
        title = intent.getStringExtra("title");
        messageCounter = intent.getIntExtra("messageCounter", 0);

        customerId = loginInstance.getCustomerId();
        loginId = loginInstance.getLoginId();
        token = loginInstance.getToken();

        getTeamFromServer(customerId, token, loginId);
        sendButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_light_grey)));




        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }

            public void validate() {

                if (teamIDs.size()==0) {
                    Toast.makeText(TeamListActivity.this, "Please select a team...", Toast.LENGTH_SHORT).show();
                } else {

                    sendNotificationDTO = new SendNotificationDTO(messageToBeSent, teamIDs, title);
                    sendPushNotification();
                }

            }

            private void sendPushNotification() {
                Call<ResponseBody> call = apiInterface.sendNotification(loginInstance.getLoginId(),
                        loginInstance.getCustomerId(), sendNotificationDTO, loginInstance.getToken());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        int status = response.code();
                        if (response.isSuccessful()) {
                            Toast.makeText(TeamListActivity.this, "Sent :)", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(TeamListActivity.this, AllNotification.class));
                            finish();
                        } else if (status == 401) {
                            StatusChecker.statusCheck(TeamListActivity.this);
                        } else {
                            JsonParser parser = new JsonParser();
                            JsonElement mJson = null;
                            try {

                                mJson = parser.parse(response.errorBody().string());
                                Gson gson = new Gson();
                                ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                                CustomSnackbar.showFailureSnakeBar(lllayout,errorMessageDto.getMessage(),TeamListActivity.this);
//                        Toast.makeText(AllNotification.this, errorMessageDto.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }                }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("fail", t.getMessage());
                        Toast.makeText(TeamListActivity.this, "Failed to send notification", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        showNotiSend = menu.findItem(R.id.notiSend);
        showNotiSendView = showNotiSend.getActionView();
        System.out.println("Here is also done...");
        sendNotification = showNotiSendView.findViewById(R.id.sendNotification);
        sendNotification.setVisibility(View.VISIBLE);
        sendNotification.setOnClickListener(view -> validate());
        return true;
    }*/



    public void getTeamFromServer(Long customerId, String token, Long loginId) {

        Call<TeamDto> call = apiInterface.getTeams(customerId,
                token, loginId, 0, 20, "id,asc", "");
        call.enqueue(new Callback<TeamDto>() {
            @Override
            public void onResponse(Call<TeamDto> call, Response<TeamDto> response) {
                int status = response.code();
                if (response.isSuccessful()) {
                    Log.d("IamInside", "Response is Sucessful");
                    TeamDto responseBody = response.body();
//                    progressBar.setVisibility(View.GONE);
                    selectAll = findViewById(R.id.select_all_team_checkbox);
                    selectAll.setVisibility(View.VISIBLE);
                    for (TeamDto.Content x : responseBody.getContents()) {
                        teams.add(x.getName());
                        teamid.add(Long.valueOf(x.getId()));
                        System.out.println("heyyy. THis is surya...");
                        lllayout = findViewById(R.id.lLayout);
                        TableRow row = new TableRow(TeamListActivity.this);
                        row.setId(x.getId());
                        row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT));
                        checkBox = new CheckBox(TeamListActivity.this);
                        checkBox.setId(x.getId());
                        checkBox.setText(x.getName());
                        checkBox.setTextSize(20);
                        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
                        checkBox.setTypeface(face);
                        checkBox.setChecked(false);
                        row.addView(checkBox);
                        lllayout.addView(row);
                        checkBox.setOnCheckedChangeListener(new TeamCheckedChangeListener(TeamListActivity.this, teamIDs, selectAll, teamCheckBoxes, sendButton));
                        teamCheckBoxes.add(checkBox);
                    }
                    selectAll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (selectAll.isChecked()) {
                                for (CheckBox cb : teamCheckBoxes) {
                                    cb.setChecked(true);
                                }
//                                teamIDs.addAll(teamid);
                            } else {
                                for (CheckBox cb : teamCheckBoxes)
                                    cb.setChecked(false);
                            }
                        }
                    });
                } else if (status == 401) {
                    StatusChecker.statusCheck(TeamListActivity.this);
                } else {
//                    progressBar.setVisibility(View.GONE);
                    //Maps the error message in ErrorMessageDto
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = null;
                    /*try {
                        mJson = parser.parse(response.errorBody().string());
                        Gson gson = new Gson();
                        ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                        Toast.makeText(getApplicationContext(), errorMessageDto.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }*/
                }
            }

            @Override
            public void onFailure(Call<TeamDto> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), R.string.no_response, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("Title is:::"+title);
        System.out.println("Message is:::"+messageToBeSent);
        if (item.getItemId() == android.R.id.home){
            Intent intent = new Intent(TeamListActivity.this, SendNotificationActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("message", messageToBeSent);
            intent.putExtra("messageCounter", messageCounter);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TeamListActivity.this, SendNotificationActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("message", messageToBeSent);
        intent.putExtra("messageCounter", messageCounter);
        startActivity(intent);
        finish();
    }
}
