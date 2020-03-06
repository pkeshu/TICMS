package com.imaginology.texas.Counseling.CreateStudentCounseling;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imaginology.texas.Counseling.Counseling_Fragment;
import com.imaginology.texas.Courses.CoursesDto;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.CourseLevel;
import com.imaginology.texas.util.DataValidityChecker;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;
import com.imaginology.texas.util.ShowNoContentPopUp;
import com.imaginology.texas.util.StatusChecker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateCounselingFragment extends Fragment {
    private EditText counseFullname, counselEmail, counselMobilenumber, counselTotalFee;
    private EditText couselAddress, counselSchool, couselCounsellor, counselRecommendator;
    private Button saveBtn;
    private ProgressBar progressBar;
    private ConstraintLayout rootRelativeLayout;
    private Spinner courseSpinner,genderSpnr;
    private List<String> courses = new ArrayList<>();
    private List<String> gender=new ArrayList<>();
    private UserLoginResponseEntity loginInstance;
    private ApiInterface apiInterface;
    private Map<String, Integer> courseNameIdMap = new HashMap<String, Integer>();
    private Integer selectedCourseId;
    private String selectedGender;
    private Map<Integer, CourseLevel> courseIdLevelMap = new HashMap<>();

    public CreateCounselingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        courses.add("Select Course");
        gender.add("Select Gender");
        gender.add("Male");
        gender.add("Female");
        gender.add("Other");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_counseling, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Create Student Counseling");
        loginInstance = new GetLoginInstanceFromDatabase(getContext()).getLoginInstance();
        apiInterface = ApiClient.getRetrofit(getContext()).create(ApiInterface.class);
        bindView(view);
        getCourse();
        geGender();
        couselCounsellor.setVisibility(View.GONE);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long totalFee = (long) 0;
                String fullname, email, phoneNO, address, schoolName, counsellorName, recomendatorName, gender;
                fullname = counseFullname.getText().toString().trim();
                email = counselEmail.getText().toString().trim();
                phoneNO = counselMobilenumber.getText().toString().trim();
                address = couselAddress.getText().toString().trim();
                schoolName = counselSchool.getText().toString().trim();
                counsellorName = couselCounsellor.getText().toString().trim();
                recomendatorName = counselRecommendator.getText().toString().trim();
                try {
                    if (!counselTotalFee.getText().toString().trim().equals("null") && !counselTotalFee.getText().toString().trim().equals("Not Available")
                            && !counselTotalFee.getText().toString().trim().equals(""))
                        totalFee = Long.valueOf(counselTotalFee.getText().toString().trim());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (DataValidityChecker.isEditTextDataValid(counseFullname, fullname, true) &&
                        DataValidityChecker.isEditTextDataValid(counselMobilenumber, phoneNO, true)
                        )
                    createStudentCounseling(fullname, email, phoneNO, address, schoolName, selectedCourseId, counsellorName, recomendatorName, totalFee, selectedGender);


            }
        });
        return view;
    }

    private void geGender() {
        ArrayAdapter<String> genderAdapter=new ArrayAdapter<>(getContext(),
                R.layout.spinner_textview_select_semester,gender);
        genderSpnr.setAdapter(genderAdapter);
        if(!gender.isEmpty()){
            setSelectActionListenerWhenGenderSelected();
        }
    }

    private void setSelectActionListenerWhenGenderSelected() {
        genderSpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!genderSpnr.getSelectedItem().toString().equals("Select Gender")&&
                        genderSpnr.getSelectedItem()!=null ){
                    selectedGender=genderSpnr.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getCourse() {
        Call<List<CoursesDto>> call = apiInterface.getAllCourse(loginInstance.getCustomerId(), loginInstance.getToken(),
                loginInstance.getLoginId());

        call.enqueue(new Callback<List<CoursesDto>>() {
            @Override
            public void onResponse(Call<List<CoursesDto>> call, Response<List<CoursesDto>> response) {
                int status = response.code();
                if (response.isSuccessful()) {
                    for (CoursesDto x : response.body()) {
                        Log.d("ParsedCourseName", response.body().get(0).getName());
                        courses.add(x.getName());
                        courseNameIdMap.put(x.getName(), x.getId());
                        courseIdLevelMap.put(x.getId(), CourseLevel.valueOf(x.getLevel()));
                    }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext()
                                , R.layout.spinner_textview_select_course, courses);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        courseSpinner.setAdapter(adapter);
                } else if (status == 401) {
                    StatusChecker.statusCheck(getContext());

                } else {
                    //Maps the error message in ErrorMessageDto
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = null;
                    try {
                        mJson = parser.parse(response.errorBody().string());
                        Gson gson = new Gson();
                        ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                        Toast.makeText(getContext(), errorMessageDto.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<CoursesDto>> call, Throwable t) {
                Toast.makeText(getContext(), R.string.no_response, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

        if (!courses.isEmpty()) {
            setSelectActionListenerWhenCourseSelected();
        } else {
            ShowNoContentPopUp.showMessage(getContext(), "There are no courses available");
        }

    }

    private void setSelectActionListenerWhenCourseSelected() {
        //Setting Click listener on Faculty Items
        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (courseSpinner.getSelectedItem() == "Select Course") {
//                    Toast.makeText(getContext(), "Select Course", Toast.LENGTH_SHORT).show();
//                    CustomSnackbar.showSuccessSnakeBar(rootRelativeLayout,"Select Course",getContext());

                } else if (courseSpinner.getSelectedItem() != null
                        && !courseSpinner.getSelectedItem().toString().equals("Select Course")) {
//                    Toast.makeText(getContext(),  " Course Name:" + spnrCourse.getSelectedItem(), Toast.LENGTH_SHORT).show();
                    selectedCourseId = courseNameIdMap.get(courseSpinner.getSelectedItem());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void bindView(View v) {
        saveBtn = v.findViewById(R.id.couns_save_btn);
        counseFullname = v.findViewById(R.id.counseling_fullname);
        counselEmail = v.findViewById(R.id.counseling_email);
        counselMobilenumber = v.findViewById(R.id.counseling_phone);
        couselAddress = v.findViewById(R.id.counseling_address);
        counselSchool = v.findViewById(R.id.counseling_shoolname);
        counselTotalFee = v.findViewById(R.id.counseling_total_fee);
        couselCounsellor = v.findViewById(R.id.counseling_counsellor);
        counselRecommendator = v.findViewById(R.id.counseling_recomendator);
        progressBar = v.findViewById(R.id.counselling_progress_bar);
        rootRelativeLayout = v.findViewById(R.id.root_layout);
        courseSpinner = v.findViewById(R.id.spnr_class_routine_course);
        genderSpnr=v.findViewById(R.id.spnr_gender);
    }

    private void createStudentCounseling(String fullname, String email, String phone, String address, String schoolName, Integer selectedCourseId,
                                         String counsellor, String recomendatoor, Long totalFee, String gender) {
        progressBar.setVisibility(View.VISIBLE);
        CreateStudentCounselingDto createStudentCounselingDto = new CreateStudentCounselingDto(
                address, counsellor, selectedCourseId, email, fullname, gender, phone, recomendatoor, schoolName, totalFee
        );

        Call<ResponseBody> call = apiInterface.createStudentCounselling(loginInstance.getCustomerId(),
                loginInstance.getLoginId(),
                createStudentCounselingDto,
                loginInstance.getToken());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Student for Counsel is created", Toast.LENGTH_SHORT).show();
//                    getActivity().startActivity(new Intent(getContext(),Counseling_Fragment.class));
//                    getActivity().finish();
                    rootRelativeLayout.setVisibility(View.GONE);
                    FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.back_couns_layout, new Counseling_Fragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else {
                    //Maps the error message in ErrorMessageDto
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = null;
                    try {
                        mJson = parser.parse(response.errorBody().string());
                        Gson gson = new Gson();
                        ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                        Toast.makeText(getContext(), errorMessageDto.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), R.string.no_response, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);

            }
        });
    }

}
