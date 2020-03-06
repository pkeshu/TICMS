package com.imaginology.texas.Teachers;

import android.app.Dialog;
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
import com.imaginology.texas.AddressFragment;
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


public class TeacherDetails extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private TextView tUsername, tRole, tActiveTxt, backTxt, tSettingImage;
    private ImageView tImage, tActiveImage;
    ApiInterface apiInterface;
    private UserLoginResponseEntity loginInstance;
    private String teacherProfilePicture;
    private BottomNavigationView nvHome;
    private ViewPager pager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private FrameLayout tvFramelayout;
    //middleFramelayout;
    private int userId;
    private String username;
    private int profileId;
    private ConstraintLayout mainLayout;
    private Long teacherLoginId;
    private Dialog showImageuploadDialog;
    private ImageView userUploadImage;
    private Button uploadImage;
    private TextView dToolbar;
    private ProgressBar uploadProgressing;
    private Uri imageUri = null;
    private File croppedImageUri;
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //Getting loginInstance from database
        GetLoginInstanceFromDatabase loginInstanceAccessor = new GetLoginInstanceFromDatabase(this);
        loginInstance = loginInstanceAccessor.getLoginInstance();
        apiInterface = ApiClient.getRetrofit(this).create(ApiInterface.class);
        tUsername = findViewById(R.id.user_username);
        tRole = findViewById(R.id.user_role);
        tActiveImage = findViewById(R.id.user_status_image);
        tSettingImage = findViewById(R.id.setting_image);
        tActiveTxt = findViewById(R.id.active_status_txt);
        nvHome = findViewById(R.id.bottomnavigation);
        backTxt = findViewById(R.id.back_txt);
        mainLayout = findViewById(R.id.rootView);
        //for pageViewer
        pager = findViewById(R.id.pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(viewPagerAdapter);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);
        tabLayout.removeTabAt(2);
        tImage = findViewById(R.id.user_image_view);
        Typeface robotoRegularFont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        tUsername.setTypeface(robotoRegularFont);
        tActiveTxt.setVisibility(View.GONE);
        tSettingImage.setVisibility(View.INVISIBLE);
        tActiveImage.setVisibility(View.GONE);
        tRole.setTypeface(robotoRegularFont);
        tvFramelayout = findViewById(R.id.frameLayout);
       // middleFramelayout = findViewById(R.id.fragment_holder_basic_info);

        //for Teacher details
        //Get teacher id from intent
        Intent intent = getIntent();
        String teacherId = intent.getStringExtra("selectedTeacherId");
        teacherLoginId=intent.getExtras().getLong("selectedTeacherLoginId");
        getTeacherDetails(teacherId);
        profileId = Integer.valueOf(teacherId);

        backTxt.setOnClickListener(this);
        nvHome.setVisibility(View.GONE);
 /*               setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                startActivity(new Intent(TeacherDetails.this,MainActivity.class));
                MainFragment fragment = new MainFragment();
                SupportActionBarInitializer.setSupportActionBarTitle(getSupportActionBar(), "Home");
                FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.home_contener,fragment);
                fragmentTransaction.commit();
                nvHome.setVisibility(View.GONE);
                tvFramelayout.setVisibility(View.GONE);
                middleFramelayout.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);
                tImage.setVisibility(View.GONE);
                tActiveImage.setVisibility(View.GONE);
                tActiveTxt.setVisibility(View.GONE);
//                        startActivity(new Intent(this,MainActivity.class));
                ClearBackStack();
                return true;
            }
        });*/

        tImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog showUserProfilePicture = new Dialog(TeacherDetails.this);
                showUserProfilePicture.setContentView(R.layout.show_user_image_form);
                ImageView teacherImage = showUserProfilePicture.findViewById(R.id.userImage);
                ImageViewLoader.loadImage(TeacherDetails.this, teacherProfilePicture, teacherImage);
                if (showUserProfilePicture.getWindow() != null) {
                    showUserProfilePicture.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    showUserProfilePicture.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

                }
                showUserProfilePicture.show();
            }
        });

        if (loginInstance.getUserRole().equals("ADMIN")) {
            tSettingImage.setVisibility(View.VISIBLE);
            tSettingImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PopupMenu popupMenu = new PopupMenu(TeacherDetails.this, tSettingImage);
                    popupMenu.inflate(R.menu.setting_menu);
                    popupMenu.setOnMenuItemClickListener(TeacherDetails.this);
                    if (loginInstance.getUserRole().equals("CUSTOMER") || loginInstance.getUserRole().equals("TEACHER") || loginInstance.getUserRole().equals("STUDENT"))
                        popupMenu.getMenu().findItem(R.id.changeImage).setVisible(false);
                    popupMenu.show();
                }
            });
        }

    }

    private void getTeacherDetails(String teacherId) {
        Call<TeacherDto> call = apiInterface.teacherInfo(loginInstance.getLoginId(), loginInstance.getCustomerId(), Long.valueOf(teacherId), loginInstance.getToken());
        call.enqueue(new Callback<TeacherDto>() {
            @Override
            public void onResponse(Call<TeacherDto> call, Response<TeacherDto> response) {
                if (response.isSuccessful()) {
                    TeacherDto teacherDto = response.body();
                    tUsername.setSelected(true);
                    tUsername.setText("@" + CheckError.checkNullString(teacherDto.getUserName()));
                    tRole.setText(CheckError.checkNullString("TEACHER"));
                    teacherProfilePicture = CheckError.checkNullString(teacherDto.getProfilePicture());
                    ImageViewLoader.loadImage(TeacherDetails.this, teacherProfilePicture, tImage);


                    Bundle bundle = new Bundle();
                    String fullName, email, gender, phone;
                    String middlename = teacherDto.getMiddleName();
                    userId = teacherDto.getId();
                    username = teacherDto.getUserName();
                    List<TeamEntity> teamEntities = new ArrayList<>();
                    List<TeamDto.Content> contentList = new ArrayList<>();
                    if (middlename.length() == 0) {
                        fullName = CheckError.checkNullString(teacherDto.getFirstName()) + " " + CheckError.checkNullString(teacherDto.getLastName());

                    } else {
                        fullName = CheckError.checkNullString(teacherDto.getFirstName()) + " " + teacherDto.getMiddleName() + " " + CheckError.checkNullString(teacherDto.getLastName());

                    }
                    email = CheckError.checkNullString(teacherDto.getEmail());
                    gender = CheckError.checkNullString(teacherDto.getGender());
                    phone = CheckError.checkNullString(teacherDto.getPhoneNumber());

                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    BasicInformationFragment basicInformationFragment = new BasicInformationFragment();
                    bundle.putParcelableArrayList("userTeamList", (ArrayList<? extends Parcelable>) teamEntities);
                    bundle.putParcelableArrayList("teamList", (ArrayList<? extends Parcelable>) contentList);
                    bundle.putString("fullname", fullName);
                    bundle.putString("email", email);
                    bundle.putString("phone", phone);
                    bundle.putString("gender", gender);
                    basicInformationFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_holder_basic_info, basicInformationFragment);
                    fragmentTransaction.commit();

                    //for page change
                    ViewPager.OnPageChangeListener onPageChangeListener;
                    onPageChangeListener = new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            if (position == 0) {
                                Bundle bundle = new Bundle();
                                String fullName, email, gender, phone;
                                String middlename = teacherDto.getMiddleName();

                                List<TeamEntity> teamEntities = new ArrayList<>();
                                List<TeamDto.Content> contentList = new ArrayList<>();
                                if (middlename.length() == 0) {
                                    fullName = CheckError.checkNullString(teacherDto.getFirstName()) + " " + CheckError.checkNullString(teacherDto.getLastName());

                                } else {
                                    fullName = CheckError.checkNullString(teacherDto.getFirstName()) + " " + teacherDto.getMiddleName() + " " + CheckError.checkNullString(teacherDto.getLastName());

                                }
                                email = CheckError.checkNullString(teacherDto.getEmail());
                                gender = CheckError.checkNullString(teacherDto.getGender());
                                phone = CheckError.checkNullString(teacherDto.getPhoneNumber());

                                FragmentManager fm = getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                                BasicInformationFragment basicInformationFragment = new BasicInformationFragment();
                                bundle.putParcelableArrayList("userTeamList", (ArrayList<? extends Parcelable>) teamEntities);
                                bundle.putParcelableArrayList("teamList", (ArrayList<? extends Parcelable>) contentList);
                                bundle.putString("fullname", fullName);
                                bundle.putString("email", email);
                                bundle.putString("phone", phone);
                                bundle.putString("gender", gender);
                                basicInformationFragment.setArguments(bundle);
                                fragmentTransaction.replace(R.id.fragment_holder_basic_info, basicInformationFragment);
                                fragmentTransaction.commit();
                            }
                            if (position == 1) {
                                List<TeacherDto.Address> addresses = teacherDto.getAddresses();
                                String district, zone, vdc, type;
                                Bundle b = new Bundle();
                                for (TeacherDto.Address address : addresses) {
                                    district = CheckError.checkNullString(address.getDistrict());
                                    zone = CheckError.checkNullString(address.getZone());
                                    vdc = CheckError.checkNullString(address.getVdc());
                                    type = CheckError.checkNullString(address.getType());
                                    b.putString("district", district);
                                    b.putString("zone", zone);
                                    b.putString("vdc", vdc);
                                    b.putString("type", type);
                                }
                                AddressFragment addressFragment = new AddressFragment();
                                addressFragment.setArguments(b);
                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.fragment_holder_basic_info, addressFragment);
                                ft.commit();

                            }
//
                        }

                        @Override
                        public void onPageSelected(int position) {

                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    };
                    pager.addOnPageChangeListener(onPageChangeListener);


                }

            }

            @Override
            public void onFailure(Call<TeacherDto> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.no_response, Toast.LENGTH_SHORT).show();


            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.equals(backTxt))
            getBack();
    }

    private void getBack() {
        onBackPressed();
        return;
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
                AdminAuthorizarion adminAuthorizarion = new AdminAuthorizarion();
                adminAuthorizarion.fetchingUserName(TeacherDetails.this, String.valueOf(userId), username);
                break;
            case R.id.changeImage:
                changeProfileImage(profileId);
                break;
            case R.id.privateMessage:
                SecreteMessage.sendSecreteMessage(getApplicationContext(), teacherLoginId);
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
            CustomSnackbar.showFailureSnakeBar(mainLayout, "You can't change Profile Picture", TeacherDetails.this);

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
        ImageViewLoader.loadImage(this, teacherProfilePicture, userImage);
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
        ImageViewLoader.loadImage(this, teacherProfilePicture, userUploadImage);
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
            Call<ProfilePicEditResponseDTO> call = apiInterface.changeTeacherProfilePicture(loginInstance.getCustomerId(), loginInstance.getLoginId(), (long) profileId,
                    new ProfilePicEditRequest(encoddedString), loginInstance.getToken());
            uploadProgressing.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<ProfilePicEditResponseDTO>() {
                @Override
                public void onResponse(Call<ProfilePicEditResponseDTO> call, Response<ProfilePicEditResponseDTO> response) {
                    if (response.isSuccessful()) {
                        ProfilePicEditResponseDTO profilePicEditResponseDTO = response.body();
                        String pPicUri = profilePicEditResponseDTO.getUrl();
                        ImageViewLoader.loadImage(TeacherDetails.this, pPicUri, tImage);
                        uploadProgressing.setVisibility(View.GONE);
                        showImageuploadDialog.dismiss();
                        CustomSnackbar.showSuccessSnakeBar(mainLayout, "Image Changed Successfully", TeacherDetails.this);
                    } else {
                        //Maps the error message in ErrorMessageDto
                        JsonParser parser = new JsonParser();
                        JsonElement mJson = null;
                        try {
                            mJson = parser.parse(response.errorBody().string());
                            Gson gson = new Gson();
                            ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                            CustomSnackbar.showFailureSnakeBar(mainLayout, errorMessageDto.getMessage(), TeacherDetails.this);
                            showImageuploadDialog.dismiss();
                            uploadProgressing.setVisibility(View.GONE);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ProfilePicEditResponseDTO> call, Throwable t) {
                    CustomSnackbar.checkErrorResponse(mainLayout, TeacherDetails.this);
                    showImageuploadDialog.dismiss();
                    uploadProgressing.setVisibility(View.GONE);

                }
            });
        } else {
            showImageuploadDialog.dismiss();
            CustomSnackbar.showFailureSnakeBar(mainLayout, "Select Image first", TeacherDetails.this);

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
