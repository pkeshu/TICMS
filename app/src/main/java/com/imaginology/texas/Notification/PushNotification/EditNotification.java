package com.imaginology.texas.Notification.PushNotification;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.imaginology.texas.MainActivity;
import com.imaginology.texas.Notification.sendNotification.SendNotificationActivity;
import com.imaginology.texas.R;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.SupportActionBarInitializer;

public class EditNotification extends AppCompatActivity {

    EditText editMessage;
    View showNotiSendView;
    private ImageView sendNotification;
    private MenuItem showNotiSend;
    private TextView inputCharCounter, total;
    EditDeleteOption editDeleteOption;
    ApiInterface apiInterface;
    NotificationEditRequestDTO notificationEditRequestDTO;
    private ProgressBar progressBar;
    Long loginId, customerId, messageId;
    String token, title, message, newMessage;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);
//        sendNotification.setVisibility(View.VISIBLE);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SupportActionBarInitializer.setUpSupportActionBar(getSupportActionBar(), "Edit Message", true);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        progressBar = findViewById(R.id.progress_bar);
        apiInterface = ApiClient.getRetrofit(this).create(ApiInterface.class);
        progressBar.setVisibility(View.INVISIBLE);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        message = intent.getStringExtra("message");

        loginId = intent.getExtras().getLong("loginId");
        customerId = intent.getExtras().getLong("customerId");
        token = intent.getStringExtra("token");
        messageId = intent.getExtras().getLong("messageId");

        System.out.println("Values are: " + title + message + loginId + loginId + token + message + customerId);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        showNotiSend = menu.findItem(R.id.notiSend);
        showNotiSendView = showNotiSend.getActionView();
        sendNotification = showNotiSendView.findViewById(R.id.sendNotification);

        showNotiSend.setVisible(true);
        showNotiSendView.setVisibility(View.VISIBLE);
//        sendNotification.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        loginId = intent.getExtras().getLong("loginId");
        customerId = intent.getExtras().getLong("customerId");
        token = intent.getStringExtra("token");
        messageId = intent.getExtras().getLong("messageId");
        checkIt(customerId, loginId, token);
        return true;
    }

    private void checkIt(Long customerId, Long loginId, String token) {

        editMessage = findViewById(R.id.your_message);
        editMessage.setText(message);

//        int textLength = newMessage.length();

        sendNotification.setVisibility(View.VISIBLE);
        this.customerId = customerId;
        this.loginId = loginId;
        this.token = token;

        System.out.println("Customer Id: " + customerId);

        System.out.println("Datas: " + messageId + " " + message + " " + title);


        sendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newMessage = editMessage.getText().toString();
                if (newMessage.length()>0){
                    notificationEditRequestDTO = new NotificationEditRequestDTO(messageId, newMessage, title, "string");
                    editDeleteOption = new EditDeleteOption(apiInterface, loginId, customerId, notificationEditRequestDTO, token, EditNotification.this);
                    editDeleteOption.editNotification();
                    finish();
                } else{
                    editMessage.setError("Notification cannot be empty.");
                }


            }
        });
    }

}
