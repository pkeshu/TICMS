package com.imaginology.texas.Notification.PushNotification;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.MainActivity;
import com.imaginology.texas.Notification.PushNotification.NotificationReply.NotificationReply;
import com.imaginology.texas.Notification.PushNotification.NotificationReply.NotificationReplyDto;
import com.imaginology.texas.Notification.sendNotification.SendNotificationActivity;
import com.imaginology.texas.service.ApiInterface;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditDeleteOption {

    Context getContext;
    ApiInterface apiInterface;
    Long loginId, customerId;
    Long id;
    String token;
    NotificationEditRequestDTO notificationEditRequestDTO;
    NotificationReplyDto notificationReplyDto;



    public EditDeleteOption(ApiInterface apiInterface, Long loginId, Long id, String token, Context getContext) {
        this.loginId = loginId;
        this.id = id;
        this.token = token;
        this.apiInterface = apiInterface;
        this.getContext = getContext;
    }


    public EditDeleteOption(ApiInterface apiInterface, Long loginId, Long customerId, NotificationEditRequestDTO notificationEditRequestDTO, String token, Context getContext) {
        this.apiInterface = apiInterface;
        this.loginId = loginId;
        this.customerId = customerId;
        this.notificationEditRequestDTO = notificationEditRequestDTO;
        this.token = token;
        this.getContext = getContext;

    }
    public EditDeleteOption(ApiInterface apiInterface, Long id,Long loginId, Long customerId, NotificationReplyDto notificationReplyDto, String token, Context getContext) {
        this.apiInterface = apiInterface;
        this.id=id;
        this.loginId = loginId;
        this.customerId = customerId;
        this.notificationReplyDto=notificationReplyDto;
        this.token = token;
        this.getContext = getContext;

    }

    public boolean deleteNotification() {
        System.out.println("Print:"+apiInterface);
        System.out.println("Print id: "+id);
        Call<Void> call = apiInterface.deleteNotification(loginId, id, token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                /*try {
//                    Toast.makeText(getContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                if (response.isSuccessful()) {
                    Toast.makeText(getContext, "The notification has been successfully deleted.", Toast.LENGTH_SHORT).show();
                    getContext.startActivity(new Intent(getContext,AllNotification.class));
                    return;
                } else {
                    Toast.makeText(getContext, response.message(), Toast.LENGTH_SHORT).show();

                    return;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
        return true;

    }

    public void editNotification() {
        System.out.println("edit method called");
        System.out.println("request dto "+ notificationEditRequestDTO);

        Call<Void> call = apiInterface.editNotification(loginId, customerId, notificationEditRequestDTO, token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    System.out.println("Success.");
                    Toast.makeText(getContext, "Success.", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getContext, "Message has been added.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext, AllNotification.class);
                    getContext.startActivity(intent);
                }else {
                    Toast.makeText(getContext, "Error: "+response.message(), Toast.LENGTH_SHORT).show();
                    /*try {
                        Toast.makeText(getContext, "Error: "+response.message(), Toast.LENGTH_SHORT).show();
//                        Toast.makeText(getContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
//                        System.out.println("Error: "+response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("Failure: "+t.getStackTrace());
            }
        });
    }
//    public void replyNotification(){
//        Call<Void> call=apiInterface.replyNotification(loginId,customerId,id,notificationReplyDto,token);
//        call.enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if(response.isSuccessful()){
////                    Toast.makeText(getContext, "Reply is success", Toast.LENGTH_SHORT).show();
////                    getContext.startActivity(new Intent(getContext,AllNotification.class));
//
////                    Intent intent=new Intent(getContext,NotificationReply.class);
////                    intent.putExtra("notificationId",notificationReplyDto.notifications.get())
////                    getContext.startActivity(new Intent(getContext,AllNotification.class));
//
//                }else {
//                    JsonParser parser = new JsonParser();
//                    JsonElement mJson = null;
//                    try {
//
//                        mJson = parser.parse(response.errorBody().string());
//                        Gson gson = new Gson();
//                        ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
//                        Toast.makeText(getContext, errorMessageDto.getMessage(), Toast.LENGTH_SHORT).show();
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
////                System.out.println("Failure: "+t.getStackTrace());
//                Toast.makeText(getContext, "Reply is failed.", Toast.LENGTH_SHORT).show();
//
//
//            }
//        });
//    }
}

