package com.imaginology.texas.Notification.PrivateMessage;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.Notification.PushNotification.NotificationReply.NotificationReplyAdapter;
import com.imaginology.texas.Notification.PushNotification.NotificationReply.NotificationReplyDto;
import com.imaginology.texas.Notification.sendNotification.SendNotificationDTO;
import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.DataValidityChecker;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;
import com.imaginology.texas.util.SupportActionBarInitializer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrivateMessageActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ApiInterface apiInterface;
    private EditText edtMessage;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private FloatingActionButton sendPMsgBtn;
    private TextView notificationTitle;
    private UserLoginResponseEntity loginInstance;
    private ConstraintLayout mainLayout;
    private Long recieverId, messageId;
    private NotificationReplyAdapter notificationReplyAdapter;
    private List<NotificationReplyDto.Notifications> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_reply);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SupportActionBarInitializer.setUpSupportActionBar(getSupportActionBar(), "Private Message", true);
        apiInterface = ApiClient.getRetrofit(this).create(ApiInterface.class);
        loginInstance = new GetLoginInstanceFromDatabase(this).getLoginInstance();
        edtMessage = findViewById(R.id.your_message);
        sendPMsgBtn = findViewById(R.id.floatingActionButton);
        mainLayout = findViewById(R.id.main_layout);
        notificationTitle = findViewById(R.id.notification_title);
        layoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.reply_notification_list);
        notificationReplyAdapter = new NotificationReplyAdapter(this);
        notificationTitle.setVisibility(View.GONE);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(notificationReplyAdapter);
        getIntentValue();

        sendPMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = edtMessage.getText().toString().trim();
                if (DataValidityChecker.isEditTextDataValid(edtMessage, message, true))
                callServerForPrivateMessage(message, recieverId);

            }
        });
    }

    private void callServerForPrivateMessage(String message, Long recieverId) {
        SendNotificationDTO sendNotificationDTO = new SendNotificationDTO(
                message,
                recieverId, "Private Message");
        Call<ResponseBody> call = apiInterface.sendNotification(loginInstance.getCustomerId(), loginInstance.getLoginId(),
                sendNotificationDTO,
                loginInstance.getToken());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() || response.code() == 201) {

                  String date=  DateFormat.format("dd-MM-yyyy hh:mm:ss", new Date()).toString();
                    NotificationReplyDto.Sender sender = new NotificationReplyDto.Sender(loginInstance.getProfilePicture(), loginInstance.getUserId().intValue(),
                            loginInstance.getFirstName() + " " + loginInstance.getLastName(), loginInstance.getUserRole());
                    NotificationReplyDto.Notifications notification = new NotificationReplyDto.Notifications(message,(long)0, sender);
                    if (notificationList == null)
                        notificationList = new ArrayList<>();
                        notificationList.clear();
                    notificationList.add(notification);
                    edtMessage.setText("");
                    notificationReplyAdapter.addNotificationReplyToList(notificationList);



//                    if (notificationList.isEmpty()) {
////                        lableNoNotification.setVisibility(View.VISIBLE);.
//                        recyclerView.setVisibility(View.GONE);
//                    }
                    //             Toast.makeText(PrivateMessageActivity.this, "Sent :)", Toast.LENGTH_SHORT).show();
//                    Intent intent=new Intent(PrivateMessageActivity.this,NotificationReply.class);
//                    intent.putExtra("notificationId",recieverId);
//                    intent.putExtra("notificationMessage",message);
//                    intent.putExtra("sender","");
//                    intent.putExtra("sentDate","");
//                    intent.putExtra("notificationTitle","Private Message");
//                    startActivity(intent);
                    //  NotificationReplyDto.Notifications notificationResponse = response.body();
//                    NotificationReplyDto.Sender sender = notificationResponse.getSender();
//                    NotificationReplyDto.Notifications notification = new NotificationReplyDto.Notifications();
//                    NotificationReplyDto.Sender sender = notification.getSender();
//                    sender.setProfilePicture(loginInstance.getProfilePicture());
//                    sender.setSenderId(loginInstance.getLoginId().intValue());
//                    sender.setSenderName(loginInstance.getFirstName() + " " + loginInstance.getLastName());
//                    sender.setUserRole(loginInstance.getUserRole());
//                    if (notificationList == null)
//                        notificationList = new ArrayList<>();
//                    notificationList.clear();
//                    notification.setId(Integer.valueOf(loginInstance.getId().toString()));
//                    notification.setMessage(message);
//                    notification.setSender(sender);
//                    notification.setRepliedDate((long) 0);
//                    notificationList.add(0, notification);
//                    edtMessage.setText("");
//                    notificationReplyAdapter.addNotificationReplyToList(notificationList);
//                    onBackPressed();
//                    finish();

                } else {
                    Log.d("NotSucess=====", response.errorBody().toString());
//                    progressBar.setVisibility(View.GONE);
                    //Maps the error message in ErrorMessageDto
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = null;
                    try {
                        mJson = parser.parse(response.errorBody().string());
                        Gson gson = new Gson();
                        ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                        Toast.makeText(getApplicationContext(), errorMessageDto.getMessage(), Toast.LENGTH_SHORT).show();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.no_response, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getIntentValue() {
        Intent dataIntent = getIntent();
        recieverId = dataIntent.getExtras().getLong("receiverId");
        Log.d("getvalueIntent====", recieverId.toString());

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
