package com.imaginology.texas.ClassRoutine;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imaginology.texas.Courses.CoursesDto;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.SnackBar.CustomSnackbar;
import com.imaginology.texas.util.CourseLevel;
import com.imaginology.texas.util.SemesterUtil;
import com.imaginology.texas.util.ShowNoContentPopUp;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.Dashboard.Teacher.RoutineDTO;
import com.imaginology.texas.Dashboard.Teacher.TeacherDashboardAdapter;
import com.imaginology.texas.Dashboard.Teacher.TeacherRoutineDTO;
import com.imaginology.texas.R;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;
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


public class ClassRoutineFragment extends Fragment {
    private ProgressBar progressBar;
    private Spinner spnrCourse;
    private List<String> courses = new ArrayList<>();
    private List<String> courseId = new ArrayList<>();
    private UserLoginResponseEntity loginInstance;
    private String getSemester;
    private RecyclerView rvRoutineTable;
    private ApiInterface apiInterface;
    final String [] semester = {"Select Semester","First","Second","Third","Fourth","Fifth","Sixth","Seventh","Eight"};
    private Map<String, Integer> courseNameIdMap = new HashMap<String, Integer>();
    private Integer selectedCourseId;
    private Spinner mSelectSemester;
    private Map<Integer, CourseLevel> courseIdLevelMap = new HashMap<>();
    private ConstraintLayout mainLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        courses.add("Select Course");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_class_routine, container, false);

        //For login instance of current user
        GetLoginInstanceFromDatabase databaseInstanceAccessor = new GetLoginInstanceFromDatabase(getContext());
        loginInstance = databaseInstanceAccessor.getLoginInstance();
        apiInterface = ApiClient.getRetrofit(getContext()).create(ApiInterface.class);
        SupportActionBarInitializer.setSupportActionBarTitle(((AppCompatActivity)getActivity()).getSupportActionBar(),"Class Routine");

        spnrCourse = view.findViewById(R.id.spnr_class_routine_course);
        progressBar = view.findViewById(R.id.progress_bar);
        mainLayout=view.findViewById(R.id.main_layout);
        rvRoutineTable = view.findViewById(R.id.rv_routine_table_list_holder);
        mSelectSemester = view.findViewById(R.id.select_semester);
        mSelectSemester.setVisibility(View.INVISIBLE);
        getCourse();
        return view;
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
                    spnrCourse.setAdapter(adapter);
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
                       // Toast.makeText(getContext(), errorMessageDto.getMessage(), Toast.LENGTH_SHORT).show();
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
            ShowNoContentPopUp.showMessage(getContext(),"There are no courses available");
        }
    }

    private void setSelectActionListenerWhenCourseSelected() {
        //Setting Click listener on Faculty Items
        spnrCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spnrCourse.getSelectedItem()=="Select Course"){
//                    Toast.makeText(getContext(), "Select Course", Toast.LENGTH_SHORT).show();
//                    CustomSnackbar.showSuccessSnakeBar(mainLayout,"Select Course",getContext());
                    mSelectSemester.setVisibility(View.INVISIBLE);
                    rvRoutineTable.setVisibility(View.GONE);

                }
                else if (spnrCourse.getSelectedItem() != null
                        && !spnrCourse.getSelectedItem().toString().equals("Select Faculty")) {
//                    Toast.makeText(getContext(),  " Course Name:" + spnrCourse.getSelectedItem(), Toast.LENGTH_SHORT).show();
                    selectedCourseId = courseNameIdMap.get(spnrCourse.getSelectedItem());
                    displaySemesterSpinner(selectedCourseId);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    void displaySemesterSpinner(Integer courseId){
        CourseLevel courseLevel = courseIdLevelMap.get(courseId);

        ArrayAdapter<CharSequence> adapter1 = new ArrayAdapter<>(getContext(),
                R.layout.spinner_textview_select_semester, SemesterUtil.getSemester(courseLevel));

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSelectSemester.setAdapter(adapter1);
//        mSelectSemester.performClick();
        getSemester();
    }


    private void getSemester( ){


        mSelectSemester.setVisibility(View.VISIBLE);
        mSelectSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if(mSelectSemester.getSelectedItem()=="Select_Semester"){
//                    CustomSnackbar.showSuccessSnakeBar(mainLayout,"Select Semester",getContext());
//                               Toast.makeText(getContext(), "Select Semester", Toast.LENGTH_SHORT).show();

                }
                else if(mSelectSemester.getSelectedItem()!=null
                        && !mSelectSemester.getSelectedItem().toString().equals("Select_Semester")){
                    getSemester = semester[position];
                    mSelectSemester.setVisibility(View.VISIBLE);
                    rvRoutineTable.setVisibility(View.VISIBLE);
                    displayRoutine();


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }


    private void displayRoutine() {
        progressBar.setVisibility(View.VISIBLE);

        Call<RoutineResponseDto> call = apiInterface.classRoutineDto(loginInstance.getCustomerId(),
                loginInstance.getLoginId(),  Long.valueOf(selectedCourseId), loginInstance.getToken());
        call.enqueue(new Callback<RoutineResponseDto>() {
            @Override
            public void onResponse(Call<RoutineResponseDto> call, Response<RoutineResponseDto> response) {

                progressBar.setVisibility(View.GONE);
                List<TeacherRoutineDTO> routineListForAdap = new ArrayList<>();
                int status = response.code();
                if (response.isSuccessful()) {
                    RoutineResponseDto routineResponseDto = response.body();

                if(routineResponseDto != null) {
                    routineListForAdap.clear();
                    if (!routineResponseDto.getSemesterRoutineResponse().isEmpty()){



                        for (SemesterRoutineResponse routineResponse : routineResponseDto.getSemesterRoutineResponse()) {

                            if(routineResponse.getSemester().equals(getSemester)){
                                TeacherRoutineDTO teacherRoutineDTO = new TeacherRoutineDTO();
                                teacherRoutineDTO.setCourse(routineResponseDto.getCourseName());
                                teacherRoutineDTO.setSemester(routineResponse.getSemester());
                                teacherRoutineDTO.setRoutinesList(getRoutineListForTable(routineResponse.getRoutines()));
                                routineListForAdap.add(teacherRoutineDTO);
                               }
                             }



                        }
                    }

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

                TeacherDashboardAdapter teacherDashboardAdapter = new TeacherDashboardAdapter(getContext(), routineListForAdap);
                if (teacherDashboardAdapter.getItemCount() == 0) {
                    CustomSnackbar.showSuccessSnakeBar(mainLayout,"There are no routines available for your selection",getContext());
//                    ShowNoContentPopUp.showMessage(getContext(), "There are no routines available for your selection");
                }
                rvRoutineTable.setAdapter(teacherDashboardAdapter);
                rvRoutineTable.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void onFailure(Call<RoutineResponseDto> call, Throwable t) {
                progressBar.setVisibility(View.GONE);

                Toast.makeText(getContext(), R.string.no_response, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private List<RoutineDTO> getRoutineListForTable(List<ClassRoutineDto> routines) {
        List<RoutineDTO> routineDTOList = new ArrayList<>();

        for (ClassRoutineDto classRoutineDto : routines) {
            RoutineDTO sunRoutineDTO = new RoutineDTO();
            sunRoutineDTO.setDay("Sunday");
            sunRoutineDTO.setStartTime(classRoutineDto.getStartTime());
            sunRoutineDTO.setEndTime(classRoutineDto.getEndTime());
            if(classRoutineDto.getSunday().equals("")){
                sunRoutineDTO.setSubject("No Class");

            }else
                sunRoutineDTO.setSubject(classRoutineDto.getSunday());


            RoutineDTO monRoutineDTO = new RoutineDTO();
            monRoutineDTO.setDay("Monday");
            monRoutineDTO.setStartTime(classRoutineDto.getStartTime());
            monRoutineDTO.setEndTime(classRoutineDto.getEndTime());
            if(classRoutineDto.getMonday().equals("")){
                monRoutineDTO.setSubject("No Class");
            }else
                monRoutineDTO.setSubject(classRoutineDto.getMonday());


            RoutineDTO tuesRoutineDTO = new RoutineDTO();
            tuesRoutineDTO.setDay("Tuesday");
            tuesRoutineDTO.setStartTime(classRoutineDto.getStartTime());
            tuesRoutineDTO.setEndTime(classRoutineDto.getEndTime());
            if(classRoutineDto.getTuesday().equals(""))
                tuesRoutineDTO.setSubject("No class");
            else
                tuesRoutineDTO.setSubject(classRoutineDto.getTuesday());



            RoutineDTO wedRoutineDTO = new RoutineDTO();
            wedRoutineDTO.setDay("Wednesday");
            wedRoutineDTO.setStartTime(classRoutineDto.getStartTime());
            wedRoutineDTO.setEndTime(classRoutineDto.getEndTime());
            if(classRoutineDto.getWednesday().equals("")){
                wedRoutineDTO.setSubject("No class");

            }else
                wedRoutineDTO.setSubject(classRoutineDto.getWednesday());

            RoutineDTO thuRoutineDTO = new RoutineDTO();
            thuRoutineDTO.setDay("Thursday");
            thuRoutineDTO.setStartTime(classRoutineDto.getStartTime());
            thuRoutineDTO.setEndTime(classRoutineDto.getEndTime());
            if(classRoutineDto.getThursday().equals(""))
                thuRoutineDTO.setSubject("No class");
            else
                thuRoutineDTO.setSubject(classRoutineDto.getThursday());



            RoutineDTO friRoutineDTO = new RoutineDTO();
            friRoutineDTO.setDay("Friday");
            friRoutineDTO.setStartTime(classRoutineDto.getStartTime());
            friRoutineDTO.setEndTime(classRoutineDto.getEndTime());
            if(classRoutineDto.getFriday().equals(""))
                friRoutineDTO.setSubject("No class");
            else
                friRoutineDTO.setSubject(classRoutineDto.getFriday());



            RoutineDTO satRoutineDTO = new RoutineDTO();
            satRoutineDTO.setDay("Saturday");
            satRoutineDTO.setStartTime(classRoutineDto.getStartTime());
            satRoutineDTO.setEndTime(classRoutineDto.getEndTime());
            if(classRoutineDto.getSaturday().equals(""))
                satRoutineDTO.setSubject("No class");
            else
                satRoutineDTO.setSubject(classRoutineDto.getSaturday());


            //Adding routine to list
            routineDTOList.add(sunRoutineDTO);
            routineDTOList.add(monRoutineDTO);
            routineDTOList.add(tuesRoutineDTO);
            routineDTOList.add(wedRoutineDTO);
            routineDTOList.add(thuRoutineDTO);
            routineDTOList.add(friRoutineDTO);
            routineDTOList.add(satRoutineDTO);


        }

        return routineDTOList;
    }
}

