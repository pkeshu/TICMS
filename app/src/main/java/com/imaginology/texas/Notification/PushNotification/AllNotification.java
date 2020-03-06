package com.imaginology.texas.Notification.PushNotification;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.Notification.PushNotification.NotificationReply.NotificationReply;
import com.imaginology.texas.Notification.sendNotification.SendNotificationActivity;
import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.SnackBar.CustomSnackbar;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.AutoLogin;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;
import com.imaginology.texas.util.ShowNoContentPopUp;
import com.imaginology.texas.util.StatusChecker;
import com.imaginology.texas.util.SupportActionBarInitializer;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllNotification extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener, NotificationAdapter.ClickListener {

    private NotificationDto notificationDto;
    private NotificationAdapter notificationAdapter;
    private List<NotificationDto.Notification> notificationDtos;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ApiInterface apiService;
    private RecyclerView recyclerView;
    private ConstraintLayout rootLayout;
    private RecyclerView.LayoutManager layoutManager;
    private GetLoginInstanceFromDatabase loginInstanceAccessor;
    private UserLoginResponseEntity loginInstance;
    private MenuItem showNotification;
    private FrameLayout frameLayout;
    //For Endless Scrolling in recyclerview
    private ImageView notificationicon;
    private Integer badge;
    private int page = 0;
    private boolean isScrolling = false;
    private int totalItems;
    private int totalVisiableItems;
    private int itemsOutsideScreen;
    private TextView read, backTxt, notify, notificationbadge;
    private FloatingActionButton sendNotiFloatingBtn;
    private LinearLayout linearLayout;
    private ApiInterface apiInterface;
    private int reducebadge;
    private String reducebadgeString;
    private EditDeleteOption editDeleteOption;
    private Integer notiId;
    Long notificationId;
    Long senderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_notification);

        loginInstanceAccessor = new GetLoginInstanceFromDatabase(this);
        loginInstance = loginInstanceAccessor.getLoginInstance();
        read = findViewById(R.id.notificationread);
        apiInterface = ApiClient.getRetrofit(this).create(ApiInterface.class);
        linearLayout = findViewById(R.id.linearLayout1);
        frameLayout = findViewById(R.id.frameLayout);
        notify = findViewById(R.id.allnotification);
        backTxt = findViewById(R.id.back_txt);
        recyclerView = findViewById(R.id.recyclerview_notification);
        notificationbadge = findViewById(R.id.notification_badge);
        notificationicon = findViewById(R.id.notification_image);
        sendNotiFloatingBtn = findViewById(R.id.send_new_notification_floating_btn);
        sendNotiFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AllNotification.this, SendNotificationActivity.class));
                view.setOnClickListener(null);
                finish();

            }
        });

        backTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(AllNotification.this, MainActivity.class);
//                startActivity(intent);
                onBackPressed();
                return;
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SupportActionBarInitializer.setUpSupportActionBar(getSupportActionBar(), "All Notifications", true);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        layoutManager = new LinearLayoutManager(this);
        rootLayout = findViewById(R.id.cl_root_layout);
        apiService = ApiClient.getRetrofit(this).create(ApiInterface.class);
        notificationAdapter = new NotificationAdapter(this);
        notificationAdapter.setNotificationClickListener(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(notificationAdapter);
        recyclerView.setLayoutManager(layoutManager);
//        notificationAdapter.setNotificationClickListener(this);

        callServerForNotification(page);

        read.setOnClickListener(v -> {

            allServerNotificationRead();
            swipeRefreshLayout.setRefreshing(false);
            notificationbadge.setVisibility(View.INVISIBLE);


        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isScrolling = true;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                swipeRefreshLayout.setRefreshing(false);
                totalItems = layoutManager.getItemCount();
                totalVisiableItems = layoutManager.getChildCount();
                itemsOutsideScreen = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                if (isScrolling && (totalVisiableItems + itemsOutsideScreen == totalItems)) {
                    isScrolling = false;
                    page = page + 1;
                    callServerForNotification(page);


                }
            }
        });


        if (getIntent().hasExtra("title"))
            getPendingNotification();

        checkingloginStatus();

    }

    private void allServerNotificationRead() {

        Call<ResponseBody> call = apiInterface.markasRead(loginInstance.getLoginId(), loginInstance.getCustomerId(), loginInstance.getToken());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.code() == 200) {
//                    Toast.makeText(AllNotification.this, "success", Toast.LENGTH_SHORT).show();
                    for (NotificationDto.Notification notificationDto1 : notificationDtos) {

                        System.out.println("Hello: " + notificationDto1);

                        notificationDto1.setSeen(true);
                        notificationAdapter.notifyDataSetChanged();

                    }
                    startActivity(new Intent(AllNotification.this,AllNotification.class));
                    finish();
                    CustomSnackbar.showSuccessSnakeBar(rootLayout,"All Notification are succesfully read.",AllNotification.this);

                } else {
                    //Maps the error message in ErrorMessageDto
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = null;
                    try {

                        mJson = parser.parse(response.errorBody().string());
                        Gson gson = new Gson();
                        ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                        CustomSnackbar.showFailureSnakeBar(rootLayout,errorMessageDto.getMessage(),AllNotification.this);
//                        Toast.makeText(AllNotification.this, errorMessageDto.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void getPendingNotification() {
//        String title,message,semester,faculty;
//        title=getIntent().getStringExtra("title");
//        message=getIntent().getStringExtra("body");
//        faculty=getIntent().getStringExtra("faculty");
//
//        StringBuffer stringBuffer=new StringBuffer();
//        stringBuffer.append(message+"\n\n"+"Thank You"+"\n"+"Texas Int'l College");
//        showMessage(title ,stringBuffer.toString());

    }


    public void callServerForNotification(long page) {
        Call<NotificationDto> call = apiService.listNotification(loginInstance.getLoginId(),
                loginInstance.getCustomerId(), loginInstance.getToken(), page, 20L, "id,asc");
        call.enqueue(new Callback<NotificationDto>() {
            @Override
            public void onResponse(Call<NotificationDto> call, Response<NotificationDto> response) {
                int status = response.code();
                if (response.isSuccessful()) {
                    NotificationDto notificationDto = response.body();
//                    Toast.makeText(AllNotification.this, String.valueOf(notificationDto.getNotifications().get(0).getTotalResponse()), Toast.LENGTH_SHORT).show();
                    System.out.println(notificationDto);
                    try{
                        if (notificationDto.getTotalUnseenNotifications() > 0) {
                            notificationbadge.setVisibility(View.GONE);
                            notificationbadge.setText(String.valueOf(notificationDto.getTotalUnseenNotifications()));
                            reducebadge = Integer.parseInt(notificationbadge.getText().toString());
                        } else {
                            notificationbadge.setVisibility(View.INVISIBLE);
                        }
                        Log.d("Response=====", response.toString());
                        Log.d("code =====", String.valueOf(response.code()));
                        Log.d("body =====", response.body().toString());
                        notificationDtos = response.body().getNotifications();
                        Log.d("Size::", String.valueOf(notificationDtos.size()));
                        notificationAdapter.addNotificationsToList(notificationDtos);
                        if (notificationAdapter.getItemCount() == 0) {
                            ShowNoContentPopUp.showMessage(AllNotification.this, "There are no Notification available right now.");

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }





                } else if (status == 401) {
                    StatusChecker.statusCheck(AllNotification.this);

                } else {
                    Log.d("Status Code::", String.valueOf(response.code()));
                    //Maps the error message in ErrorMessageDto
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = null;
                    try {

                        mJson = parser.parse(response.errorBody().string());
                        Gson gson = new Gson();
                        ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                        CustomSnackbar.showFailureSnakeBar(rootLayout,errorMessageDto.getMessage(),AllNotification.this);
//                        Toast.makeText(AllNotification.this, errorMessageDto.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onFailure(Call<NotificationDto> call, Throwable t) {
                Log.d("AllNotifiFalilure==", t.getMessage());
                CustomSnackbar.checkErrorResponse(rootLayout, AllNotification.this);
            }
        });

    }

    private void requestForNewToken() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_password_ask_layout, null);
        dialogBuilder.setView(dialogView);

        EditText etPassword = dialogView.findViewById(R.id.et_oops_password);
        Button btnCreatNewSession = dialogView.findViewById(R.id.btn_oops_create_new_session);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationUpBottom;

        alertDialog.show();
        btnCreatNewSession.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(etPassword.getText())) {

                AutoLogin.getNewToken(AllNotification.this, etPassword.getText().toString());
                callServerForNotification(1);
                alertDialog.dismiss();

            } else {
                etPassword.setError("Password Required!!!");
                etPassword.requestFocus();
            }

        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }


    public void markNotificationAsRead(Integer id) {

        System.out.println("Id of notification:::"+id);

        Call<ResponseBody> call = apiService.markNotificationAsRead(Long.valueOf(id), loginInstance.getLoginId(),
                loginInstance.getCustomerId(), loginInstance.getToken());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int reducingnoti;
                int status = response.code();
                if (response.isSuccessful()) {
//                    String responseMsg = response.body().toString();

                    for (NotificationDto.Notification notificationDto1 : notificationDtos) {

                        if (notificationDto1.getId() == id) {
                            notificationDto1.setSeen(true);
                            notificationAdapter.notifyDataSetChanged();
                            reducebadge = reducebadge - 1;
                            reducebadgeString = String.valueOf(reducebadge);
                            notificationbadge.setText(reducebadgeString);


                        }

                        if (reducebadge == 0) {

                            notificationbadge.setVisibility(View.INVISIBLE);

                        }


                    }


                    Log.d("Notification: ", "Successfully marked as read");
                } else if (status == 401) {
                    StatusChecker.statusCheck(AllNotification.this);

                } else {
                    Log.d("Status Code::", String.valueOf(response.code()));
                    //Maps the error message in ErrorMessageDto
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = null;
                    try {

                        mJson = parser.parse(response.errorBody().string());
                        Gson gson = new Gson();
                        ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                        Toast.makeText(AllNotification.this, errorMessageDto.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("AllNotifiFalilure==", t.getMessage());
                CustomSnackbar.checkErrorResponse(rootLayout, AllNotification.this);
            }
        });
    }

    @Override
    public void onRefresh() {
        notificationAdapter = new NotificationAdapter(this);
        recyclerView = findViewById(R.id.recyclerview_notification);
        callServerForNotification(0);
        notificationAdapter.setNotificationClickListener(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(notificationAdapter);
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout.setRefreshing(false);        //callServerForNotification(0);

    }

    private void ClearBackStack() {
        final FragmentManager fm = getSupportFragmentManager();
        while (fm.getBackStackEntryCount() > 0) {
            fm.popBackStackImmediate();
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Bundle bundle = notificationAdapter.getNotificationValue(item.getOrder());
        Long messageId = bundle.getLong("messageId");
        String title = bundle.getString("title");
        String message = bundle.getString("message");
        Integer id = bundle.getInt("notiId");
        notificationId = Long.valueOf(bundle.getInt("senderId"));
        switch (item.getItemId()) {
//            case NotificationAdapter.CONTEXT_MENU_REPLY:
//                Intent intent = new Intent(this, NotificationReply.class);
//                intent.putExtra("notificationId", messageId);
//                intent.putExtra("loginId", loginInstance.getLoginId());
//                intent.putExtra("token", loginInstance.getToken());
//                intent.putExtra("customerId", loginInstance.getCustomerId());
//                startActivity(intent);
//                finish();
//                return true;
            case NotificationAdapter.CONTEXT_MENU_MARKASREAD:
                markNotificationAsRead(id);
                startActivity(new Intent(this, AllNotification.class));
                finish();
                return true;
            case NotificationAdapter.CONTEXT_MENU_DELETE:
                if (loginInstance.getLoginId().equals(notificationId)) {
//                    Toast.makeText(AllNotification.this, "Inside delete view.", Toast.LENGTH_SHORT).show();
                    editDeleteOption = new EditDeleteOption(apiInterface, loginInstance.getLoginId(), messageId, loginInstance.getToken(), AllNotification.this);
                    editDeleteOption.deleteNotification();
                    finish();
                } else {
                    Toast.makeText(AllNotification.this, "This is not your notification.", Toast.LENGTH_SHORT).show();
                }
                return true;
            case NotificationAdapter.CONTEXT_MENU_EDIT:
                if (loginInstance.getLoginId().equals(notificationId)) {
//                    Toast.makeText(AllNotification.this, "Successfully matched.", Toast.LENGTH_SHORT).show();
                    Intent intentEdit = new Intent(AllNotification.this, EditNotification.class);
                    intentEdit.putExtra("title", title);
                    intentEdit.putExtra("message", message);
                    intentEdit.putExtra("loginId", loginInstance.getLoginId());
                    intentEdit.putExtra("messageId", messageId);
                    intentEdit.putExtra("token", loginInstance.getToken());
                    intentEdit.putExtra("customerId", loginInstance.getCustomerId());
                    startActivity(intentEdit);
                    finish();
                } else {
                    Toast.makeText(AllNotification.this, "This is not your notification", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }


    @Override
    public void itemClicked(View v, NotificationDto.Notification notificationDto, int position) {
        senderId = Long.valueOf(notificationDto.getSender().getSenderId());

        markNotificationAsRead(notificationDto.getNotificationReceiverId().intValue());


        Long messageId = Long.valueOf(notificationDto.getNotificationId());
        Intent intent = new Intent(this, NotificationReply.class);
        intent.putExtra("notificationId", notificationDto.getNotificationId());
        intent.putExtra("loginId", loginInstance.getLoginId());
        intent.putExtra("notificationMessage",notificationDto.getMessage());
        intent.putExtra("notificationTitle",notificationDto.getTitle());
        intent.putExtra("token", loginInstance.getToken());
        intent.putExtra("customerId", loginInstance.getCustomerId());
        intent.putExtra("sentDate", notificationDto.getSentDate());

        Gson senderObject = new Gson();
        String sender = senderObject.toJson(notificationDto.getSender());

        intent.putExtra("sender", sender);
        startActivity(intent);



//        showMessage(messageId, senderId, notificationDto.getTitle(), notificationDto.getMessage(), notificationDto.getTotalResponse());

    }

    private void showMessage(Long messageId, Long senderId, String title, String message, Long totalResponse) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_desigh_to_show_notification_detail, null);
        dialogBuilder.setView(dialogView);

        TextView titleView = dialogView.findViewById(R.id.custom_alert_notificaton_title);
        TextView messageView = dialogView.findViewById(R.id.custom_slert_notificaton_message);
        Button closeBtn = dialogView.findViewById(R.id.edit_notification_button);
        TextView reply = dialogView.findViewById(R.id.reply);
        titleView.setText(title);
        messageView.setText(message);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationUpBottom;
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        if (totalResponse >= 1) {
            reply.setVisibility(View.VISIBLE);
            reply.setText(totalResponse + " Response");
            reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(AllNotification.this, NotificationReply.class);
                    intent.putExtra("notificationId", messageId);
                    intent.putExtra("loginId", loginInstance.getLoginId());
                    intent.putExtra("token", loginInstance.getToken());
                    intent.putExtra("customerId", loginInstance.getCustomerId());
                    startActivity(intent);
                    finish();

                }
            });
        }

        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
    }

    public void checkingloginStatus(){
        if(loginInstance.getUserRole().equals("STUDENT") || loginInstance.getUserRole().equals("TEACHER") || (loginInstance.getUserRole().equals("USER") && !loginInstance.getUserRole().equals("ADMIN") )){
            sendNotiFloatingBtn.setVisibility(View.INVISIBLE);
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("Starting activity:::");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        System.out.println("Post resumed::::");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("Post paused::::");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("Activity stopped:::::");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = new Intent(this, AllNotification.class);
        startActivity(intent);
        finish();
    }
}
