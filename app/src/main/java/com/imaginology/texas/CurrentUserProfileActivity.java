package com.imaginology.texas;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.Login.ChangePasswordDto;
import com.imaginology.texas.RoomDatabase.TicmsRoomDatabase;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.RoomDatabase.UserTeamResponse.TeamEntity;
import com.imaginology.texas.SnackBar.CustomSnackbar;
import com.imaginology.texas.Team.TeamDto;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.CheckError;
import com.imaginology.texas.util.DataValidityChecker;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;
import com.imaginology.texas.util.ImageViewLoader;
import com.imaginology.texas.util.ProfilePicEditRequest;
import com.imaginology.texas.util.ProfilePicEditResponseDTO;
import com.imaginology.texas.util.SupportActionBarInitializer;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CurrentUserProfileActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private TextView tvUserName, tvUserRole, tvBackTxt, userStatusText, ivSettingImage;
    private ImageView ivCurrentUser, ivUserStatusImage;
    //    private TextView tvChangePw;
    private ApiInterface apiInterface;
    private UserLoginResponseEntity loginInstance;
    private List<TeamEntity> teamEntityList = new ArrayList<>();
    private ConstraintLayout rootRelativeLayout;
    private String picUrl;
    private Uri imageUri = null;
    private ImageView userUploadImage;
    private Button uploadImage;
    private Button chooseImage;
    private TextView dToolbar;
    private Bitmap bitmap;
    private Dialog showImageuploadDialog;
    private boolean isScrolling = false;
    private BottomNavigationView nvHome;
    TextView txtChangePw, txtChangeImage;
    private ViewPager pager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private ConstraintLayout constraintLayout;
    private StringBuilder fullName, userName;
    private ProgressBar uploadProgressing;
    private File croppedImageUri;
    private String encoddedString;
    private String pPicUri;
    private FrameLayout tvFramelayout, middleFramelayout;


    public CurrentUserProfileActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        apiInterface = ApiClient.getRetrofit(this).create(ApiInterface.class);
        //Getting loginInstance from database
        GetLoginInstanceFromDatabase loginInstanceAccessor = new GetLoginInstanceFromDatabase(this);
        loginInstance = loginInstanceAccessor.getLoginInstance();
        teamEntityList.addAll(loginInstanceAccessor.getTeamInstance());
        bindViews();
        updateUI();
        tvBackTxt.setOnClickListener(this);


        System.out.println("User Type is:::::: " + loginInstance.getUserRole());

        ivSettingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu = new PopupMenu(CurrentUserProfileActivity.this, ivSettingImage);
                popupMenu.inflate(R.menu.setting_menu);
                popupMenu.setOnMenuItemClickListener(CurrentUserProfileActivity.this);
                if (loginInstance.getUserRole().equals("CUSTOMER") || loginInstance.getUserRole().equals("TEACHER") || loginInstance.getUserRole().equals("STUDENT") || loginInstance.getUserRole().equals("USER"))
                    popupMenu.getMenu().findItem(R.id.changeImage).setVisible(false);
                popupMenu.getMenu().findItem(R.id.privateMessage).setVisible(false);
                popupMenu.show();
            }
        });
        nvHome.setVisibility(View.GONE);


        ivCurrentUser.setOnClickListener(this);

    }


    private void updateUI() {
        if (loginInstance.getUserRole().equals("CUSTOMER")) {
            tvUserName.setText("This is customer");
            tvUserRole.setText("");
            Picasso.with(this)
                    .load(R.drawable.user)
                    .into(ivCurrentUser);
            pager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);

        } else {

            GetLoginInstanceFromDatabase loginInstanceAccessor = new GetLoginInstanceFromDatabase(this);
            UserLoginResponseEntity loginInstance = loginInstanceAccessor.getLoginInstance();


            userName = new StringBuilder("@");
            userName.append(loginInstance.getUserName());
            tvUserName.setText(userName);
            tvUserRole.setText(loginInstance.getUserRole());
            ivUserStatusImage.setVisibility(View.VISIBLE);
            picUrl = loginInstance.getProfilePicture();
            ImageViewLoader.loadImage(CurrentUserProfileActivity.this, picUrl, ivCurrentUser);

            //for passing user details to basicinformation fragment
            String phone = CheckError.checkNullString(loginInstance.getMobileNumber());
            String email = CheckError.checkNullString(loginInstance.getEmail());
            String gender = CheckError.checkNullString(loginInstance.getGender());
            fullName = new StringBuilder(loginInstance.getFirstName());
            fullName.append(" ");
            fullName.append(loginInstance.getLastName());
            List<TeamDto.Content> contentList = new ArrayList<>();
            FragmentManager fm = this.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            BasicInformationFragment basicInformationFragment = new BasicInformationFragment();
            Bundle bundle = new Bundle();

            bundle.putParcelableArrayList("teamList", (ArrayList<? extends Parcelable>) teamEntityList);
            bundle.putParcelableArrayList("userTeamList", (ArrayList<? extends Parcelable>) contentList);
            bundle.putString("fullname", String.valueOf(fullName));
            bundle.putString("email", email);
            bundle.putString("phone", phone);
            bundle.putString("gender", gender);
            basicInformationFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.fragment_holder_basic_info, basicInformationFragment);
            fragmentTransaction.commit();


        }
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {
                switch (item.getItemId()) {
                    case R.id.bottom_nav_home:
                        MainFragment fragment = new MainFragment();
                        SupportActionBarInitializer.setSupportActionBarTitle(getSupportActionBar(), "Home");
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.home_contener, fragment);
                        fragmentTransaction.commit();
                        ivSettingImage.setVisibility(View.GONE);
                        nvHome.setVisibility(View.GONE);
                        tvFramelayout.setVisibility(View.GONE);
                        middleFramelayout.setVisibility(View.GONE);
                        tabLayout.setVisibility(View.GONE);
                        ivCurrentUser.setVisibility(View.GONE);
                        userStatusText.setVisibility(View.GONE);
                        ivUserStatusImage.setVisibility(View.GONE);
//                        startActivity(new Intent(this,MainActivity.class));
                        ClearBackStack();
                }
                return true;
            };

    private void bindViews() {
        tvUserName = findViewById(R.id.user_username);
        tvUserName.setSelected(true);
        tvUserRole = findViewById(R.id.user_role);
        ivCurrentUser = findViewById(R.id.user_image_view);
        ivUserStatusImage = findViewById(R.id.user_status_image);
        tvBackTxt = findViewById(R.id.back_txt);
        nvHome = findViewById(R.id.bottomnavigation);
        ivSettingImage = findViewById(R.id.setting_image);
        userStatusText = findViewById(R.id.active_status_txt);
        rootRelativeLayout = findViewById(R.id.rootView);

        //for viewPager
        pager = findViewById(R.id.pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(viewPagerAdapter);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);
        tabLayout.removeTabAt(2);
        tabLayout.removeTabAt(1);
        ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                boolean swipeEnabled = false;
                if (!swipeEnabled) {
                    if (pager.getAdapter().getCount() > 1) {
                        pager.setCurrentItem(1);
                        pager.setCurrentItem(2);
                    }
                }

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        pager.addOnPageChangeListener(onPageChangeListener);
        constraintLayout = findViewById(R.id.constraintLayout2);
        constraintLayout.setVisibility(View.GONE);

        tvFramelayout = findViewById(R.id.frameLayout);
       // middleFramelayout = findViewById(R.id.fragment_holder_basic_info);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        if (view.equals(ivCurrentUser)) {
            showProfilePicture();

        }
        if (view.equals(tvBackTxt))
            goBack();
    }

    private void showProfilePicture() {
        Dialog showUserProfilePicture = new Dialog(this);
        showUserProfilePicture.setContentView(R.layout.show_user_image_form);
        ImageView userImage = showUserProfilePicture.findViewById(R.id.userImage);
        ImageViewLoader.loadImage(this, picUrl, userImage);
        if (showUserProfilePicture.getWindow() != null) {
            showUserProfilePicture.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            showUserProfilePicture.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        }
        showUserProfilePicture.show();
    }


    private void goBack() {
        onBackPressed();
        return;
    }

    private void showPictureDialog() {
        showImageuploadDialog = new Dialog(this);
        showImageuploadDialog.setContentView(R.layout.image_upload_dialog);
        userUploadImage = showImageuploadDialog.findViewById(R.id.chooseImage);
        uploadImage = showImageuploadDialog.findViewById(R.id.btn_upload);
        dToolbar = showImageuploadDialog.findViewById(R.id.back_btn);
        uploadProgressing = showImageuploadDialog.findViewById(R.id.upload_progress);
        TextView backText = showImageuploadDialog.findViewById(R.id.back_btn);
        ImageViewLoader.loadImage(this, picUrl, userUploadImage);
        userUploadImage.setVisibility(View.VISIBLE);
        uploadProgressing.setVisibility(View.GONE);
        uploadImage.setVisibility(View.GONE);
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageuploadDialog.dismiss();
            }
        });
        if (loginInstance.getUserRole().equals("ADMIN")) {
            uploadImage.setVisibility(View.VISIBLE);
        } else {
            dToolbar.setVisibility(View.GONE);
//            chooseImage.setVisibility(View.GONE);
            uploadImage.setVisibility(View.GONE);
        }
        if (showImageuploadDialog.getWindow() != null) {
            showImageuploadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            showImageuploadDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);

        }
        showImageuploadDialog.show();
//        chooseImage.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                selectImage();
//            }
//        });
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageToServer();
            }
        });
    }

    private void uploadImageToServer() {
        if (imageUri != null) {
            encoddedString = convertToBase64(imageUri);

            Call<ProfilePicEditResponseDTO> call = apiInterface.uplodProfilePic(loginInstance.getUserId(),
                    loginInstance.getLoginId(), loginInstance.getCustomerId(), new ProfilePicEditRequest(encoddedString), loginInstance.getToken());
            uploadProgressing.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<ProfilePicEditResponseDTO>() {
                @Override
                public void onResponse(Call<ProfilePicEditResponseDTO> call, Response<ProfilePicEditResponseDTO> response) {
                    if (response.isSuccessful()) {
                        ProfilePicEditResponseDTO profilePicChangeDto = response.body();
                        pPicUri = profilePicChangeDto.getUrl();
                        ImageViewLoader.loadImage(CurrentUserProfileActivity.this, pPicUri, ivCurrentUser);
                        uploadProgressing.setVisibility(View.GONE);
                        showImageuploadDialog.dismiss();
                        //update user details
                        UpdateUserDetails(profilePicChangeDto);
                        startActivity(new Intent(CurrentUserProfileActivity.this, CurrentUserProfileActivity.class));
                        finish();
                        CustomSnackbar.showSuccessSnakeBar(rootRelativeLayout, "Image Changed Successfully", CurrentUserProfileActivity.this);
                    } else {
                        //Maps the error message in ErrorMessageDto

                        System.out.println("response not success====="+response.code());
                        System.out.println("Not success due to====="+response.message());
                        System.out.println("====="+response.errorBody().toString());

                        JsonParser parser = new JsonParser();
                        JsonElement mJson = null;
                        try {
                            mJson = parser.parse(response.errorBody().string());
                            Gson gson = new Gson();
                            ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                            CustomSnackbar.showFailureSnakeBar(rootRelativeLayout, errorMessageDto.getMessage(), CurrentUserProfileActivity.this);
                            uploadProgressing.setVisibility(View.GONE);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ProfilePicEditResponseDTO> call, Throwable t) {
                    System.out.println("Response on failure=====");
                    CustomSnackbar.checkErrorResponse(ivCurrentUser, CurrentUserProfileActivity.this);
                    showImageuploadDialog.dismiss();
                    uploadProgressing.setVisibility(View.GONE);

                }
            });
        } else {
            showImageuploadDialog.dismiss();
            CustomSnackbar.showFailureSnakeBar(rootRelativeLayout, "Select Image first", CurrentUserProfileActivity.this);

//
        }


    }

    private void UpdateUserDetails(ProfilePicEditResponseDTO profilePicChangeDto) {
        TicmsRoomDatabase ticmsRoomDatabase = TicmsRoomDatabase.getDatabaseInstance(this);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        ticmsRoomDatabase.userLoginResponseDao().updateImageUrl(loginInstance.getId(), profilePicChangeDto.getUrl());
                    }
                });
            }
        });
        updateUI();

    }

    private void selectImage() {
        if (loginInstance.getUserRole().equals("CUSTOMER"))
            showProfilePicture();

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(3, 3)
                .setMaxCropResultSize(4096, 4096)
                .start(this);
        showPictureDialog();


    }

    private void cropRequest() {
        CropImage.activity()
                .setCropShape(CropImageView.CropShape.OVAL)
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
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                cropRequest();
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
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
        userUploadImage.setImageURI(imageUri);
        userUploadImage.setVisibility(View.VISIBLE);
        uploadImage.setEnabled(true);


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


    private void showPwChangeEditForm() {
        Dialog pwChangeDialog = new Dialog(this);
        pwChangeDialog.setContentView(R.layout.change_password_password_input_form);

        EditText etOldPw = pwChangeDialog.findViewById(R.id.et_change_pw_old_pw);
        EditText etNewPw = pwChangeDialog.findViewById(R.id.et_change_pw_new_pw);
        EditText etConfirmNewPw = pwChangeDialog.findViewById(R.id.et_change_pw_confirm_new_pw);
        Button btnChangePw = pwChangeDialog.findViewById(R.id.btn_change_pw);
        ImageView ivCancel = pwChangeDialog.findViewById(R.id.cancel_image);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pwChangeDialog.dismiss();
            }
        });

        btnChangePw.setOnClickListener(view -> {
            if (DataValidityChecker.isEditTextDataValid(etOldPw, "Old Password", true)) {
                if (DataValidityChecker.isEditTextDataValid(etNewPw, "New Password", true)) {
                    if (DataValidityChecker.isEditTextDataValid(etConfirmNewPw, "Confirm New Password", true)) {
                        if (etNewPw.getText().toString().trim().equals(etConfirmNewPw.getText().toString().trim())) {
                            ChangePasswordDto changePasswordDto = new ChangePasswordDto(loginInstance.getUserName(),
                                    etOldPw.getText().toString().trim(), etNewPw.getText().toString().trim()
                                    , etConfirmNewPw.getText().toString().trim());

                            pwChangeDialog.dismiss();


                            sendPasswordToSrver(changePasswordDto);
                        } else {
                            etConfirmNewPw.setError("Password not matched!!!");
                            etConfirmNewPw.requestFocus();
                        }
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

    private void sendPasswordToSrver(ChangePasswordDto changePasswordDto) {

        Call<ResponseBody> call = apiInterface.changePassword(loginInstance.getLoginId(), loginInstance.getCustomerId(),
                loginInstance.getToken(), loginInstance.getUserId().toString(), changePasswordDto);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {

                    if (response.code() == 203) {
                        Toast.makeText(CurrentUserProfileActivity.this, "invalid old password", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CurrentUserProfileActivity.this, "password successfully changed ", Toast.LENGTH_SHORT).show();
                    }
//                    CustomSnackbar.make(rootRelativeLayout,"Password Successfully Changed",CustomSnackbar.LENGTH_SHORT).show();

                } else {


                    //Maps the error message in ErrorMessageDto
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = null;
                    try {
                        mJson = parser.parse(response.errorBody().string());
                        Gson gson = new Gson();
                        ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                        Toast.makeText(CurrentUserProfileActivity.this, errorMessageDto.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                CustomSnackbar.checkErrorResponse(rootRelativeLayout, CurrentUserProfileActivity.this);
            }
        });
    }

    private void ClearBackStack() {
        final FragmentManager fm = getSupportFragmentManager();
        while (fm.getBackStackEntryCount() > 0) {
            fm.popBackStackImmediate();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.changePw:
                showPwChangeEditForm();
                break;
            case R.id.changeImage:
                selectImage();
                break;
            default:
                break;


        }
        return false;
    }
}
