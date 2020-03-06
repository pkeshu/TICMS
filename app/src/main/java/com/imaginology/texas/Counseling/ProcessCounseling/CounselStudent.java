package com.imaginology.texas.Counseling.ProcessCounseling;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.imaginology.texas.Counseling.CounselingDetail.CounselingDetail;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.SnackBar.CustomSnackbar;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;
import com.imaginology.texas.util.ImageViewLoader;

import java.io.File;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CounselStudent extends AppCompatActivity{
    private TextView editTxt;
    private TextView counseFullname, counselEmail, counselMobilenumber;
    private TextView couselAddress, counselSchool, counselCourse, couselCounsellor, counselRecommendator;
    private Button saveBtn;
    private EditText counselTotalFee, counselDiscountFee, counselRemarks;
    private ImageView counselStudentImage;
    private String fullname, email, mobileNumber, address, schoolName, course, counsellor, recomendator, totalFee, discountFee, remarks, profilePicture;
    private ApiInterface apiInterface;
    private UserLoginResponseEntity loginInstance;
    private Long studentCounselId;
    private ProgressBar progressBar;
    private ConstraintLayout rootRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counseling_details_edit);
        editTxt = findViewById(R.id.edit_txt);
        bindView();
        progressBar.setVisibility(View.GONE);
        getDataFromIntent();
        setValue();
        editTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(CounselStudent.this,CounselingDetail.class);
                intent.putExtra("selectedCounsellingId",studentCounselId);
                startActivity(intent);
                finish();
            }
        });

        apiInterface = ApiClient.getRetrofit(this).create(ApiInterface.class);
        loginInstance = new GetLoginInstanceFromDatabase(this).getLoginInstance();
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String remarks = counselRemarks.getText().toString().trim();
                Long discountFee = (long) 0;
                Long totalFee = (long) 0;
                if (!counselDiscountFee.getText().toString().trim().equals("null") && !counselDiscountFee.getText().toString().trim().equals("Not Available") && Long.valueOf(counselDiscountFee.getText().toString().trim()) > 0)
                    discountFee = Long.valueOf(counselDiscountFee.getText().toString().trim());
                if (!counselTotalFee.getText().toString().trim().equals("null") && !counselTotalFee.getText().toString().trim().equals("Not Available"))
                    totalFee = Long.valueOf(counselTotalFee.getText().toString().trim());
                EditCounsellingUploadToServer(remarks, discountFee, totalFee);
            }
        });


    }

    private void bindView() {
        saveBtn = findViewById(R.id.couns_save_btn);
        counseFullname = findViewById(R.id.counseling_full_name);
        counselEmail = findViewById(R.id.counseling_email);
        counselMobilenumber = findViewById(R.id.counseling_phone);
        couselAddress = findViewById(R.id.counseling_address);
        counselSchool = findViewById(R.id.counseling_shoolname);
        counselCourse = findViewById(R.id.counseling_course);
        counselTotalFee = findViewById(R.id.counseling_total_fee);
        counselDiscountFee = findViewById(R.id.counseling_negotiated_fee);
        couselCounsellor = findViewById(R.id.counseling_counsellor);
        counselRecommendator = findViewById(R.id.counseling_recomendator);
        counselRemarks = findViewById(R.id.counseling_remarks);
        counselStudentImage = findViewById(R.id.circleImageView);
        progressBar = findViewById(R.id.counselling_progress_bar);
        rootRelativeLayout=findViewById(R.id.root_layout);
    }

    private void getDataFromIntent() {
        Intent data = getIntent();
        fullname = data.getExtras().getString("fullname");
        email = data.getExtras().getString("email");
        mobileNumber = data.getExtras().getString("mobileNumber");
        address = data.getExtras().getString("counsAddress");
        schoolName = data.getExtras().getString("counsSchoolName");
        course = data.getExtras().getString("counsCourse");
        counsellor = data.getExtras().getString("counseledBy");
        recomendator = data.getExtras().getString("counsRecomendator");
        totalFee = data.getExtras().getString("counsTotalFee");
        discountFee = data.getExtras().getString("negotiatedFee");
        remarks = data.getExtras().getString("counsRemarks");
        profilePicture = data.getExtras().getString("image");
        studentCounselId = data.getExtras().getLong("selectedCounsellingId");
    }

    private void setValue() {
        counseFullname.setText(fullname);
        counselEmail.setText(email);
        counselMobilenumber.setText(mobileNumber);
        ImageViewLoader.loadImage(CounselStudent.this, profilePicture, counselStudentImage);
        couselAddress.setText(address);
        counselSchool.setText(schoolName);
        counselCourse.setText(course);
        counselTotalFee.setText(totalFee);
        counselDiscountFee.setText(discountFee);
        couselCounsellor.setText(counsellor);
        counselRecommendator.setText(recomendator);
        if (remarks.equals("Not Available"))
            counselRemarks.setHint(remarks);
    }

    private void EditCounsellingUploadToServer(String remarks, Long discountFee, Long totalFee) {
        progressBar.setVisibility(View.VISIBLE);
        Log.d("kesharpaudel:", remarks + " " + String.valueOf(discountFee) + " " + String.valueOf(totalFee));
        ProcessCounselDto editCounselDto = new ProcessCounselDto(discountFee, remarks, totalFee);

        Call<ResponseBody> call = apiInterface.processCounsil(loginInstance.getCustomerId(),
                loginInstance.getLoginId(),
                studentCounselId.intValue(),
                editCounselDto,
                loginInstance.getToken());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("responseCode:", String.valueOf(response.code()));
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CounselStudent.this, "Counselled.", Toast.LENGTH_SHORT).show();
                    Intent passIntent = new Intent(CounselStudent.this, CounselingDetail.class);
                    passIntent.putExtra("selectedCounsellingId", studentCounselId);
                    startActivity(passIntent);
                    finish();
                } else {
                    progressBar.setVisibility(View.GONE);
                    //Maps the error message in ErrorMessageDto
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = null;
                    try {
                        mJson = parser.parse(response.errorBody().string());
                        Gson gson = new Gson();
                        ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                        CustomSnackbar.showFailureSnakeBar(rootRelativeLayout, errorMessageDto.getMessage(), CounselStudent.this);
                     //   Toast.makeText(getApplicationContext(), errorMessageDto.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
               // Toast.makeText(getApplicationContext(), R.string.no_response, Toast.LENGTH_SHORT).show();
                CustomSnackbar.showFailureSnakeBar(rootRelativeLayout, String.valueOf(R.string.no_response), CounselStudent.this);
                progressBar.setVisibility(View.GONE);
            }
        });
    }


}
