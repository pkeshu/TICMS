package com.imaginology.texas.Courses;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imaginology.texas.Counseling.CouncelingCourseList;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.SnackBar.CustomSnackbar;
import com.imaginology.texas.Subjects.SubjectsDto;
import com.imaginology.texas.Subjects.SubjectsDtoAdapter;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.CourseLevel;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;
import com.imaginology.texas.util.ShowNoContentPopUp;
import com.imaginology.texas.util.StatusChecker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CourseListFragment extends Fragment implements CustomCourseAdapter.ClickListener {
    private RecyclerView courseRecyclerView, subjectRecyclerView;
    private ProgressBar progressBar;
    private List<CoursesDto> coursesDtoList = new ArrayList<>();
    private Map<String, Integer> courseNameIdMap = new HashMap<String, Integer>();
    public static Spinner mSemesterSpinner, courseSpinner;
    public static View myView, subjectContainer;
    private TextView tvSubjectName;
    private String[] courseid;
    private TextView tvSubjectCode;
    private UserLoginResponseEntity loginInstance;
    private CustomCourseAdapter customCourseAdapter;
    LinearLayoutManager layoutManager;
    private Integer getCourseId;
    private String getSemester;
    private String[] semester;
    private Integer selectedCourseId;
    private String index1;
    private String getCourseName;
    private List<String> courses = new ArrayList<>();
    private List<String> coursesid = new ArrayList<>();
    private String spinner1;
    private TextView labelCourse;
    Map<Integer, CourseLevel> courseIdLevelMap = new HashMap<>();


    String courseLevel;

    private ApiInterface apiInterface = ApiClient.getRetrofit(getContext()).create(ApiInterface.class);

    public CourseListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_courses_list, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Courses");

        GetLoginInstanceFromDatabase loginInstanceFromDatabase = new GetLoginInstanceFromDatabase(getContext());
        loginInstance = loginInstanceFromDatabase.getLoginInstance();

        defineViews(view);
        return view;
    }

    private void defineViews(View view) {
        courseSpinner = view.findViewById(R.id.spnr_class_routine_course);
        courseRecyclerView = view.findViewById(R.id.recyclerview_courses);
        subjectRecyclerView = view.findViewById(R.id.subject_recyclerview);
        progressBar = view.findViewById(R.id.courseslist_progressbar);
        mSemesterSpinner = view.findViewById(R.id.semester_spinner);
        progressBar.setVisibility(View.VISIBLE);
        myView = view.findViewById(R.id.course_view_container);
        subjectContainer = view.findViewById(R.id.subject_view_container);
        tvSubjectName = view.findViewById(R.id.label_courses_sub_name);
        tvSubjectCode = view.findViewById(R.id.label_courses_sub_code);
        labelCourse=view.findViewById(R.id.label_course);
        layoutManager = new LinearLayoutManager(getContext());
        courseRecyclerView.setLayoutManager(layoutManager);
        customCourseAdapter = new CustomCourseAdapter(getContext(), R.layout.courses_list_row);
        customCourseAdapter.setNotificationClickListener(this);
        courseRecyclerView.setAdapter(customCourseAdapter);
        courseRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                boolean isScrolling;
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isScrolling = true;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//
            }


        });

        mainWork();
    }

    private void mainWork() {
//        courseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        //SharedPreferences sharedPref = getActivity().getSharedPreferences("loginDetails", Context.MODE_PRIVATE);

        Call<List<CoursesDto>> call = apiInterface.getAllCourse(loginInstance.getCustomerId(),
                loginInstance.getToken(), loginInstance.getLoginId());
        call.enqueue(new Callback<List<CoursesDto>>() {
            @Override
            public void onResponse(Call<List<CoursesDto>> call, Response<List<CoursesDto>> response) {

                int status = response.code();
                if (response.isSuccessful()) {
                    coursesDtoList = response.body();
                        try {
                            if (!coursesDtoList.isEmpty()) {
                                labelCourse.setVisibility(View.GONE);
                                customCourseAdapter.addCourseInList(coursesDtoList);
                                for (CoursesDto courseDtoList1 : coursesDtoList) {
                                    courses.add(courseDtoList1.getName());
                                    coursesid.add(courseDtoList1.getId().toString());

                                    courseNameIdMap.put(courseDtoList1.getName(), courseDtoList1.getId());
                                    courseIdLevelMap.put(courseDtoList1.getId(), CourseLevel.valueOf(courseDtoList1.getLevel()));

//                        System.out.println("Course id is:::::" +courseDtoList1.getLevel());
                                    String level = courseDtoList1.getLevel();
                                    System.out.println("Level of course is::::" + level);

                                }
                            }else {
                                labelCourse.setVisibility(View.VISIBLE);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            labelCourse.setVisibility(View.VISIBLE);
                        }


//                    coursesCustomAdapter=new CoursesCustomAdapter(coursesDtoList,R.layout.courses_list_row,getContext(),courseRecyclerView,subjectRecyclerView,tvSubjectName,tvSubjectCode);
//                    courseRecyclerView.setAdapter(coursesCustomAdapter);


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


    }


    @Override
    public void itemClicked(View v, CoursesDto notificationDto, int position) {
        CourseListFragment.myView.setVisibility(View.GONE);
        CourseListFragment.subjectContainer.setVisibility(View.VISIBLE);
        getCourseId = coursesDtoList.get(position).getId();
        getCourseName = coursesDtoList.get(position).getName();
//        courseLevel = courseNameIdMap.get(coursesDtoList.);
        courseLevel = coursesDtoList.get(position).getLevel();
        System.out.println("Course Level is:::::" + courseLevel);
        if (courses.contains(getCourseName)) {

            index1 = getCourseName;
        }
        getCourseclickevent(courseLevel);


    }


    private void getSemester(String courseName) {
        courseLevel = courseName;

        System.out.println("LEV:::" + courseLevel);
        if (courseLevel.equals("PLUS_TWO")) {
            semester = new String[]{"Select Year", "FIRST", "SECOND"};
        } else if (courseLevel.equals("MASTERS")) {
            semester = new String[]{"Select Semester", "FIRST", "SECOND", "THIRD", "FOURTH"};
        } else {
            semester = new String[]{"Select Semester", "FIRST", "SECOND", "THIRD", "FOURTH", "FIFTH", "SIXTH", "SEVENTH", "EIGHTH"};
        }
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getContext()
                , R.layout.spinner_textview_select_semester, semester);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSemesterSpinner.setAdapter(adapter1);
        mSemesterSpinner.performClick();
        mSemesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mSemesterSpinner.getSelectedItem() != null) {
                    getSemester = semester[position];
                    if (position == 0) {
                        subjectRecyclerView.setVisibility(View.INVISIBLE);
                    }

                    getCourseId = courseNameIdMap.get(courseSpinner.getSelectedItem());

                    getSubjectsList();
                    tvSubjectName.setVisibility(View.INVISIBLE);
                    tvSubjectCode.setVisibility(View.INVISIBLE);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });


    }

    private void getCourseclickevent(String levelOfCourse) {
        courseLevel = levelOfCourse;
        System.out.println("Leveling of course is:::::" + courseLevel);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext()
                , R.layout.spinner_textview_select_course, courses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(adapter);
        selectedCourseId = adapter.getPosition(index1);


        courseSpinner.setSelection(selectedCourseId);

        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (courseSpinner.getSelectedItem() == "Select Course") {
                    Toast.makeText(getContext(), "Select Course", Toast.LENGTH_SHORT).show();

                } else if (courseSpinner.getSelectedItem() != null
                        && !courseSpinner.getSelectedItem().toString().equals("Select Faculty")) {
                    String selection = courseSpinner.getSelectedItem().toString();
                    Integer courseId = courseNameIdMap.get(courseSpinner.getSelectedItem().toString());
                    courseLevel = String.valueOf(courseIdLevelMap.get(courseId));
                    getSemester(courseLevel);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });
    }

    private void getSubjectsList() {
        subjectRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SharedPreferences sharedPref = (getActivity()).getSharedPreferences("loginDetails", Context.MODE_PRIVATE);

        Call<List<SubjectsDto>> call = apiInterface.getListOfSubjects(sharedPref.getLong("customerId", 0),
                Long.valueOf(getCourseId), getSemester, sharedPref.getString("token", ""), sharedPref.getLong("loginId", 0));
        call.enqueue(new Callback<List<SubjectsDto>>() {
            @Override
            public void onResponse(Call<List<SubjectsDto>> call, Response<List<SubjectsDto>> response) {
                List<SubjectsDto> subjectsDtos = new ArrayList<>();

                int status = response.code();

                if (response.isSuccessful()) {
                    subjectsDtos = response.body();
                    tvSubjectName.setVisibility(View.VISIBLE);
                    tvSubjectCode.setVisibility(View.VISIBLE);
                    if (subjectsDtos.isEmpty()) {


                     //   ShowNoContentPopUp.showMessage(getContext(), "There are no subjects for your selection.");
                        View v=getActivity().findViewById(android.R.id.content);
                        CustomSnackbar.showFailureSnakeBar(v,"There are no subjects for your selection.", getContext());

                        String spinner1 = courseSpinner.getSelectedItem().toString();
                        getSemester(spinner1);

                    }
                    subjectRecyclerView.setVisibility(View.VISIBLE);
                    SubjectsDtoAdapter subjectsDtoAdapter = new SubjectsDtoAdapter(subjectsDtos, R.layout.course_list_adapter, getContext());
                    subjectRecyclerView.setAdapter(subjectsDtoAdapter);


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
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SubjectsDto>> call, Throwable t) {
                Toast.makeText(getContext(), R.string.no_response, Toast.LENGTH_SHORT).show();
            }
        });

    }


}
