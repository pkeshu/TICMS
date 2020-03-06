package com.imaginology.texas.Notification.sendNotification;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.Notification.PushNotification.AllNotification;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.SnackBar.CustomSnackbar;
import com.imaginology.texas.Students.StudentListDto;
import com.imaginology.texas.Team.TeamDto;
import com.imaginology.texas.R;
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

public class SendNotificationActivity extends AppCompatActivity {
    private EditText message, titleOfNotification;
    private String selectedFaculty, title;
    private String messageTobeSent;
    private ProgressBar progressBar;
    private final String TAG = SendNotificationActivity.class.getSimpleName();
    private ArrayList<String> teams = new ArrayList<>();
    private ArrayList<Long> teamid = new ArrayList<>();
    private ArrayList<Long> teamIDs = new ArrayList<>();
    private ApiInterface apiInterface;
    private UserLoginResponseEntity loginInstance;
    private TextView inputCharCounter, total, notifTitle;
    LinearLayout lllayout, teamContaining, textCountingLayout;
    CheckBox selectAll;
    List<CheckBox> teamCheckBoxes = new ArrayList<>();

    private int messageCounter;

    FloatingActionButton sendButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);

        textCountingLayout = findViewById(R.id.linearLayout);
        textCountingLayout.setVisibility(View.VISIBLE);
        teams.add("Select Team");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SupportActionBarInitializer.setUpSupportActionBar(getSupportActionBar(), "Send Notification", true);
        selectAll = findViewById(R.id.select_all_team_checkbox);
        GetLoginInstanceFromDatabase loginInstanceAccessor = new GetLoginInstanceFromDatabase(this);
        loginInstance = loginInstanceAccessor.getLoginInstance();
        apiInterface = ApiClient.getRetrofit(SendNotificationActivity.this).create(ApiInterface.class);
        progressBar = findViewById(R.id.progress_bar);
        sendButton = findViewById(R.id.sendButtonID);

        sendButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_light_grey)));


        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        messageTobeSent = intent.getStringExtra("message");
        messageCounter = intent.getIntExtra("messageCounter", 0);
        try {

            if (messageTobeSent.length() == 0) {
                sendButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_light_grey)));

            } else {
                sendButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));

            }
        } catch (Exception e) {
            System.out.println("Exception:::" + e.getMessage());
        }

        sendButton.setOnClickListener(view -> validate());


//        progressBar.setVisibility(View.VISIBLE);
        defineView();
    }


    private void defineView() {
        notifTitle = findViewById(R.id.title_text);
        notifTitle.setVisibility(View.VISIBLE);
        titleOfNotification = findViewById(R.id.title_of_notification);
        titleOfNotification.setVisibility(View.VISIBLE);
        message = findViewById(R.id.your_message);
        teamContaining = findViewById(R.id.teamContainer);
//        teamContaining.setVisibility(View.VISIBLE);
        inputCharCounter = findViewById(R.id.counterForInput);
        total = findViewById(R.id.total_input);
        titleOfNotification.setText(title);
        message.setText(messageTobeSent);
        String newMessage = message.getText().toString();
        int calc = newMessage.length();
        System.out.println("Calcc :::" + calc);
        inputCharCounter.setText("" + calc);



        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = message.getText().toString();
                messageCounter = s.length();
                inputCharCounter.setText("" + (int) messageCounter);
                if (messageCounter == 0) {
                    sendButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_light_grey)));

                } else {

                    sendButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));


                }

                if (messageCounter >= 800) {
                    inputCharCounter.setTextColor(getResources().getColor(R.color.blue_alert));
                    total.setTextColor(getResources().getColor(R.color.blue_alert));
                }
                if (messageCounter == 1000) {
                    inputCharCounter.setText("Max limit reached...    " + (int) messageCounter);
                    total.setTextColor(getResources().getColor(R.color.red_alert));
                    inputCharCounter.setTextColor(getResources().getColor(R.color.red_alert));
                }
                if (messageCounter < 800) {
                    inputCharCounter.setTextColor(getResources().getColor(R.color.colorBlack));
                    total.setTextColor(getResources().getColor(R.color.colorBlack));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }


    private void validate() {
        messageTobeSent = message.getText().toString();
        selectedFaculty = null;
        title = titleOfNotification.getText().toString();
        if (TextUtils.isEmpty(messageTobeSent)) {
            message.setError("Please enter your message...");
        } else if (TextUtils.isEmpty(title.trim())) {
            titleOfNotification.setError("Please enter a title...");
            titleOfNotification.setText("");
        } else {
            messageTobeSent = messageTobeSent.trim();
            title = title.trim();

            Intent intent = new Intent(SendNotificationActivity.this, TeamListActivity.class);
            intent.putExtra("message", messageTobeSent);
            intent.putExtra("title", title);
            intent.putExtra("messageCounter", this.messageCounter);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }*/

}
