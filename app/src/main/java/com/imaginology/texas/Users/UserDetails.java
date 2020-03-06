package com.imaginology.texas.Users;

import android.app.Dialog;
import android.app.Notification;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imaginology.texas.AdminAuthorizarion;
import com.imaginology.texas.BasicInformationFragment;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.Notification.PrivateMessage.SecreteMessage;
import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.RoomDatabase.UserTeamResponse.TeamEntity;
import com.imaginology.texas.SnackBar.CustomSnackbar;
import com.imaginology.texas.Team.TeamDto;
import com.imaginology.texas.ViewPagerAdapter;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.CheckError;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;
import com.imaginology.texas.util.ImageViewLoader;
import com.imaginology.texas.util.ProfilePicEditRequest;
import com.imaginology.texas.util.ProfilePicEditResponseDTO;
import com.imaginology.texas.util.StatusChecker;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetails extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private TextView userFullName, userEmail, userUsername, userRole, userGender,
            userPhone, showUsername, userChangePw, userActiveTxt, backTxt, userSettingImage;
    private ImageView userImage, userActiveImage;
    private ApiInterface apiInterface;
    private BottomNavigationView nvHome;

    public static final String STUDENT = "users";
    UserListDto.Datum userListDto;
    private final String TAG = UserDetails.class.getSimpleName();
    private GetLoginInstanceFromDatabase loginInstanceAccessor;
    private UserLoginResponseEntity loginInstance;
    String picUrl;
    private ViewPager pager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private List<TeamDto.Content> teamList = new ArrayList<>();
    private FrameLayout tvFramelayout, middleFramelayout;
    private int userId;
    private String username;
    private ConstraintLayout mainLayout;
    private Long userLoginId,NotificationId;
    private Dialog showImageuploadDialog;
    private ImageView userUploadImage;
    private Button uploadImage;
    private TextView dToolbar;
    private ProgressBar uploadProgressing;
    private Uri imageUri = null;
    private File croppedImageUri;
    private Bitmap bitmap;
    private int profileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        loginInstanceAccessor = new GetLoginInstanceFromDatabase(this);
        loginInstance = loginInstanceAccessor.getLoginInstance();
        apiInterface = ApiClient.getRetrofit(this).create(ApiInterface.class);


        userUsername = findViewById(R.id.user_username);
        mainLayout = findViewById(R.id.rootView);
        userRole = findViewById(R.id.user_role);
        userGender = findViewById(R.id.user_gender);
        userActiveImage = findViewById(R.id.user_status_image);
        userActiveTxt = findViewById(R.id.active_status_txt);
        userSettingImage = findViewById(R.id.setting_image);
        tvFramelayout = findViewById(R.id.frameLayout);
        middleFramelayout = findViewById(R.id.fragment_holder_basic_info);


        userImage = findViewById(R.id.user_image_view);
        Typeface robotoRegularFont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
//        userFullName.setTypeface(robotoRegularFont);
//        userEmail.setTypeface(robotoRegularFont);
        userUsername.setTypeface(robotoRegularFont);
        userActiveTxt.setVisibility(View.GONE);
        userActiveImage.setVisibility(View.GONE);
        userRole.setTypeface(robotoRegularFont);
        userSettingImage.setVisibility(View.INVISIBLE);
        Bundle bundle = getIntent().getExtras();
        //Getting loginInstance from database

        //get Data from intent
        Intent getData = getIntent();
        profileId = getData.getExtras().getInt("userId");
        userLoginId = getData.getExtras().getLong("profileId");
        //for pageViewer
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

        userImage = findViewById(R.id.user_image_view);
//        Typeface robotoRegularFont=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
        userUsername.setTypeface(robotoRegularFont);
        userActiveTxt.setVisibility(View.GONE);
        userActiveImage.setVisibility(View.GONE);
        userRole.setTypeface(robotoRegularFont);
//        Bundle bundle = getIntent().getExtras();
        backTxt = findViewById(R.id.back_txt);
        nvHome = findViewById(R.id.bottomnavigation);
        backTxt.setOnClickListener(this);
        nvHome.setVisibility(View.GONE);
          /*      setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                startActivity(new Intent(UserDetails.this,MainActivity.class));
                MainFragment fragment = new MainFragment();
                SupportActionBarInitializer.setSupportActionBarTitle(getSupportActionBar(), "Home");
                FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.home_contener,fragment);
                fragmentTransaction.commit();
                nvHome.setVisibility(View.GONE);
                tvFramelayout.setVisibility(View.GONE);
                middleFramelayout.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);
                userImage.setVisibility(View.GONE);
                userActiveTxt.setVisibility(View.GONE);
                userActiveImage.setVisibility(View.GONE);
//                        startActivity(new Intent(this,MainActivity.class));
                ClearBackStack();
                return true;
            }
        });*/


        if (bundle != null) {
            userId = bundle.getInt("userId");
            Call<UserDto> call = apiInterface.userInfo(loginInstance.getLoginId(), Long.valueOf(userId),
                    loginInstance.getCustomerId(), loginInstance.getToken());
            call.enqueue(new Callback<UserDto>() {
                @Override
                public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                    int status = response.code();
                    if (response.isSuccessful()) {
                        UserDto userDetails = response.body();
                        UserDto.User user = userDetails.getUser();
                        String middleName = user.getMiddleName();
                        teamList.addAll(user.getTeamList());
                        userRole.setText(CheckError.checkNullString(user.getUserRole()));
                        userUsername.setSelected(true);
                        userUsername.setText("@" + CheckError.checkNullString(String.valueOf(user.getUserName())));
                        picUrl = CheckError.checkNullString(user.getProfilePicture());
                        ImageViewLoader.loadImage(UserDetails.this, picUrl, userImage);
                        username = user.getUserName();
                        //for basic information tab
                        Bundle bundle1 = new Bundle();
                        String fullName;
                        if (middleName.length() == 0) {
                            fullName = CheckError.checkNullString(user.getFirstName()) + " " + CheckError.checkNullString(user.getLastName());

                        } else {
                            fullName = CheckError.checkNullString(user.getFirstName()) + " " + user.getMiddleName() + " " + CheckError.checkNullString(user.getLastName());

                        }
                        String email = user.getEmail();
                        String phone = user.getPhone();
                        String gender = CheckError.checkNullString(user.getGender());
                        List<TeamEntity> teamEntities = new ArrayList<>();

                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        BasicInformationFragment basicInformationFragment = new BasicInformationFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("userTeamList", (ArrayList<? extends Parcelable>) teamList);
                        bundle.putParcelableArrayList("teamList", (ArrayList<? extends Parcelable>) teamEntities);
                        bundle.putString("fullname", fullName);
                        bundle.putString("email", email);
                        bundle.putString("phone", phone);
                        bundle.putString("gender", gender);
                        basicInformationFragment.setArguments(bundle);
                        fragmentTransaction.replace(R.id.fragment_holder_basic_info, basicInformationFragment);
                        fragmentTransaction.commit();


                    } else if (status == 401) {
                        StatusChecker.statusCheck(UserDetails.this);

                    } else {
                        //Maps the error message in ErrorMessageDto
                        JsonParser parser = new JsonParser();
                        JsonElement mJson = null;
                        try {
                            mJson = parser.parse(response.errorBody().string());
                            Gson gson = new Gson();
                            ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                            Toast.makeText(UserDetails.this, errorMessageDto.getMessage(), Toast.LENGTH_SHORT).show();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                }

                @Override
                public void onFailure(Call<UserDto> call, Throwable t) {
                    Toast.makeText(UserDetails.this, R.string.no_response, Toast.LENGTH_SHORT).show();
                }
            });
        }

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog showUserProfilePicture = new Dialog(UserDetails.this);
                showUserProfilePicture.setContentView(R.layout.show_user_image_form);
                ImageView userImage = showUserProfilePicture.findViewById(R.id.userImage);
                ImageViewLoader.loadImage(UserDetails.this, picUrl, userImage);
                if (showUserProfilePicture.getWindow() != null) {
                    showUserProfilePicture.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    showUserProfilePicture.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

                }
                showUserProfilePicture.show();
            }
        });

        if (loginInstance.getUserRole().equals("ADMIN")) {
            userSettingImage.setVisibility(View.VISIBLE);
            userSettingImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PopupMenu popupMenu = new PopupMenu(UserDetails.this, userSettingImage);
                    popupMenu.inflate(R.menu.setting_menu);
                    popupMenu.setOnMenuItemClickListener(UserDetails.this);
                    if (loginInstance.getUserRole().equals("CUSTOMER") || loginInstance.getUserRole().equals("TEACHER") || loginInstance.getUserRole().equals("STUDENT"))
                        popupMenu.getMenu().findItem(R.id.changeImage).setVisible(false);
                    popupMenu.show();
                }
            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View view) {
        if (view.equals(backTxt))
            goBack();

    }

    private void goBack() {
        onBackPressed();
        return;

    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.changePw:
                AdminAuthorizarion adminAuthorizarion = new AdminAuthorizarion();
                adminAuthorizarion.fetchingUserName(UserDetails.this, String.valueOf(userId), username);
                break;
            case R.id.changeImage:
                changeProfileImage(profileId);
                break;
            case R.id.privateMessage:
                //added
                SecreteMessage.sendSecreteMessage(getApplicationContext(), userLoginId);
                break;
            default:
                break;


        }
        return false;
    }

    private void changeProfileImage(int profileId) {
        if (loginInstance.getUserRole().equalsIgnoreCase("ADMIN"))
            selectImage(profileId);

        else
            CustomSnackbar.showFailureSnakeBar(mainLayout, "You can't change Profile Picture", UserDetails.this);

    }

    private void selectImage(int profileId) {
        if (loginInstance.getUserRole().equals("CUSTOMER"))
            showProfilePicture();

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(3, 3)
                .setMaxCropResultSize(4096, 4096)
                .start(this);
        showPictureDialog(profileId);


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

    private void showPictureDialog(int profileId) {
        showImageuploadDialog = new Dialog(this);
        showImageuploadDialog.setContentView(R.layout.image_upload_dialog);
        userUploadImage = showImageuploadDialog.findViewById(R.id.chooseImage);
        uploadImage = showImageuploadDialog.findViewById(R.id.btn_upload);
        dToolbar = showImageuploadDialog.findViewById(R.id.back_btn);
        uploadProgressing = showImageuploadDialog.findViewById(R.id.upload_progress);
        ImageViewLoader.loadImage(this, picUrl, userUploadImage);
        TextView backText = showImageuploadDialog.findViewById(R.id.back_btn);
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageuploadDialog.dismiss();
            }
        });
        userUploadImage.setVisibility(View.VISIBLE);
        uploadProgressing.setVisibility(View.GONE);
        uploadImage.setVisibility(View.GONE);
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
                uploadImageToServer(profileId);
            }
        });
    }

    private void uploadImageToServer(int profileId) {
        if (imageUri != null) {
            String encoddedString = convertToBase64(imageUri);
            Log.d("ecncodedString:", encoddedString);
            Call<ProfilePicEditResponseDTO> call = apiInterface.uplodProfilePic((long) profileId,
                    loginInstance.getLoginId(), loginInstance.getCustomerId(), new ProfilePicEditRequest(encoddedString), loginInstance.getToken());
            uploadProgressing.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<ProfilePicEditResponseDTO>() {
                @Override
                public void onResponse(Call<ProfilePicEditResponseDTO> call, Response<ProfilePicEditResponseDTO> response) {
                    if (response.isSuccessful()) {
                        ProfilePicEditResponseDTO profilePicEditResponseDTO = response.body();
                        String pPicUri = profilePicEditResponseDTO.getUrl();
                        ImageViewLoader.loadImage(UserDetails.this, pPicUri, userImage);
                        uploadProgressing.setVisibility(View.GONE);
                        showImageuploadDialog.dismiss();
                        Intent intent = new Intent(UserDetails.this, UserDetails.class);
                        intent.putExtra("userId", profileId);
                        startActivity(intent);
                        finish();
                        CustomSnackbar.showSuccessSnakeBar(mainLayout, "Image Changed Successfully", UserDetails.this);
                    } else {
                        //Maps the error message in ErrorMessageDto
                        JsonParser parser = new JsonParser();
                        JsonElement mJson = null;
                        try {
                            mJson = parser.parse(response.errorBody().string());
                            Gson gson = new Gson();
                            ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                            CustomSnackbar.showFailureSnakeBar(mainLayout, errorMessageDto.getMessage(), UserDetails.this);
                            showImageuploadDialog.dismiss();
                            uploadProgressing.setVisibility(View.GONE);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ProfilePicEditResponseDTO> call, Throwable t) {
                    CustomSnackbar.checkErrorResponse(userImage, UserDetails.this);
                    showImageuploadDialog.dismiss();
                    uploadProgressing.setVisibility(View.GONE);

                }
            });
        } else {
            showImageuploadDialog.dismiss();
            CustomSnackbar.showFailureSnakeBar(mainLayout, "Select Image first", UserDetails.this);

//
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
}
