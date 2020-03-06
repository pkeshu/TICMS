package com.imaginology.texas.Users;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.Students.StudentListFragment;
import com.imaginology.texas.Teachers.TeacherListFragment;
import com.imaginology.texas.util.ShowNoContentPopUp;
import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;

import com.imaginology.texas.util.StatusChecker;
import com.imaginology.texas.util.SupportActionBarInitializer;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListFragment extends Fragment implements SearchView.OnQueryTextListener, UserDtoAdapter.ClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private UserDtoAdapter userDtoAdapter;
    UserListDto userListDtoResponse;
    List<UserListDto.Datum> userListDto;
    private SearchView searchView;
    private ProgressBar progressBar;
    private ApiInterface apiInterface;
    private MenuItem search;
    private GetLoginInstanceFromDatabase loginInstanceAccessor;
    private UserLoginResponseEntity loginInstance;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.LayoutManager layoutManager;


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
        SupportActionBarInitializer.setSupportActionBarTitle(((AppCompatActivity)getActivity()).getSupportActionBar(),"Users");
        recyclerView = view.findViewById(R.id.recyclerview);
        layoutManager=new LinearLayoutManager(getContext());
        progressBar = view.findViewById(R.id.userlist_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout=view.findViewById(R.id.swiperefreshstudent);
        swipeRefreshLayout.setOnRefreshListener(this);
        userDtoAdapter = new UserDtoAdapter(R.layout.user_list_adapter, getContext());
        recyclerView.setAdapter(userDtoAdapter);
        recyclerView.setLayoutManager(layoutManager);
        userDtoAdapter.setUserClickListener(UserListFragment.this);
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
/*

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.activity_menu_item:

                // Not implemented here
                return false;
            case R.id.fragment_menu_item:

                // Do Fragment menu item stuff here
                return true;

            default:
                break;
        }

        return false;
    }
*/

    private void defineView(int page) {
        progressBar.setVisibility(View.VISIBLE);


        //Getting loginInstance from database
        loginInstanceAccessor = new GetLoginInstanceFromDatabase(getContext());
        loginInstance = loginInstanceAccessor.getLoginInstance();

        if(loginInstance!=null){
            Call<UserListDto> call = apiInterface.listUsers(loginInstance.getLoginId(),
            loginInstance.getCustomerId(),loginInstance.getToken(), "firstName,asc", 20, page, "");
            call.enqueue(new Callback<UserListDto>() {
                @Override
                public void onResponse(Call<UserListDto> call, Response<UserListDto> response) {

                    int status = response.code();




                    if (response.isSuccessful()) {
                        userListDtoResponse = response.body();//.getContents();
                        userListDto=userListDtoResponse.getData();
                        userDtoAdapter.addUserToList(userListDto);
//                        Toast.makeText(getContext(), userDtoAdapter.getItemCount(), Toast.LENGTH_SHORT).show();

                        if(userDtoAdapter.getItemCount()==0){
                            ShowNoContentPopUp.showMessage(getContext(),"There are no users available right now.");
                            search.setVisible(false);

                        }else{
                            search.setVisible(true);

                        }
                        progressBar.setVisibility(View.GONE);
                    }else if (status==401){
                        StatusChecker.statusCheck(getContext());

                    }

                    else {
                        //Maps the error message in ErrorMessageDto
                        progressBar.setVisibility(View.GONE);
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
                public void onFailure(Call<UserListDto> call, Throwable t) {
                    Toast.makeText(getContext(), R.string.no_response, Toast.LENGTH_SHORT).show();
                    Log.d("Error message===",t.getMessage());
                    progressBar.setVisibility(View.GONE);
                }
            });
        }



    }


    @Override
    public void itemClicked(View v, UserListDto.Datum userlistDto, int position) {

        progressBar.setVisibility(View.VISIBLE);

        Intent detailsActivity = new Intent(getContext(), UserDetails.class);
        detailsActivity.putExtra("userId",userlistDto.getId());
        detailsActivity.putExtra("profileId",(long) userlistDto.getLoginId());
        startActivity(detailsActivity);

        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //By uv to search students
        newText = newText.toLowerCase();
        List<UserListDto.Datum> newList = new ArrayList<>();
                        for (UserListDto.Datum list : userListDto) {
                            String studentName = list.getFirstName().toLowerCase();
                            String studentId = String.valueOf(list.getId());
                            String studentLastName = list.getLastName().toLowerCase();
                            String userEmail=list.getEmail().toLowerCase();
                            if (studentName.contains(newText)  || studentId.contains(newText) || studentLastName.contains(newText) || userEmail.contains(newText)) {
                                newList.add(list);
                            }
                        }
        userDtoAdapter.setFilter(newList);
//        if(newText==null){
//            userDtoAdapter.setFilter(userListDto);
//
//        }else {
//            Call<UserListDto> call = apiInterface.listUsers(loginInstance.getLoginId(),
//                    loginInstance.getCustomerId(),loginInstance.getToken(), "firstName,asc", 20, page, newText);
//            String finalNewText = newText;
//            call.enqueue(new Callback<UserListDto>() {
//                @Override
//                public void onResponse(Call<UserListDto> call, Response<UserListDto> response) {
//                    if(response.isSuccessful()){
//                        List<UserListDto.Datum> uList;
//                        uList=response.body().getData();
//                        List<UserListDto.Datum> newList = new ArrayList<>();
//                        for (UserListDto.Datum list : uList) {
//                            String studentName = list.getFirstName().toLowerCase();
//                            String studentId = String.valueOf(list.getId());
//                            String studentLastName = list.getLastName().toLowerCase();
//                            String userEmail=list.getEmail().toLowerCase();
//                            if (studentName.contains(finalNewText)  || studentId.contains(finalNewText) || studentLastName.contains(finalNewText) || userEmail.contains(finalNewText)) {
//                                newList.add(list);
//                            }
//                        }
//                        userDtoAdapter.setFilter(uList);
//                    }
//
//                }
//
//                @Override
//                public void onFailure(Call<UserListDto> call, Throwable t) {
//                    Toast.makeText(getContext(), "No user found", Toast.LENGTH_SHORT).show();
//
//                }
//            });
//        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Call<UserListDto> call = apiInterface.listUsers(loginInstance.getLoginId(),
                loginInstance.getCustomerId(),loginInstance.getToken(), "firstName,asc", 20, page, query);
        String finalNewText = query;
        call.enqueue(new Callback<UserListDto>() {
            @Override
            public void onResponse(Call<UserListDto> call, Response<UserListDto> response) {
                if(response.isSuccessful()){
                    List<UserListDto.Datum> uList;
                    uList=response.body().getData();
                    List<UserListDto.Datum> newList = new ArrayList<>();
                    for (UserListDto.Datum list : uList) {
                        String studentName = list.getFirstName().toLowerCase();
                        String studentId = String.valueOf(list.getId());
                        String studentLastName = list.getLastName().toLowerCase();
                        String userEmail=list.getEmail().toLowerCase();
                        if (studentName.contains(finalNewText)  || studentId.contains(finalNewText) || studentLastName.contains(finalNewText) || userEmail.contains(finalNewText)) {
                            newList.add(list);
                        }
                    }
                    userDtoAdapter.setFilter(uList);
                }

            }

            @Override
            public void onFailure(Call<UserListDto> call, Throwable t) {
                Toast.makeText(getContext(), "No user found", Toast.LENGTH_SHORT).show();

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
        ft.replace(R.id.content_frame, new UserListFragment());
        ft.commit();
        swipeRefreshLayout.setRefreshing(false);
//        defineView(0);

    }
}
