package com.imaginology.texas.Subjects;

import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imaginology.texas.Courses.CoursesDto;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.SnackBar.CustomSnackbar;
import com.imaginology.texas.util.CourseLevel;
import com.imaginology.texas.util.Semester;
import com.imaginology.texas.util.SemesterUtil;
import com.imaginology.texas.util.ShowNoContentPopUp;
import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;

import com.imaginology.texas.util.StatusChecker;

import com.imaginology.texas.util.SupportActionBarInitializer;

import com.imaginology.texas.util.StatusChecker;
import com.imaginology.texas.util.SupportActionBarInitializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubjectListFragment extends Fragment {
    private SubjectsDtoAdapter subjectsDtoAdapter;
    private ProgressBar progressBar;
    private Integer selectedCourseId;
    private String getSemester;
    private TextView labelSubject;
    private TextView labelSubCode;
    ArrayList<CharSequence> courses = new ArrayList<>(Collections.EMPTY_LIST);
    private Map<String, Integer> courseNameIdMap = new HashMap<String, Integer>();

    Spinner mSelectCourse, mSelectSemester;
    ApiInterface apiInterface;
    private UserLoginResponseEntity loginInstance;
    private RecyclerView rvSubjectHolder;
    final String [] semester = {"Select Semester","FIRST","SECOND","THIRD","FOURTH","FIFTH","SIXTH","SEVENTH","EIGHTH"};
    Map<Integer, CourseLevel> courseIdLevelMap = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For login instance of current user
        courses.add("Select Course");
        GetLoginInstanceFromDatabase databaseInstanceAccessor = new GetLoginInstanceFromDatabase(getContext());
        loginInstance = databaseInstanceAccessor.getLoginInstance();
        apiInterface = ApiClient.getRetrofit(getContext()).create(ApiInterface.class);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subject_list, container, false);
        progressBar = view.findViewById(R.id.courselist_progressbar);
        progressBar.setVisibility(View.VISIBLE);

        bindViews(view);
        SupportActionBarInitializer.setSupportActionBarTitle(((AppCompatActivity)getActivity()).getSupportActionBar(),"Subjects");

        labelSubject.setVisibility(View.GONE);
        labelSubCode.setVisibility(View.GONE);
        mSelectCourse.setVisibility(View.GONE);
        mSelectSemester.setVisibility(View.GONE);
        getCourse();
        //defineView(view);
        return view;
    }

    private void bindViews(View view) {
        rvSubjectHolder= view.findViewById(R.id.recyclerview_course);
        mSelectCourse = view.findViewById(R.id.select_course);
        mSelectSemester = view.findViewById(R.id.select_semester);
        labelSubject = view.findViewById(R.id.label_subjects);
        labelSubCode = view.findViewById(R.id.label_subject_code);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    public void getCourse() {

        Call<List<CoursesDto>> call = apiInterface.getAllCourse(loginInstance.getCustomerId(),
                loginInstance.getToken(),loginInstance.getLoginId());
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
                    mSelectCourse.setVisibility(View.VISIBLE);
                    ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getContext(),
                            R.layout.spinner_textview_select_course, courses);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSelectCourse.setAdapter(adapter);


                } else if (status==401){
                    StatusChecker.statusCheck(getContext());

                } else {
                    //Maps the error message in ErrorMessageDto
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = null;
                    try {
                        mJson = parser.parse(response.errorBody().string());
                        Gson gson = new Gson();
                        ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
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

        if(!courses.isEmpty()){
            setSelectActionListenerWhenCourseSelected();
        }else{
            //ShowNoContentPopUp.showMessage(getContext(),"There are no courses available");

            View v=getActivity().findViewById(android.R.id.content);
            CustomSnackbar.showFailureSnakeBar(v,"There are no subjects for your selection.", getContext());
        }

    }

    private void setSelectActionListenerWhenCourseSelected() {
        //Setting Click listener on Faculty Items
        mSelectCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(mSelectCourse.getSelectedItem()=="Select Course"){
                    mSelectSemester.setVisibility(View.GONE);

                }
                else if (mSelectCourse.getSelectedItem() != null
                        && !mSelectCourse.getSelectedItem().toString().equalsIgnoreCase("Select Course")) {
                    selectedCourseId = courseNameIdMap.get(mSelectCourse.getSelectedItem());
                    mSelectSemester.setVisibility(View.VISIBLE);
                    displaySemesterSpinner(selectedCourseId);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSelectSemester.setVisibility(View.GONE);
            }
        });
    }
    void displaySemesterSpinner(Integer courseId) {
        CourseLevel courseLevel = courseIdLevelMap.get(courseId);

        ArrayAdapter<CharSequence> adapter1 = new ArrayAdapter<>(getContext(),
                R.layout.spinner_textview_select_semester,SemesterUtil.getSemester(courseLevel));
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSelectSemester.setAdapter(adapter1);
        mSelectSemester.performClick();
        getSemester();

    }

    private void getSemester(){


        mSelectSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if(mSelectSemester.getSelectedItem()=="Select Semester"){
                }
                else if(mSelectSemester.getSelectedItem()!=null
                        && !mSelectSemester.getSelectedItem().toString().equals("Select Semester")){
                    getSemester = semester[position];
                    progressBar.setVisibility(View.VISIBLE);
                    getSubjectsList();
                    labelSubject.setVisibility(View.VISIBLE);
                    labelSubCode.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }

    private void getSubjectsList() {

        Call<List<SubjectsDto>> call = apiInterface.getListOfSubjects(loginInstance.getCustomerId(),
                Long.valueOf(selectedCourseId), getSemester,loginInstance.getToken(),loginInstance.getLoginId());
        call.enqueue(new Callback<List<SubjectsDto>>() {
            @Override
            public void onResponse(Call<List<SubjectsDto>> call, Response<List<SubjectsDto>> response) {

                int status = response.code();

                if(response.isSuccessful()){

                    List<SubjectsDto> subjectsDtos = response.body();
                    if(subjectsDtos.isEmpty()){
                       // ShowNoContentPopUp.showMessage(getContext(),"There are no Subjects available");
                        View v=getActivity().findViewById(android.R.id.content);
                        CustomSnackbar.showFailureSnakeBar(v,"There are no subjects for your selection.", getContext());

                        labelSubject.setVisibility(View.GONE);
                        labelSubCode.setVisibility(View.GONE);
                    }
                    subjectsDtoAdapter = new SubjectsDtoAdapter(subjectsDtos, R.layout.course_list_adapter, getContext());
                    rvSubjectHolder.setAdapter(subjectsDtoAdapter);
                    rvSubjectHolder.setLayoutManager(new LinearLayoutManager(getContext()));

                } else if (status==401){
                    StatusChecker.statusCheck(getContext());

                } else{
                    //Maps the error message in ErrorMessageDto
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = null;
                    try {
                        mJson = parser.parse(response.errorBody().string());
                        Gson gson = new Gson();
                        ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                      //  Toast.makeText(getContext(), errorMessageDto.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<List<SubjectsDto>> call, Throwable t) {
                Toast.makeText(getContext(), R.string.no_response, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }
}
