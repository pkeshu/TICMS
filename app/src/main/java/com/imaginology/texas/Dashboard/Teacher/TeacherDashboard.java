package com.imaginology.texas.Dashboard.Teacher;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.imaginology.texas.MainFragment;
import com.imaginology.texas.Notification.OfflineNotification.RoutineNotificationReceiver;
import com.imaginology.texas.Notification.OfflineNotification.RoutineNotificationService;
import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.RoutineEntity.RoutineEntity;
import com.imaginology.texas.RoomDatabase.RoutineEntity.RoutineDao;
import com.imaginology.texas.RoomDatabase.TicmsRoomDatabase;
import com.imaginology.texas.SnackBar.CustomSnackbar;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.ImageViewLoader;
import com.imaginology.texas.util.LoginChecker;
import com.imaginology.texas.util.NavmenuHideShow;
import com.imaginology.texas.Routine.Util.NotificationChannelCreator;
import com.imaginology.texas.util.SharedPreferencesUtil;
import com.imaginology.texas.util.StatusChecker;
import com.imaginology.texas.util.SupportActionBarInitializer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherDashboard extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView rvRoutine;
    private TeacherDashboardAdapter teacherDashboardAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NavigationView navigationView;
    private View noRoutineView;
    private TextView tvNoRoutineCallAdmin;
    private TextView mUsername,mEmail,mLogin,notificationCountTxt;
    private ImageView headernavimage,mUserProfile,notificationImage;
    private FrameLayout rootFrameLayout;

    public TeacherDashboard() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_teacher_dashboard, container, false);
        SupportActionBarInitializer.setSupportActionBarTitle( ((AppCompatActivity) getActivity()).getSupportActionBar(),"Your Routine");

        rvRoutine=view.findViewById(R.id.rv_routine_container);
        swipeRefreshLayout= view.findViewById(R.id.swiperefresh);
        navigationView= getActivity().findViewById(R.id.nav_view);
        View mView=navigationView.getHeaderView(0);



        mUsername=mView.findViewById(R.id.username1);
        mEmail=mView.findViewById(R.id.email1);
        headernavimage=mView.findViewById(R.id.image);
        mLogin=mView.findViewById(R.id.loginfirst);


        noRoutineView = view.findViewById(R.id.no_routine_container);
        tvNoRoutineCallAdmin = view.findViewById(R.id.tv_no_data_contact_admin);
        rootFrameLayout = view.findViewById(R.id.fl_root_layout);

        //Create Channel for Notification for android version equal or greater than Oreo
        NotificationChannelCreator.createNotificationChannel(getContext(),RoutineNotificationReceiver.NOTIFICATION_CHANNEL);

        if(LoginChecker.IsLoggedIn(getContext())){
            SharedPreferences sharedPref = getContext().getSharedPreferences("loginDetails", Context.MODE_PRIVATE);
            NavmenuHideShow.showHideNavMenuAccordingToLoginRole(navigationView,sharedPref.getString("loginType",""),getContext());
        }else {
            Fragment fragment = new MainFragment();
            android.support.v4.app.FragmentTransaction ft= getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        callServerForRoutine();
        swipeRefreshLayout.setOnRefreshListener(this);

        rvRoutine.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        tvNoRoutineCallAdmin.setOnClickListener(view1 -> {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + "+977014479017"));

                getContext().startActivity(intent);

            }else {
                ActivityCompat.requestPermissions(getActivity(),new String[]{
                        Manifest.permission.CALL_PHONE
                },0);
            }
        });

        return view;
    }

    private void callServerForRoutine() {

        ApiInterface apiInterface = ApiClient.getRetrofit(getContext()).create(ApiInterface.class);
        Call<TeacherDashboardDTO> call= apiInterface.getTeacherRoutines(SharedPreferencesUtil.getUserId(getActivity()),
                SharedPreferencesUtil.getCustomerId(),SharedPreferencesUtil.getLoginId(),
                SharedPreferencesUtil.getToken());
        call.enqueue(new Callback<TeacherDashboardDTO>() {
            @Override
            public void onResponse(Call<TeacherDashboardDTO> call, Response<TeacherDashboardDTO> response) {

                int status = response.code();

                Log.d("OnResponse :", String.valueOf(response.isSuccessful()));
                if(response.isSuccessful()){
                    swipeRefreshLayout.setRefreshing(false);
                    TeacherDashboardDTO teacherDashboardDTOList=response.body();
                    if(teacherDashboardDTOList.getData()!=null && !teacherDashboardDTOList.getData().isEmpty() && teacherDashboardDTOList.getData().size()>0){
                        Log.d("Null empty","Not null not empty and size: "+ teacherDashboardDTOList.getData().size());
                        noRoutineView.setVisibility(View.GONE);
                        teacherDashboardAdapter= new TeacherDashboardAdapter(getContext(),loadDataSetForAdapter(teacherDashboardDTOList.getData().get(0)));
                        rvRoutine.setAdapter(teacherDashboardAdapter);
                        rvRoutine.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                        startRotineNotificationServie();

                    } else if (status==401){
                        StatusChecker.statusCheck(getContext());

                    } else{
                        Log.d("callServerForRoutine","Called");
                        noRoutineView.setVisibility(View.VISIBLE);
                    }

                } else {
                    Log.d("Status: ", String.valueOf(response.code()));
                    Log.d("Message: ",response.message());
                }

                //To hide swipeRefresh loader
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<TeacherDashboardDTO> call, Throwable t) {
                Log.d("Failure msg: ", t.getMessage());
                CustomSnackbar.checkErrorResponse(rootFrameLayout,getContext());
            }
        });

    }

    private List<TeacherRoutineDTO> loadDataSetForAdapter(TeacherDashboardDTO.Datum teacherDashboardDTOList) {
        List<TeacherRoutineDTO> teacherRoutineDTOS = new ArrayList<>();
        List<RoutineEntity> routineEntityList = new ArrayList<>();
        if(teacherDashboardDTOList.getCourses()!=null) {
            for (TeacherDashboardDTO.Course course : teacherDashboardDTOList.getCourses()) {
                for (TeacherDashboardDTO.Semester semester : course.getSemesters()) {
                    TeacherRoutineDTO teacherRoutineDTO = new TeacherRoutineDTO();
                    List<RoutineDTO> routinesList = new ArrayList<>();
                    for (RoutineDTO routine : semester.getRoutines()) {
                        RoutineEntity routineEntity = new RoutineEntity();

                        teacherRoutineDTO.setCourse(course.getName());
                        teacherRoutineDTO.setSemester(semester.getValue());
                        RoutineDTO teacherRoutine = new RoutineDTO();
                        teacherRoutine.setStartTime(routine.getStartTime());
                        teacherRoutine.setEndTime(routine.getEndTime());
                        teacherRoutine.setDay(routine.getDay());
                        teacherRoutine.setSubject(routine.getSubject());




                        routinesList.add(teacherRoutine);
                        teacherRoutineDTO.setRoutinesList(routinesList);

                        //Routine to store in database
                        routineEntity.setCourse(course.getName());
                        routineEntity.setSemester(semester.getValue());
                        routineEntity.setDay(routine.getDay());
                        routineEntity.setStartTime(routine.getStartTime());
                        routineEntity.setEndTime(routine.getEndTime());
                        routineEntity.setSubject(routine.getSubject());



                        routineEntityList.add(routineEntity);
                    }
                    teacherRoutineDTOS.add(teacherRoutineDTO);
                }
            }
        }
        //Store routine in database
        TicmsRoomDatabase ticmsRoomDatabase=TicmsRoomDatabase.getDatabaseInstance(getActivity());
        RoutineDao routineDao= ticmsRoomDatabase.getRoutineDao();
       new AsyncTask<List<RoutineEntity>,Void,Void>(){

            @Override
            protected Void doInBackground(List<RoutineEntity>... lists) {
                routineDao.storeRoutineToDatabase(lists[0]);
                Log.d("Store Routine :","Data is stored. Length "+ routineEntityList.size());
                return null;
            }
        }.execute(routineEntityList);


        Log.d("list length:: ", String.valueOf(teacherRoutineDTOS.size()));
        return teacherRoutineDTOS;
    }

    private void startRotineNotificationServie(){
        Log.d("Server Starter","called");
        Intent serviceIntent = new Intent(getContext(), RoutineNotificationService.class);
        getContext().startService(serviceIntent);
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences sharedPref = getActivity().getSharedPreferences("loginDetails", Context.MODE_PRIVATE);
        String username, email, imageUrl;
        username = sharedPref.getString("firstname", "") + " " + sharedPref.getString("lastname", "");
        email = sharedPref.getString("email", "");
        imageUrl = sharedPref.getString("profilePicture", "Not Available");
        if (LoginChecker.IsLoggedIn(getContext())) {
            NavmenuHideShow.showHideNavMenuAccordingToLoginRole(navigationView, sharedPref.getString("loginType", ""),getContext());
            if (mUsername.getVisibility() != View.VISIBLE || mEmail.getVisibility() != View.VISIBLE) {
                mUsername.setVisibility(View.VISIBLE);
                mEmail.setVisibility(View.VISIBLE);
                mLogin.setVisibility(View.INVISIBLE);
            }
            mUsername.setText(username);
            mEmail.setText(email);
           ImageViewLoader.loadImage(getContext(),imageUrl,headernavimage);
        } else {
            NavmenuHideShow.showHideNavMenuAccordingToLoginRole(navigationView, sharedPref.getString("loginType", ""),getContext());
            if (mUsername.getVisibility() != View.INVISIBLE || mEmail.getVisibility() != View.INVISIBLE) {
                mUsername.setVisibility(View.INVISIBLE);
                mEmail.setVisibility(View.INVISIBLE);
            }
            if (mLogin.getVisibility() == View.INVISIBLE) {
                mLogin.setVisibility(View.VISIBLE);
            }
            ImageViewLoader.loadImage(getContext(),imageUrl,headernavimage);
        }

    }


    @Override
    public void onRefresh() {
        callServerForRoutine();
        //To show swipeRefresh loader icon
        swipeRefreshLayout.setRefreshing(false);
    }


}
