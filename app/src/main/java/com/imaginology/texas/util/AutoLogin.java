package com.imaginology.texas.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.Login.LoginDto;
import com.imaginology.texas.R;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AutoLogin {
    private static final String TAG = "Auto Login";
    public static void getNewToken(Context context,String password){
        SharedPreferences sharedPreferences = context.getSharedPreferences("DeviceToken", Context.MODE_PRIVATE);
        String deviceId = sharedPreferences.getString("token", "");
        SharedPreferences sharedPref = context.getSharedPreferences("loginDetails", Context.MODE_PRIVATE);
        LoginDto loginDto=new LoginDto(password,sharedPref.getString("username",""),deviceId,"MOBILE");
        Log.d(TAG,loginDto.getDeviceId());
        Log.d(TAG,loginDto.getUsername());
        Log.d(TAG,loginDto.getPassword());

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<LoginDto> call = apiService.sendUsernameAndPassword(loginDto, "texasintl.edu.np");
        call.enqueue(new Callback<LoginDto>() {
            @Override
            public void onResponse(Call<LoginDto> call, Response<LoginDto> response) {
                Log.d(TAG,"Status Code: "+response.code());
                //Executed when Response is Success
                if (response.isSuccessful()) {
                    LoginDto loginDto = response.body();
                    //Saving Token and User ID when successfully logged in
                    SharedPreferences sharedPref = context.getSharedPreferences("loginDetails", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("token", loginDto.getUser().getToken());
//                    editor.putLong("id", loginDto.getUser().getId());
//                    editor.putLong("loginId", loginDto.getUser().getLoginId());
//                    if (!("SUPERADMIN".equals(loginDto.getUser().getUserRole()))) {
//                        editor.putLong("customerId", loginDto.getUser().getCustomerId());
//                    }
//
//                    editor.putString("firstname", loginDto.getUser().getFirstName());
//                    editor.putString("loginType", loginDto.getUser().getLoginType());
//                    editor.putString("lastname", loginDto.getUser().getLastName());
//                    editor.putString("email", loginDto.getUser().getEmail());
//                    editor.putString("userrole", loginDto.getUser().getUserRole());
//                    editor.putString("username", loginDto.getUser().getUsername());
                    editor.apply();

                    if (loginDto.getUser().getProfilePicture() != null) {
                        editor.putString("profilePicture", loginDto.getUser().getProfilePicture());
                    } else {
                        editor.putString("profilePicture", "nopic");
                    }

                    editor.apply();

                } else {
                    //Maps the error message in ErrorMessageDto
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = null;
                    try {
                        mJson = parser.parse(response.errorBody().string());
                        Gson gson = new Gson();
                        ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                        Toast.makeText(context, errorMessageDto.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<LoginDto> call, Throwable t) {

                Toast.makeText(context, R.string.no_response, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
