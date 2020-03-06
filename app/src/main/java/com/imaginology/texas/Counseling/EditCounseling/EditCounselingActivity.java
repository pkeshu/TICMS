package com.imaginology.texas.Counseling.EditCounseling;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
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
import com.imaginology.texas.Counseling.CounselingDetail.CounselingDetail;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.DataValidityChecker;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;
import com.imaginology.texas.util.ImageViewLoader;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditCounselingActivity extends AppCompatActivity {
    private TextView editTxt;
    private EditText counseFullname, counselEmail, counselMobilenumber;
    private EditText couselAddress, counselSchool, counselCourse, couselCounsellor, counselRecommendator;
    private Button saveBtn;
    private ImageView counselStudentImage;
    private EditText counselTotalFee, counselRemarks;
    private String fullname, email, mobileNumber, address, schoolName, course, counsellor, recomendator, totalFee, profilePicture;
    private Integer courseId;
    private ApiInterface apiInterface;
    private UserLoginResponseEntity loginInstance;
    private Long studentCounselId;
    private ProgressBar progressBar;
    private ConstraintLayout rootRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_counseling);
        editTxt = findViewById(R.id.edit_txt);
        bindView();
        couselCounsellor.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        getDataFromIntent();
        setValue();
        editTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(EditCounselingActivity.this, CounselingDetail.class);
                intent.putExtra("selectedCounsellingId", studentCounselId);
                startActivity(intent);
                finish();
            }
        });

        apiInterface = ApiClient.getRetrofit(this).create(ApiInterface.class);
        loginInstance = new GetLoginInstanceFromDatabase(this).getLoginInstance();
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long discountFee = (long) 0;
                Long totalFee = (long) 0;
                String fullname, email, phoneNO, address, schoolName, courseName, counsellorName, recomendatorName;
                fullname = counseFullname.getText().toString().trim();
                email = counselEmail.getText().toString().trim();
                phoneNO = counselMobilenumber.getText().toString().trim();
                address = couselAddress.getText().toString().trim();
                schoolName = counselSchool.getText().toString().trim();
                courseName = counselCourse.getText().toString().trim();
                counsellorName = couselCounsellor.getText().toString().trim();
                recomendatorName = counselRecommendator.getText().toString().trim();
                if (!counselTotalFee.getText().toString().trim().equals("null") && !counselTotalFee.getText().toString().trim().equals("Not Available")
                        && !counselTotalFee.getText().toString().trim().equals(""))
                    totalFee = Long.valueOf(counselTotalFee.getText().toString().trim());
                if (DataValidityChecker.isEditTextDataValid(counseFullname, fullname, true) &&
                        DataValidityChecker.isEditTextDataValid(counselMobilenumber, phoneNO, true)
                        )
                    EditCounselingSaveToServer(fullname, email, phoneNO, address, schoolName, courseName, counsellorName, recomendatorName, totalFee, discountFee);
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
        couselCounsellor = findViewById(R.id.counseling_counsellor);
        counselRecommendator = findViewById(R.id.counseling_recomendator);
        counselRemarks = findViewById(R.id.counseling_remarks);
        counselStudentImage = findViewById(R.id.circleImageView);
        progressBar = findViewById(R.id.counselling_progress_bar);
        rootRelativeLayout = findViewById(R.id.root_layout);
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
        profilePicture = data.getExtras().getString("image");
        studentCounselId = data.getExtras().getLong("selectedCounsellingId");

    }

    private void setValue() {
        counseFullname.setText(fullname);
        counselEmail.setText(email);
        counselMobilenumber.setText(mobileNumber);
        ImageViewLoader.loadImage(EditCounselingActivity.this, profilePicture, counselStudentImage);
        couselAddress.setText(address);
        counselSchool.setText(schoolName);
        counselCourse.setText(course);
        counselTotalFee.setText(totalFee);
        couselCounsellor.setText(counsellor);
        counselRecommendator.setText(recomendator);
    }

    private void EditCounselingSaveToServer(String fullname, String email, String phoneNO, String address, String schoolName, String courseName, String counsellorName, String recomendatorName, Long totalFee, Long discountFee) {
        progressBar.setVisibility(View.VISIBLE);
        EditCounselingDto editCounselingDto = new EditCounselingDto(
                address, counsellorName, email, fullname, studentCounselId, phoneNO, recomendatorName, schoolName, totalFee
        );

        Call<ResponseBody> call = apiInterface.editStudentCounselling(loginInstance.getCustomerId(),
                loginInstance.getLoginId(), editCounselingDto,
                loginInstance.getToken());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(EditCounselingActivity.this, "Counseling Edited.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditCounselingActivity.this, CounselingDetail.class);
                    intent.putExtra("selectedCounsellingId", studentCounselId);
                    startActivity(intent);
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
                        Toast.makeText(getApplicationContext(), errorMessageDto.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.no_response, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);

            }
        });
    }
}
