package com.imaginology.texas.Dashboard;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.imaginology.texas.ClassRoutine.ClassRoutineFragment;
import com.imaginology.texas.Courses.CourseListFragment;

import com.imaginology.texas.MainFragment;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.Subjects.SubjectListFragment;
import com.imaginology.texas.Users.UserListFragment;
import com.imaginology.texas.R;
import com.imaginology.texas.Students.StudentListFragment;
import com.imaginology.texas.Teachers.TeacherListFragment;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;
import com.imaginology.texas.util.ImageViewLoader;
import com.imaginology.texas.util.LoginChecker;
import com.imaginology.texas.util.NavmenuHideShow;
import com.imaginology.texas.util.SupportActionBarInitializer;


public class DashboardFragment extends Fragment {
    private NavigationView navigationView;

    private ImageView iBtnUsers, iBtnTeachers, iBtnSubjects, iBtnStudents, iBtnCourses, iBtnRoutine;
    private TextView mUsername, mEmail, mLogin ;
    private ImageView headernavimage;

    private BottomNavigationView bottomNavigationView;
    private UserLoginResponseEntity loginInstance;
    private ConstraintLayout mainLayout;

    private float[] lastTouchedXY = new float[2];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        SupportActionBarInitializer.setSupportActionBarTitle( ((AppCompatActivity) getActivity()).getSupportActionBar(),"Dashboard");

        bindViews(view);
        GetLoginInstanceFromDatabase loginInstanceFromDatabase=new GetLoginInstanceFromDatabase(getContext());
        loginInstance=loginInstanceFromDatabase.getLoginInstance();

        navigationView = getActivity().findViewById(R.id.nav_view);
        View mView = navigationView.getHeaderView(0);
        mUsername = mView.findViewById(R.id.username1);
        mEmail = mView.findViewById(R.id.email1);
        headernavimage = mView.findViewById(R.id.image);
        mLogin = mView.findViewById(R.id.loginfirst);
        mainLayout=view.findViewById(R.id.drawer_layout_dashboard);



        iBtnCourses.setOnClickListener(clickListener);
        iBtnRoutine.setOnClickListener(clickListener);
        iBtnStudents.setOnClickListener(clickListener);
        iBtnUsers.setOnClickListener(clickListener);
        iBtnSubjects.setOnClickListener(clickListener);
        iBtnTeachers.setOnClickListener(clickListener);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        return view;
    }

    private void bindViews(View view) {
        iBtnUsers = view.findViewById(R.id.users);
        iBtnSubjects = view.findViewById(R.id.subjects);
        iBtnStudents = view.findViewById(R.id.students);
        iBtnTeachers = view.findViewById(R.id.teachers);
        iBtnCourses = view.findViewById(R.id.courses1);
        iBtnRoutine = view.findViewById(R.id.routine);
        bottomNavigationView = view.findViewById(R.id.bottomnavigation);

    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {
                switch (item.getItemId()) {
                    case R.id.bottom_nav_home:
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, new MainFragment());
                        ft.commit();
                }
                return true;
            };


    View.OnClickListener clickListener = view -> {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        Fragment fragment = getFragmentFromView(view);
        if(fragment!=null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mainLayout.setVisibility(View.GONE);
                    ft.replace(R.id.content_frame,fragment);
                    ft.addToBackStack(null);
//                    ft.addToBackStack(DashboardFragment.class.getName());
                    ft.commit();
                }

            },100);
        }

    };

    public Fragment getFragmentFromView(View view){
        Fragment fragment = null;

        if(view.equals(iBtnCourses)){
            fragment = new CourseListFragment();
        }else if(view.equals(iBtnRoutine)) {
            fragment = new ClassRoutineFragment();
        }else if(view.equals(iBtnStudents)) {
            fragment = new StudentListFragment();
        }else if(view.equals(iBtnSubjects)) {
            fragment = new SubjectListFragment();
        }else if(view.equals(iBtnTeachers)) {
            fragment = new TeacherListFragment();
        }else if(view.equals(iBtnUsers)){
            fragment = new UserListFragment();
        }

        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        String username, email, imageUrl;
        username = loginInstance.getFirstName()+" "+loginInstance.getLastName();
        email = loginInstance.getEmail();
        imageUrl = loginInstance.getProfilePicture();
        if (LoginChecker.IsLoggedIn(getContext())) {
            NavmenuHideShow.showHideNavMenuAccordingToLoginRole(navigationView, loginInstance.getLoginType(),getContext());
            if (mUsername.getVisibility() != View.VISIBLE || mEmail.getVisibility() != View.VISIBLE) {
                mUsername.setVisibility(View.VISIBLE);
                mEmail.setVisibility(View.VISIBLE);
                mLogin.setVisibility(View.INVISIBLE);
            }


            mUsername.setText(username);
            mEmail.setText(email);

        } else {
            NavmenuHideShow.showHideNavMenuAccordingToLoginRole(navigationView, loginInstance.getLoginType(),getContext());
            if (mUsername.getVisibility() != View.INVISIBLE || mEmail.getVisibility() != View.INVISIBLE) {
                mUsername.setVisibility(View.INVISIBLE);
                mEmail.setVisibility(View.INVISIBLE);
            }
            if (mLogin.getVisibility() == View.INVISIBLE) {
                mLogin.setVisibility(View.VISIBLE);
            }
        }

        ImageViewLoader.loadImage(getContext(), imageUrl, headernavimage);

    }

}
