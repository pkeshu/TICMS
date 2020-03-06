package com.imaginology.texas.Counseling.CounselingDetail;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imaginology.texas.Counseling.EditCounseling.EditCounselingActivity;
import com.imaginology.texas.Counseling.ProcessCounseling.CounselHistoryAdapter;
import com.imaginology.texas.Counseling.ProcessCounseling.CounselStudent;
import com.imaginology.texas.Counseling.ProcessCounseling.ShowHistoryActivity;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.SnackBar.CustomSnackbar;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.CheckError;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;
import com.imaginology.texas.util.ImageViewLoader;
import com.imaginology.texas.util.ProfilePicEditRequest;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CounselingDetail extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private TextView counselingFullName, counselingEmail, counselingPhone, negotiatedFee, counseledBy;
    private Button counseling;
    private TextView counAddress, counSchoolName, counCourse, counTotalFee, counRecomendator, backTxt,counselGender;
    private ImageView counselStudentImage;
    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;
    private final String TAG = CounselingDetail.class.getSimpleName();
    public static final int CAMERA_REQUEST = 10;
    private Bitmap profilePic;
    private Long counsellingId;
    private String fullName, email, mobileNumber;
    private ApiInterface apiInterface;
    private UserLoginResponseEntity loginInstance;
    private ImageView iVTakePhoto;
    private Dialog showImageuploadDialog;
    private Uri imageUri = null;
    private ImageView userUploadImage;
    private Button uploadImage;
    private TextView dToolbar;
    private ProgressBar uploadProgressing;
    private File croppedImageUri;
    private String encoddedString;
    private Bitmap bitmap;
    private ConstraintLayout rootRelativeLayout;
    private ImageView close, upload;
    private PopupMenu popupMenu;
    private PopupWindow histryPopUpMenu;
    private String studentCounselProPic = null;
    private ProgressBar uploadImageProgress;
    private ImageView counsEdit, counselShowHistry;
    private View v;
    private CounselHistoryAdapter counselHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counseling_detail);
        progressBar = findViewById(R.id.counselling_progress_bar);
        counselingFullName = findViewById(R.id.counseling_full_name);
        counselingEmail = findViewById(R.id.counseling_email);
        counselingPhone = findViewById(R.id.counseling_phone);
        negotiatedFee = findViewById(R.id.counseling_negotiated_fee);
        counseledBy = findViewById(R.id.counseling_counsellor);
        counseling = findViewById(R.id.couns_edit_btn);
        backTxt = findViewById(R.id.back_txt);
        relativeLayout = findViewById(R.id.counseling_details_relative_layout);
        counselStudentImage = findViewById(R.id.circleImageView);
        counAddress = findViewById(R.id.counseling_address);
        counSchoolName = findViewById(R.id.counseling_shoolname);
        counCourse = findViewById(R.id.counseling_course);
        counTotalFee = findViewById(R.id.counseling_total_fee);
        counselGender=findViewById(R.id.counseling_gender);
        counRecomendator = findViewById(R.id.counseling_recomendator);
        uploadImageProgress = findViewById(R.id.upload_progress);
        rootRelativeLayout = findViewById(R.id.root_layout);
        counsEdit = findViewById(R.id.couns_edit);
        counselShowHistry = findViewById(R.id.counsel_histries);
        close = findViewById(R.id.close);
        upload = findViewById(R.id.upload);
        progressBar.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.GONE);
        counselHistoryAdapter = new CounselHistoryAdapter(this);
        loginInstance = new GetLoginInstanceFromDatabase(this).getLoginInstance();
        //Get user id from intent
        Intent intent = getIntent();
        counsellingId = intent.getLongExtra("selectedCounsellingId", 0);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        //For cached apiClient instance
        apiInterface = ApiClient.getRetrofit(this).create(ApiInterface.class);
        getCounsellingDetails(counsellingId);
        backTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        counselStudentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhotoPopUp();
            }
        });
    }

    private void getCounsellingDetails(Long counsellingId) {
        Call<CounselingDetailDto> call = apiInterface.counsellingInfo(loginInstance.getCustomerId(), loginInstance.getLoginId(), counsellingId,
                loginInstance.getToken());
        call.enqueue(new Callback<CounselingDetailDto>() {
            @Override
            public void onResponse(Call<CounselingDetailDto> call, Response<CounselingDetailDto> response) {
                if (response.isSuccessful()) {
                    CounselingDetailDto counselingDetail = response.body();
                    fullName = CheckError.checkNullString(counselingDetail.getFullName());
                    email = CheckError.checkNullString(counselingDetail.getEmail());
                    mobileNumber = CheckError.checkNullString(counselingDetail.getMobileNumber());
                    studentCounselProPic = CheckError.checkNullString(counselingDetail.getProfilePicture());
                    counselingFullName.setText(fullName);
                    counselingEmail.setText(email);
                    counselingPhone.setText(mobileNumber);
                    counselGender.setText(CheckError.checkNullString(counselingDetail.getGender()));
                    ImageViewLoader.loadImage(CounselingDetail.this, studentCounselProPic, counselStudentImage);
                    negotiatedFee.setText(CheckError.checkNullString(String.valueOf(counselingDetail.getNegotiatedFee())));
                    counseledBy.setText(CheckError.checkNullString(counselingDetail.getCounseledBy()));
                    counTotalFee.setText(CheckError.checkNullString(String.valueOf(counselingDetail.getTotalFee())));
                    counAddress.setText(CheckError.checkNullString(counselingDetail.getAddress()));
                    counCourse.setText(CheckError.checkNullString(counselingDetail.getCourseName()));
                    counSchoolName.setText(CheckError.checkNullString(counselingDetail.getSchoolName()));
                    counRecomendator.setText(CheckError.checkNullString(counselingDetail.getRecommendedBy()));
                    String remarks = null;
                    try {
                        remarks = counselingDetail.getHistories().get(counselingDetail.getHistories().size() - 1).getRemarks();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progressBar.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.VISIBLE);

                    //for process counsel
                    counseling.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent editIntent = new Intent(CounselingDetail.this, CounselStudent.class);
                            editIntent.putExtra("fullname", fullName);
                            editIntent.putExtra("email", email);
                            editIntent.putExtra("mobileNumber", mobileNumber);
                            editIntent.putExtra("image", studentCounselProPic);
                            editIntent.putExtra("negotiatedFee", CheckError.checkNullString(String.valueOf(counselingDetail.getNegotiatedFee())));
                            editIntent.putExtra("counseledBy", CheckError.checkNullString(counselingDetail.getCounseledBy()));
                            editIntent.putExtra("counsTotalFee", CheckError.checkNullString(String.valueOf(counselingDetail.getTotalFee())));
                            editIntent.putExtra("counsAddress", CheckError.checkNullString(counselingDetail.getAddress()));
                            editIntent.putExtra("counsCourse", CheckError.checkNullString(counselingDetail.getCourseName()));
                            editIntent.putExtra("counsSchoolName", CheckError.checkNullString(counselingDetail.getSchoolName()));
                            editIntent.putExtra("counsRecomendator", CheckError.checkNullString(counselingDetail.getRecommendedBy()));
                            String remarks = null;
                            try {
                                remarks = counselingDetail.getHistories().get(counselingDetail.getHistories().size() - 1).getRemarks();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            editIntent.putExtra("counsRemarks", CheckError.checkNullString(remarks));
                            editIntent.putExtra("selectedCounsellingId", counsellingId);
                            startActivity(editIntent);
                            finish();
                        }
                    });
                    //for history setup
                    List<History> historyList = counselingDetail.getHistories();
                    counselShowHistry.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (historyList.size() != 0) {
                                Intent intent = new Intent(CounselingDetail.this, ShowHistoryActivity.class);
                                intent.putExtra("selectedCounsellingId", counsellingId);
                                startActivity(intent);
                                finish();

                            }
//                                showHistryPopUPWindow(view);
                            else
                                CustomSnackbar.showFailureSnakeBar(rootRelativeLayout, "No History Found", CounselingDetail.this);
                        }
                    });
                    //for edit counsel
                    counsEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent editIntent = new Intent(CounselingDetail.this, EditCounselingActivity.class);
                            editIntent.putExtra("fullname", fullName);
                            editIntent.putExtra("email", email);
                            editIntent.putExtra("mobileNumber", mobileNumber);
                            editIntent.putExtra("image", studentCounselProPic);
                            editIntent.putExtra("counseledBy", counselingDetail.getCounseledBy());
                            editIntent.putExtra("counsTotalFee", String.valueOf(counselingDetail.getTotalFee()));
                            editIntent.putExtra("counsAddress", counselingDetail.getAddress());
                            editIntent.putExtra("counsCourse", counselingDetail.getCourseName());
                            editIntent.putExtra("counsSchoolName", counselingDetail.getSchoolName());
                            editIntent.putExtra("counsRecomendator", counselingDetail.getRecommendedBy());
                            editIntent.putExtra("selectedCounsellingId", counsellingId);
                            startActivity(editIntent);
                            finish();
                        }
                    });
                } else {
                    progressBar.setVisibility(View.GONE);
                    //Maps the error message in ErrorMessageDto
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = null;
                    try {
                        mJson = parser.parse(response.errorBody().string());
                        Gson gson = new Gson();
                        ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                       // Toast.makeText(getApplicationContext(), errorMessageDto.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CounselingDetailDto> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.no_response, Toast.LENGTH_SHORT).show();
                Log.d(TAG, t.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void takePhotoPopUp() {
        popupMenu = new PopupMenu(CounselingDetail.this, counselStudentImage);
        popupMenu.inflate(R.menu.take_new_photo_menu);
        popupMenu.setOnMenuItemClickListener(CounselingDetail.this);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.take_photo:
                selectImage();
                break;
            default:
                break;
        }
        return false;
    }

    private void selectImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(3, 3)
                .setMaxCropResultSize(4096, 4096)
                .start(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                CustomSnackbar.showFailureSnakeBar(rootRelativeLayout, "Cancelling, required permissions are not granted", CounselingDetail.this);
               // Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                cropRequest();
            } else {
                CustomSnackbar.showFailureSnakeBar(rootRelativeLayout, "Cancelling, required permissions are not granted", CounselingDetail.this);
               // Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imageUri = resultUri;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }
        counselStudentImage.setImageURI(imageUri);
        if (imageUri != null) {
            upload.setVisibility(View.VISIBLE);
            close.setVisibility(View.VISIBLE);
            popupMenu.dismiss();
        }
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload.setVisibility(View.GONE);
                close.setVisibility(View.GONE);
                uploadImageToServer();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageViewLoader.loadImage(CounselingDetail.this, studentCounselProPic, counselStudentImage);
                upload.setVisibility(View.GONE);
                close.setVisibility(View.GONE);
            }
        });
    }

    private void cropRequest() {
        CropImage.activity()
                .setCropShape(CropImageView.CropShape.OVAL)
                .setAspectRatio(3, 3)
                .setMaxCropResultSize(4096, 4096)
                .start(this);
    }

    private void uploadImageToServer() {
        uploadImageProgress.setVisibility(View.VISIBLE);
        if (imageUri != null) {
            encoddedString = convertToBase64(imageUri);
            Log.d("ecncodedString:", encoddedString);
            Log.d("couselcounsel:", String.valueOf(counsellingId));
            Call<ResponseBody> call = apiInterface.editProfilePictureofStudentInCounseling(loginInstance.getCustomerId(),
                    loginInstance.getLoginId(),
                    Integer.valueOf(String.valueOf(counsellingId)),
                    new ProfilePicEditRequest(encoddedString), loginInstance.getToken());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
//                        ProfilePicEditRequest profilePicChangeDto = response.body();
//                        String pPicUri = profilePicChangeDto.getImageUrl();
//                        ImageViewLoader.loadImage(CounselingDetail.this, pPicUri, counselStudentImage);
                        uploadImageProgress.setVisibility(View.GONE);
                        close.setVisibility(View.GONE);
                        upload.setVisibility(View.GONE);
                        CustomSnackbar.showSuccessSnakeBar(rootRelativeLayout, "Image Changed Successfully", CounselingDetail.this);
                    } else {
                        //Maps the error message in ErrorMessageDto
                        JsonParser parser = new JsonParser();
                        JsonElement mJson = null;
                        try {
                            mJson = parser.parse(response.errorBody().string());
                            Gson gson = new Gson();
                            ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                            CustomSnackbar.showFailureSnakeBar(rootRelativeLayout, errorMessageDto.getMessage(), CounselingDetail.this);
                            uploadImageProgress.setVisibility(View.GONE);
                            upload.setVisibility(View.GONE);
                            close.setVisibility(View.GONE);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    CustomSnackbar.checkErrorResponse(rootRelativeLayout, CounselingDetail.this);
                    upload.setVisibility(View.GONE);
                    close.setVisibility(View.GONE);
                    uploadImageProgress.setVisibility(View.GONE);
                }
            });
        } else {
            showImageuploadDialog.dismiss();
            CustomSnackbar.showFailureSnakeBar(rootRelativeLayout, "Select Image first", CounselingDetail.this);
        }
    }

    private String convertToBase64(Uri imagePath) {

        croppedImageUri = new File(imagePath.getPath());

        try {
            bitmap = new Compressor(this)
                    .compressToBitmap(croppedImageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
}

//    private void showHistryPopUPWindow(View view) {
////        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
////        v = inflater.inflate(R.layout.counsel_show_histry_layout, null);
////        v.setBackgroundColor(getResources().getColor(R.color.colorWhite));
////        histryPopUpMenu = new PopupWindow(v, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
//////        histryPopUpMenu.showAsDropDown(view, -153, 0);
////        histryPopUpMenu.showAtLocation(view,Gravity.BOTTOM,0,0);
////        RecyclerView historyRecyclerView = v.findViewById(R.id.recyclerview);
////        historyRecyclerView.setHasFixedSize(true);
////        historyRecyclerView.setLayoutManager(new LinearLayoutManager(CounselingDetail.this));
////        historyRecyclerView.setAdapter(counselHistoryAdapter);
//
//    }
//}


