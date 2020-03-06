package com.imaginology.texas.Teachers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imaginology.texas.MainFragment;
import com.imaginology.texas.Users.UserDetails;
import com.imaginology.texas.Users.UserListDto;
import com.imaginology.texas.util.ShowNoContentPopUp;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.util.CheckError;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.R;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;

import com.imaginology.texas.util.StatusChecker;

import com.imaginology.texas.util.SupportActionBarInitializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeacherListFragment extends Fragment implements SearchView.OnQueryTextListener, TeacherDtoAdapter.ClickListener, View.OnClickListener,SwipeRefreshLayout.OnRefreshListener {
    private TeacherDtoAdapter teacherDtoAdapter;
    private List<TeacherListDto.Datum> teacherListDto;
    private SearchView searchView;
    ProgressBar progressBar;
    ApiInterface apiInterface;
    private  UserLoginResponseEntity loginInstance;
    private MenuItem search;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    //Endless Scrolling in recyclerview
    private int page = 0;
    private boolean isScrolling = false;
    private int totalItems;
    private int totalVisiableItems;
    private int itemsOutsideScreen;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getRetrofit(getContext()).create(ApiInterface.class);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        progressBar = view.findViewById(R.id.userlist_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        //Getting loginInstance from database
        GetLoginInstanceFromDatabase loginInstanceAccessor = new GetLoginInstanceFromDatabase(getContext());
        loginInstance = loginInstanceAccessor.getLoginInstance();
        SupportActionBarInitializer.setSupportActionBarTitle(((AppCompatActivity)getActivity()).getSupportActionBar(),"Teachers");
        recyclerView = view.findViewById(R.id.recyclerview);
        layoutManager=new LinearLayoutManager(getContext());
        swipeRefreshLayout=view.findViewById(R.id.swiperefreshstudent);
        swipeRefreshLayout.setOnRefreshListener(this);
        teacherDtoAdapter = new TeacherDtoAdapter(R.layout.user_list_adapter, getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(teacherDtoAdapter);
        teacherDtoAdapter.setTeacherClickListener(TeacherListFragment.this);
        defineView(page);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isScrolling=true;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItems = layoutManager.getItemCount();
                totalVisiableItems = layoutManager.getChildCount();
                itemsOutsideScreen = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                if(isScrolling && (totalVisiableItems+itemsOutsideScreen == totalItems)
                        ){
                    isScrolling = false;
                    page=page+1;
                    defineView(page);
                }
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        search = menu.findItem(R.id.search);
        search.setVisible(false);

        searchView = (SearchView) search.getActionView();
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        super.onCreateOptionsMenu(menu, inflater);
    }
    private void defineView(int page) {
        progressBar.setVisibility(View.VISIBLE);


        Call<TeacherListDto> call = apiInterface.listTeachers(loginInstance.getLoginId(),loginInstance.getCustomerId(),
                "firstName,asc",20,page,"",loginInstance.getToken());
        call.enqueue(new Callback<TeacherListDto>() {
            @Override
            public void onResponse(Call<TeacherListDto> call, Response<TeacherListDto> response) {

                int status = response.code();

                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    TeacherListDto teacherResponseDto = response.body();
                    teacherListDto = teacherResponseDto.getData();
                    teacherDtoAdapter.addTeacherToList(teacherListDto);

                    if(teacherDtoAdapter.getItemCount()==0){
                        search.setVisible(false);
                        ShowNoContentPopUp.showMessage(getContext(),"There are no teachers available right now");
                    }else {
                        search.setVisible(true);
                    }
                    progressBar.setVisibility(View.GONE);

                } else if (status==401){
                    StatusChecker.statusCheck(getContext());

                } else {
                    progressBar.setVisibility(View.GONE);
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
                    swipeRefreshLayout.setRefreshing(true);

                }


            }

            @Override
            public void onFailure(Call<TeacherListDto> call, Throwable t) {
                Toast.makeText(getContext(), R.string.no_response, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void itemClicked(View v, TeacherListDto.Datum teacherlistDto, int position) {

        progressBar.setVisibility(View.VISIBLE);

        Intent detailsActivity = new Intent(getContext(), TeacherDetails.class);
        detailsActivity.putExtra("selectedTeacherId",teacherlistDto.getId().toString());
        detailsActivity.putExtra("selectedTeacherLoginId",(long)teacherlistDto.getLoginId());
        startActivity(detailsActivity);

        progressBar.setVisibility(View.GONE);

//        Call<TeacherDto> call = apiInterface.teacherInfo(loginInstance.getLoginId(),
//                loginInstance.getCustomerId(), Long.valueOf(teacherlistDto.getId()),
//                loginInstance.getToken(),);
//        Log.d("id===",teacherlistDto.getId().toString());
//        call.enqueue(new Callback<TeacherDto>() {
//            @Override
//            public void onResponse(Call<TeacherDto> call, Response<TeacherDto> response) {
//
//                int status = response.code();
//
//                if (response.isSuccessful()) {
//                    TeacherDto teacherDetails = response.body();
//                    Intent teacherDetailsIntentActivity = new Intent(getActivity().getApplicationContext(), TeacherDetails.class);
//                    teacherDetailsIntentActivity.putExtra("firstName", CheckError.checkNullString(teacherDetails.getFirstName()));
//                    teacherDetailsIntentActivity.putExtra("lastName",CheckError.checkNullString(teacherDetails.getLastName()));
//                    teacherDetailsIntentActivity.putExtra("middleName",teacherDetails.getMiddleName());
//                    teacherDetailsIntentActivity.putExtra("selectedTeacherId",teacherDetails.getId().toString());
//                    teacherDetailsIntentActivity.putExtra("email",CheckError.checkNullString(teacherDetails.getEmail()));
//                    teacherDetailsIntentActivity.putExtra("phone", CheckError.checkNullString(teacherDetails.getPhoneNumber()));
//                    teacherDetailsIntentActivity.putExtra("gender", CheckError.checkNullString(teacherDetails.getGender()));
//                    teacherDetailsIntentActivity.putExtra("zone",CheckError.checkNullString(teacherDetails.getAddresses().get(0).getZone()));
//                    teacherDetailsIntentActivity.putExtra("district", CheckError.checkNullString(teacherDetails.getAddresses().get(0).getDistrict()));
//                    teacherDetailsIntentActivity.putExtra("vdc",CheckError.checkNullString(teacherDetails.getAddresses().get(0).getVdc()));
//                    teacherDetailsIntentActivity.putExtra("ward",CheckError.checkNullString(teacherDetails.getAddresses().get(0).getWardNo()));
//                    teacherDetailsIntentActivity.putExtra("profilePicture", CheckError.checkNullString(teacherDetails.getProfilePicture()));
//                    startActivity(teacherDetailsIntentActivity);
//                    progressBar.setVisibility(View.GONE);
//                } else if (status==401){
//                    StatusChecker.statusCheck(getContext());
//
//                } else {
//                    progressBar.setVisibility(View.GONE);
//                    //Maps the error message in ErrorMessageDto
//                    JsonParser parser = new JsonParser();
//                    JsonElement mJson = null;
//                    try {
//                        mJson = parser.parse(response.errorBody().string());
//                        Gson gson = new Gson();
//                        ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
//                        Toast.makeText(getContext(), errorMessageDto.getMessage(), Toast.LENGTH_SHORT).show();
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<TeacherDto> call, Throwable t) {
//                Toast.makeText(getContext(), R.string.no_response, Toast.LENGTH_SHORT).show();
//                progressBar.setVisibility(View.VISIBLE);
//            }
//        });
    }



    @Override
    public boolean onQueryTextChange(String newText) {
        //By uv to search students
        newText = newText.toLowerCase();
//        if(newText==null){
        List<TeacherListDto.Datum> newList = new ArrayList<>();
                    for (TeacherListDto.Datum list : teacherListDto) {
                        String studentName = list.getFirstName().toLowerCase();
                        String studentId = String.valueOf(list.getId());
                        String studentLastName = list.getLastName().toLowerCase();
                        String userEmail=list.getEmail().toLowerCase();
                        if (studentName.contains(newText)  || studentId.contains(newText) || studentLastName.contains(newText) || userEmail.contains(newText)) {
                            newList.add(list);
                        }
                    }
                    teacherDtoAdapter.setFilter(newList);

//        }else {
//            Call<TeacherListDto> call = apiInterface.listTeachers(loginInstance.getLoginId(),loginInstance.getCustomerId(),
//                    "firstName,asc",20,page,newText,loginInstance.getToken());
//            String finalNewText = newText;
//            call.enqueue(new Callback<TeacherListDto>() {
//                @Override
//                public void onResponse(Call<TeacherListDto> call, Response<TeacherListDto> response) {
//                    List<TeacherListDto.Datum> uList;
//                    uList=response.body().getData();
//                    List<TeacherListDto.Datum> newList = new ArrayList<>();
//                    for (TeacherListDto.Datum list : uList) {
//                        String studentName = list.getFirstName().toLowerCase();
//                        String studentId = String.valueOf(list.getId());
//                        String studentLastName = list.getLastName().toLowerCase();
//                        String userEmail=list.getEmail().toLowerCase();
//                        if (studentName.contains(finalNewText)  || studentId.contains(finalNewText) || studentLastName.contains(finalNewText) || userEmail.contains(finalNewText)) {
//                            newList.add(list);
//                        }
//                    }
//                    teacherDtoAdapter.setFilter(uList);
//                }
//
//                @Override
//                public void onFailure(Call<TeacherListDto> call, Throwable t) {
//                    Toast.makeText(getContext(), "No Teacher found", Toast.LENGTH_SHORT).show();
//
//                }
//            });
//        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Call<TeacherListDto> call = apiInterface.listTeachers(loginInstance.getLoginId(),loginInstance.getCustomerId(),
                "firstName,asc",20,page,query,loginInstance.getToken());
        String finalNewText = query;
        call.enqueue(new Callback<TeacherListDto>() {
            @Override
            public void onResponse(Call<TeacherListDto> call, Response<TeacherListDto> response) {
                List<TeacherListDto.Datum> uList;
                uList=response.body().getData();
                List<TeacherListDto.Datum> newList = new ArrayList<>();
                for (TeacherListDto.Datum list : uList) {
                    String studentName = list.getFirstName().toLowerCase();
                    String studentId = String.valueOf(list.getId());
                    String studentLastName = list.getLastName().toLowerCase();
                    String userEmail=list.getEmail().toLowerCase();
                    if (studentName.contains(finalNewText)  || studentId.contains(finalNewText) || studentLastName.contains(finalNewText) || userEmail.contains(finalNewText)) {
                        newList.add(list);
                    }
                }
                teacherDtoAdapter.setFilter(uList);
            }

            @Override
            public void onFailure(Call<TeacherListDto> call, Throwable t) {
                Toast.makeText(getContext(), "No Teacher found", Toast.LENGTH_SHORT).show();

            }
        });

        return true;
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onRefresh() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, new TeacherListFragment());
        ft.commit();
        swipeRefreshLayout.setRefreshing(false);
//        defineView(0);



    }

}
