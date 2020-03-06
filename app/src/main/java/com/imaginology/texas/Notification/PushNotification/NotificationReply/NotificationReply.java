package com.imaginology.texas.Notification.PushNotification.NotificationReply;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.Notification.PushNotification.AllNotification;
import com.imaginology.texas.Notification.PushNotification.EditDeleteOption;
import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.SnackBar.CustomSnackbar;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;
import com.imaginology.texas.util.SupportActionBarInitializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationReply extends AppCompatActivity {
    private Toolbar toolbar;
    private ApiInterface apiInterface;
    private EditText replyMessage;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private FloatingActionButton sendNotiBtn;
    private NotificationReplyDto notificationReplyDto;
    private Long notificationId, loginId, customerId;
    private String token;
    private EditDeleteOption editDeleteOption;
    private UserLoginResponseEntity userLoginResponseEntity;
    private TextView notificationTitle;
    private List<NotificationReplyDto.Notifications> notificationList;
    private NotificationReplyAdapter notificationReplyAdapter;
    private int page = 0;
    private UserLoginResponseEntity loginInstance;

    TextView notificationMessage;
    String message;
    private ConstraintLayout mainLayout;

    private String notiTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_reply);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SupportActionBarInitializer.setUpSupportActionBar(getSupportActionBar(), "Response", true);
        apiInterface = ApiClient.getRetrofit(this).create(ApiInterface.class);
//        lableNoNotification = findViewById(R.id.no_reply_text);
        loginInstance = new GetLoginInstanceFromDatabase(this).getLoginInstance();
        replyMessage = findViewById(R.id.your_message);
        sendNotiBtn = findViewById(R.id.floatingActionButton);
        mainLayout = findViewById(R.id.main_layout);
        notificationTitle = findViewById(R.id.notification_title);
        layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setSmoothScrollbarEnabled(true);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        recyclerView = findViewById(R.id.reply_notification_list);
//        notificationMessage = findViewById(R.id.messageBoxId);

        //get data from intent
        Intent intent = getIntent();
        notificationId = intent.getExtras().getLong("notificationId");
//        loginId = intent.getExtras().getLong("loginId");
        loginId = loginInstance.getLoginId();
        message = intent.getStringExtra("notificationMessage");
//        customerId = intent.getExtras().getLong("customerId");
        customerId = loginInstance.getCustomerId();

//        token = intent.getStringExtra("token");
        token = loginInstance.getToken();
        String senderJsonObject = intent.getStringExtra("sender");
        String sentDate = intent.getStringExtra("sentDate");

        notiTitle = intent.getExtras().getString("notificationTitle");

        notificationTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        notificationTitle.setSelected(true);
        notificationTitle.setSingleLine(true);
        notificationTitle.setText(notiTitle);
//        notificationTitle.startAnimation(AnimationUtils.loadAnimation(this,R.anim.text_translation));
        NotificationReplyDto.Sender sender = new Gson().fromJson(senderJsonObject, NotificationReplyDto.Sender.class);
        NotificationReplyDto.Notifications notification = new NotificationReplyDto.Notifications(message, (long)0,sender);
        if (!notificationTitle.equals("") && !message.equals("")) {
            if (notificationList == null)
                notificationList = new ArrayList<>();
            notification.setId(notificationId.intValue());
            notification.setMessage(message);
            notification.setSender(sender);
            if (sentDate != null)
                notification.setRepliedDate(Long.valueOf(sentDate));

//            notificationList.add(0, notification);
        }

        /*notificationMessage.setText(message);
        notificationMessage.setMovementMethod(new ScrollingMovementMethod());*/

        //database insatance
        GetLoginInstanceFromDatabase getLoginInstanceFromDatabase = new GetLoginInstanceFromDatabase(this);
        userLoginResponseEntity = getLoginInstanceFromDatabase.getLoginInstance();
        recyclerView.setHasFixedSize(true);
        notificationReplyAdapter = new NotificationReplyAdapter(this);
        recyclerView.setAdapter(notificationReplyAdapter);
        recyclerView.setLayoutManager(layoutManager);
        callServerForListofReplyNotification(loginId, customerId, notificationId, token, (long) page);

        sendNotiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callServerForReplyNotification(notiTitle, customerId, loginId, token, notificationId, senderJsonObject, sentDate);
            }
        });
    }

    private void callServerForListofReplyNotification(Long loginId, Long customerId, Long notificationId, String token, Long page) {
        Call<NotificationReplyDto> call = apiInterface.listReplyNotification(loginId,
                customerId, notificationId, token
                , page, (long) 20, "id,asc");
        call.enqueue(new Callback<NotificationReplyDto>() {
            @Override
            public void onResponse(Call<NotificationReplyDto> call, Response<NotificationReplyDto> response) {
//                Log.d("responseGo:", String.valueOf(response.code()));
                if (response.isSuccessful()) {
                    notificationList.addAll(response.body().getNotifications());
                    System.out.println("Notification list:::::" + notificationList);
                    notificationReplyAdapter.addNotificationReplyToList(notificationList);
                    if (notificationList.isEmpty()) {
//                        lableNoNotification.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                } else {
                    //Maps the error message in ErrorMessageDto
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = null;
                    try {

                        mJson = parser.parse(response.errorBody().string());
                        Gson gson = new Gson();
                        ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                        Toast.makeText(NotificationReply.this, errorMessageDto.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<NotificationReplyDto> call, Throwable t) {
                Toast.makeText(NotificationReply.this, "Response Failed", Toast.LENGTH_SHORT).show();
//                Log.d("responseGo",t.getMessage());

            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(NotificationReply.this, AllNotification.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void callServerForReplyNotification(String notiTitle, Long customerId, Long loginId, String token, Long notificationId, String senderJsonObjects, String sentDates) {
        this.notiTitle = notiTitle;
        this.customerId = customerId;
        this.loginId = loginId;
        this.token = token;
        this.notificationId = notificationId;
        String newMessage = replyMessage.getText().toString();
        if (newMessage.length() > 0 && newMessage.trim().length() != 0) {
            notificationReplyDto = new NotificationReplyDto(newMessage);
            Call<NotificationReplyDto.Notifications> call = apiInterface.replyNotification(loginId, customerId, notificationId, notificationReplyDto, token);
            call.enqueue(new Callback<NotificationReplyDto.Notifications>() {
                @Override
                public void onResponse(Call<NotificationReplyDto.Notifications> call, Response<NotificationReplyDto.Notifications> response) {
                    if (response.isSuccessful()) {
                        NotificationReplyDto.Notifications notificationResponse = response.body();
                        NotificationReplyDto.Sender sender = notificationResponse.getSender();
                        NotificationReplyDto.Notifications notification = new NotificationReplyDto.Notifications(message,(long)0, sender);
                        if (notificationList == null)
                            notificationList = new ArrayList<>();
                        notificationList.clear();
                        notification.setId(notificationResponse.getId());
                        notification.setMessage(notificationResponse.getMessage());
                        notification.setSender(sender);
                        if (notificationResponse.getRepliedDate() != null)
                            notification.setRepliedDate(notificationResponse.getRepliedDate());
                        notificationList.add(0, notification);
                        replyMessage.setText("");
                        notificationReplyAdapter.addNotificationReplyToList(notificationList);
                        final int SCROLLING_UP = -1;
                        boolean scrollToNewTop = recyclerView.canScrollVertically(SCROLLING_UP);
                        notificationReplyAdapter.notifyItemInserted(notificationReplyAdapter.getItemCount() - 1);
                        if (scrollToNewTop) {
                            recyclerView.scrollToPosition(notificationReplyAdapter.getItemCount() - 1);
                        }

                    } else {
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
                public void onFailure(Call<NotificationReplyDto.Notifications> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Reply is failed.", Toast.LENGTH_SHORT).show();


                }
            });
        } else {
            CustomSnackbar.showFailureSnakeBar(mainLayout, "Message can't be empty", NotificationReply.this);
        }
    }

}
