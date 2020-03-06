package com.imaginology.texas;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.Login.ChangePasswordDto;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.RoomDatabase.UserTeamResponse.TeamEntity;
import com.imaginology.texas.SnackBar.CustomSnackbar;
import com.imaginology.texas.Students.StudentDetails;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.DataValidityChecker;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAuthorizarion extends AppCompatActivity  {
    private ConstraintLayout rootRelativeLayout;
    private ApiInterface apiInterface;
    private UserLoginResponseEntity loginInstance;
    private List<TeamEntity> teamEntityList = new ArrayList<>();
    private String picUrl;
    private ImageView userUploadImage;
    private Button uploadImage;
    private Button chooseImage;
    private TextView dToolbar;
    private Bitmap bitmap;
    private Dialog showImageuploadDialog;
    private TabLayout tabLayout;
    private ProgressBar uploadProgressing;
    private File croppedImageUri;
    private String encoddedString;
    private String pPicUri;
    private Uri imageUri = null;
    private String user,userIdFetched;
    private Context context1;
    private Integer Id;
    private TextView backTxt;
    private ImageView stuImage;
    private StudentDetails studentDetail;
    private ImageView ivCurrentUser;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        rootRelativeLayout = findViewById(R.id.rootView);



    }


    public void fetchingUserName(Context mycontext, String userId,String rollNo){
        user = rollNo;
        apiInterface = ApiClient.getRetrofit(this).create(ApiInterface.class);
        //Getting loginInstance from database
        GetLoginInstanceFromDatabase loginInstanceAccessor = new GetLoginInstanceFromDatabase(this);
        loginInstance = loginInstanceAccessor.getLoginInstance();
        teamEntityList.addAll(loginInstanceAccessor.getTeamInstance());
        userIdFetched = userId;
        context1 = mycontext;
        showPwChangeEditForm(user);

    }

    public void showPwChangeEditForm(String userName) {

        Dialog pwChangeDialog = new Dialog(context1);

        pwChangeDialog.setContentView(R.layout.reset_password_input_form);
        EditText etNewPw = pwChangeDialog.findViewById(R.id.et_change_pw_new_pw);
        EditText etConfirmNewPw = pwChangeDialog.findViewById(R.id.et_change_pw_confirm_new_pw);
        Button btnChangePw = pwChangeDialog.findViewById(R.id.btn_change_pw);
        ImageView cancelImage=pwChangeDialog.findViewById(R.id.cancel_image);
        cancelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pwChangeDialog.dismiss();
            }
        });
        btnChangePw.setOnClickListener(view -> {
                if (DataValidityChecker.isEditTextDataValid(etNewPw, "New Password", true)) {
                    if (DataValidityChecker.isEditTextDataValid(etConfirmNewPw, "Confirm New Password", true)) {
                        if (etNewPw.getText().toString().trim().equals(etConfirmNewPw.getText().toString().trim())) {
                            ChangePasswordDto changePasswordDto = new ChangePasswordDto(userName,
                                    "", etNewPw.getText().toString().trim()
                                    , etConfirmNewPw.getText().toString().trim());

                            pwChangeDialog.dismiss();


                            sendPasswordToSrver(changePasswordDto,userIdFetched);
                        } else {
                            etConfirmNewPw.setError("Password not matched!!!");
                            etConfirmNewPw.requestFocus();
                        }
                    }
                }

        });

        if (pwChangeDialog.getWindow() != null) {
            pwChangeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            pwChangeDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        }
        pwChangeDialog.show();

    }

    private void sendPasswordToSrver(ChangePasswordDto changePasswordDto, String fetchedUserId) {

        Call<ResponseBody> call = apiInterface.resetPassword(loginInstance.getCustomerId(),loginInstance.getLoginId(),
                loginInstance.getToken(), changePasswordDto);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {

                    if (response.code() == 203) {
                        Toast.makeText(context1, "invalid old password", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context1, "password successfully changed ", Toast.LENGTH_SHORT).show();
                    }
//                    CustomSnackbar.make(rootRelativeLayout,"Password Successfully Changed",CustomSnackbar.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(AdminAuthorizarion.this, "call missmatched", Toast.LENGTH_SHORT).show();

                    //Maps the error message in ErrorMessageDto
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = null;
                    try {
                        mJson = parser.parse(response.errorBody().string());
                        Gson gson = new Gson();
                        ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                       // Toast.makeText(context1, errorMessageDto.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                CustomSnackbar.checkErrorResponse(rootRelativeLayout, context1);
            }
        });
    }

}
