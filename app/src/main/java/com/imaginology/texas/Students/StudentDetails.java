package com.imaginology.texas.Students;

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
import android.widget.RelativeLayout;
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
import com.imaginology.texas.ParentFragment;
import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.RoomDatabase.UserTeamResponse.TeamEntity;
import com.imaginology.texas.SnackBar.CustomSnackbar;
import com.imaginology.texas.Team.TeamDto;
import com.imaginology.texas.Users.UserDetails;
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

public class StudentDetails extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    ProgressBar progressBar;
    RelativeLayout relativeLayout;
    private TextView stuFullName, stuEmail, stuUsername, stuRole,
            stuPhone, stuGender, showUsername, stuChangePw, stuActiveTxt, backTxt, stuAddress, stuSettingImage;
    private ImageView stuImage, stuActiveImage;
    private final String TAG = UserDetails.class.getSimpleName();
    ApiInterface apiInterface;
    private UserLoginResponseEntity loginInstance;
    String studentProfilePicture;
    private BottomNavigationView nvHome;
    private ViewPager pager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private FrameLayout tvFramelayout, middleFramelayout;
    private String userId;
    private Integer IdImage;
    private String rollno;
    private ConstraintLayout mainLayout;
    private Dialog showImageuploadDialog;
    private ImageView userUploadImage;
    private Button uploadImage;
    private TextView dToolbar;
    private ProgressBar uploadProgressing;
    private Uri imageUri = null;
    private File croppedImageUri;
    private Bitmap bitmap;
    private Long studentLoginId;
    private String middlename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //Getting loginInstance from database
        GetLoginInstanceFromDatabase loginInstanceAccessor = new GetLoginInstanceFromDatabase(StudentDetails.this);
        loginInstance = loginInstanceAccessor.getLoginInstance();
        apiInterface = ApiClient.getRetrofit(this).create(ApiInterface.class);

        progressBar = findViewById(R.id.progress_bar);
        stuFullName = findViewById(R.id.user_full_name);
        stuEmail = findViewById(R.id.user_email);
        stuUsername = findViewById(R.id.user_username);
        mainLayout = findViewById(R.id.rootView);
        stuPhone = findViewById(R.id.user_phone);
        stuGender = findViewById(R.id.user_gender);
        stuRole = findViewById(R.id.user_role);
        stuActiveImage = findViewById(R.id.user_status_image);
        stuActiveTxt = findViewById(R.id.active_status_txt);
        nvHome = findViewById(R.id.bottomnavigation);
        backTxt = findViewById(R.id.back_txt);
        stuAddress = findViewById(R.id.user_address);
        stuSettingImage = findViewById(R.id.setting_image);
        tvFramelayout = findViewById(R.id.frameLayout);
        middleFramelayout = findViewById(R.id.fragment_holder_basic_info);

        //for pageViewer
        pager = findViewById(R.id.pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(viewPagerAdapter);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);

        stuImage = findViewById(R.id.user_image_view);
        Typeface robotoRegularFont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        stuFullName.setTypeface(robotoRegularFont);
        stuEmail.setTypeface(robotoRegularFont);
        stuUsername.setTypeface(robotoRegularFont);
        stuActiveTxt.setVisibility(View.GONE);
        stuActiveImage.setVisibility(View.GONE);
        stuSettingImage.setVisibility(View.INVISIBLE);
        stuRole.setTypeface(robotoRegularFont);

        //Get user id from intent
        Intent intent = getIntent();
        userId = intent.getStringExtra("selectedUserId");
        studentLoginId=intent.getExtras().getLong("studentLoginId");
        Log.d("StudentId=====",studentLoginId.toString());
        getUserDetails(Long.valueOf(userId));

        backTxt.setOnClickListener(this);
        nvHome.setVisibility(View.GONE);
          /*      setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                startActivity(new Intent(StudentDetails.this,MainActivity.class));
                MainFragment fragment = new MainFragment();
                SupportActionBarInitializer.setSupportActionBarTitle(getSupportActionBar(), "Home");
                FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.home_contener,fragment);
                fragmentTransaction.commit();
                nvHome.setVisibility(View.GONE);
                tvFramelayout.setVisibility(View.GONE);
                middleFramelayout.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);
                stuImage.setVisibility(View.GONE);
                stuActiveImage.setVisibility(View.GONE);
                stuActiveTxt.setVisibility(View.GONE);
//                        startActivity(new Intent(this,MainActivity.class));
                ClearBackStack();
                return true;
            }
        });*/

        if (loginInstance.getUserRole().equals("ADMIN")) {
            stuSettingImage.setVisibility(View.VISIBLE);
            stuSettingImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PopupMenu popupMenu = new PopupMenu(StudentDetails.this, stuSettingImage);
                    popupMenu.inflate(R.menu.setting_menu);
                    popupMenu.setOnMenuItemClickListener(StudentDetails.this);
                    if (loginInstance.getUserRole().equals("CUSTOMER") || loginInstance.getUserRole().equals("TEACHER") || loginInstance.getUserRole().equals("STUDENT"))
                        popupMenu.getMenu().findItem(R.id.changeImage).setVisible(false);
                    popupMenu.show();
                }
            });
        }

        stuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog showUserProfilePicture = new Dialog(StudentDetails.this);
                showUserProfilePicture.setContentView(R.layout.show_user_image_form);
                ImageView userImage = showUserProfilePicture.findViewById(R.id.userImage);

                ImageViewLoader.loadImage(StudentDetails.this, studentProfilePicture, userImage);
                if (showUserProfilePicture.getWindow() != null) {
                    showUserProfilePicture.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    showUserProfilePicture.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

                }
                showUserProfilePicture.show();
            }
        });
    }

    private void getUserDetails(Long userId) {

        //SharedPreferences sharedPref = getSharedPreferences("loginDetails", Context.MODE_PRIVATE);

        Call<StudentDto> call = apiInterface.studentInfo(loginInstance.getCustomerId(), userId,
                loginInstance.getToken(), loginInstance.getLoginId());
        call.enqueue(new Callback<StudentDto>() {
            @Override
            public void onResponse(Call<StudentDto> call, Response<StudentDto> response) {

                int status = response.code();

                if (response.isSuccessful()) {
                    StudentDto studentDto = response.body();

                    middlename = studentDto.getStudent().getMiddleName();
                    rollno = studentDto.getStudent().getRollNumber();
                    IdImage = studentDto.getStudent().getId();
                    //for basic information tab

                    Bundle bundle1 = new Bundle();
                    String fullName;
                    if (middlename.length() == 0) {
                        fullName = CheckError.checkNullString(studentDto.getStudent().getFirstName()) + " " + CheckError.checkNullString(studentDto.getStudent().getLastName());

                    } else {
                        fullName = CheckError.checkNullString(studentDto.getStudent().getFirstName()) + " " + studentDto.getStudent().getMiddleName() + " " + CheckError.checkNullString(studentDto.getStudent().getLastName());

                    }
                    String email = studentDto.getStudent().getEmail();
                    String phone = studentDto.getStudent().getPhoneNumber();
                    String gender = CheckError.checkNullString(studentDto.getStudent().getGender());
                    List<TeamEntity> teamEntities = new ArrayList<>();
                    List<TeamDto.Content> teamList = new ArrayList<>();
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

                    //for page change
               //     ViewPager.OnPageChangeListener onPageChangeListener;


                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {


                            if (tab.getPosition() == 0) {
                                positionZero(studentDto);
                            }
                            if (tab.getPosition() == 1) {
                                positionOne(studentDto);

                            }
                            if (tab.getPosition() == 2) {
                                //for parent details
                                PosotionTwo(studentDto);
                            }
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {

                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {

                        }
                    });
                    /*onPageChangeListener = new ViewPager.OnPageChangeListener() {


                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


                            if (position == 0) {
                                positionZero(studentDto);
                            }
                            if (position == 1) {
                                positionOne(studentDto);

                            }
                            if (position == 2) {
                                //for parent details
                               PosotionTwo(studentDto);
                            }
                        }

                        @Override
                        public void onPageSelected(int position) {

                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    };*/
                    pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

                    stuUsername.setSelected(true);
                    stuUsername.setText(CheckError.checkNullString(studentDto.getStudent().getRollNumber()));
                    stuRole.setText(CheckError.checkNullString("STUDENT"));
                    studentProfilePicture = CheckError.checkNullString(studentDto.getStudent().getProfilePicture());
                    ImageViewLoader.loadImage(StudentDetails.this, studentProfilePicture, stuImage);
                    Log.d("AddressSize", String.valueOf(studentDto.getStudent().getAddresses().get(0).getDistrict()));


                } else if (status == 401) {
                    StatusChecker.statusCheck(StudentDetails.this);

                } else {
//                    progressBar.setVisibility(View.GONE);
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

            public void PosotionTwo(StudentDto studentDto){
                List<StudentDto.Parent> parents = studentDto.getStudent().getParents();
                String pName, pEmail, pPhone, pRelation;
                Bundle arg = new Bundle();
                for (StudentDto.Parent parent : parents) {
                    pName = CheckError.checkNullString(parent.getFullName());
                    pEmail = CheckError.checkNullString(parent.getEmail());
                    pPhone = CheckError.checkNullString(parent.getMobileNumber());
                    pRelation = CheckError.checkNullString(parent.getRelation());
                    arg.putString("pName", pName);
                    arg.putString("pEmail", pEmail);
                    arg.putString("pPhone", pPhone);
                    arg.putString("pRelation", pRelation);

                }
                ParentFragment parentFragment = new ParentFragment();
                parentFragment.setArguments(arg);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_holder_basic_info, parentFragment);
                ft.commit();
            }


            public void positionOne(StudentDto studentDto){
                List<StudentDto.Address> addresses = studentDto.getStudent().getAddresses();
                String district, zone, vdc, type;
                Bundle b = new Bundle();
                for (StudentDto.Address address : addresses) {
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


            public void positionZero(StudentDto studentDto){
                Bundle bundle1 = new Bundle();
                String fullName;
                if (middlename.length() == 0) {
                    fullName = CheckError.checkNullString(studentDto.getStudent().getFirstName()) + " " + CheckError.checkNullString(studentDto.getStudent().getLastName());

                } else {
                    fullName = CheckError.checkNullString(studentDto.getStudent().getFirstName()) + " " + studentDto.getStudent().getMiddleName() + " " + CheckError.checkNullString(studentDto.getStudent().getLastName());

                }
                String email = studentDto.getStudent().getEmail();
                String phone = studentDto.getStudent().getPhoneNumber();
                String gender = CheckError.checkNullString(studentDto.getStudent().getGender());
                List<TeamEntity> teamEntities = new ArrayList<>();
                List<TeamDto.Content> teamList = new ArrayList<>();
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
            }


            @Override
            public void onFailure(Call<StudentDto> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.no_response, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    public void onClick(View view) {
        if (view.equals(backTxt))
            goBack();

    }

    private void goBack() {
//        startActivity(new Intent(StudentDetails.this,MainActivity.class));
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
                adminAuthorizarion.fetchingUserName(StudentDetails.this, userId, rollno);
                break;
            case R.id.changeImage:
                changeProfileImage(Integer.valueOf(userId));
                break;
            case R.id.privateMessage:
                SecreteMessage.sendSecreteMessage(getApplicationContext(), studentLoginId);
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
            CustomSnackbar.showFailureSnakeBar(mainLayout, "You can't change Profile Picture", StudentDetails.this);

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
        ImageViewLoader.loadImage(this, studentProfilePicture, userImage);
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
        ImageViewLoader.loadImage(this, studentProfilePicture, userUploadImage);
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
            Call<ProfilePicEditResponseDTO> call = apiInterface.changeStudentProfilePicture(loginInstance.getCustomerId(),
                    loginInstance.getLoginId(), (long) profileId, new ProfilePicEditRequest(encoddedString), loginInstance.getToken());
            uploadProgressing.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<ProfilePicEditResponseDTO>() {
                @Override
                public void onResponse(Call<ProfilePicEditResponseDTO> call, Response<ProfilePicEditResponseDTO> response) {
                    if (response.isSuccessful()) {
                        ProfilePicEditResponseDTO profilePicEditResponseDTO = response.body();
                        String pPicUri = profilePicEditResponseDTO.getUrl();
                        ImageViewLoader.loadImage(StudentDetails.this, pPicUri, stuImage);
                        uploadProgressing.setVisibility(View.GONE);
                        showImageuploadDialog.dismiss();
                        CustomSnackbar.showSuccessSnakeBar(mainLayout, "Image Changed Successfully", StudentDetails.this);
                    } else {
                        //Maps the error message in ErrorMessageDto
                        JsonParser parser = new JsonParser();
                        JsonElement mJson = null;
                        try {
                            mJson = parser.parse(response.errorBody().string());
                            Gson gson = new Gson();
                            ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                            CustomSnackbar.showFailureSnakeBar(mainLayout, errorMessageDto.getMessage(), StudentDetails.this);
                            showImageuploadDialog.dismiss();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }


                }

                @Override
                public void onFailure(Call<ProfilePicEditResponseDTO> call, Throwable t) {
                    CustomSnackbar.checkErrorResponse(stuImage, StudentDetails.this);
                    showImageuploadDialog.dismiss();
                    uploadProgressing.setVisibility(View.GONE);
                    Log.d("responseGo:", t.getMessage());

                }
            });
        } else {
            showImageuploadDialog.dismiss();
            CustomSnackbar.showFailureSnakeBar(mainLayout, "Select Image first", StudentDetails.this);

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
