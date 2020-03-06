package com.imaginology.texas.Students;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.Teachers.TeacherListFragment;
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

public class StudentListFragment extends Fragment implements SearchView.OnQueryTextListener, StudentDtoAdapter.ClickListener, View.OnClickListener,SwipeRefreshLayout.OnRefreshListener {
    private StudentDtoAdapter studentDtoAdapter;
    List<StudentListDto.Datum> studentListDto;
    StudentListDto studentListDtoResponse;
    private SearchView searchView;
    private ProgressBar progressBar;
    private ApiInterface apiInterface;
    private MenuItem search;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TextView noStuText;


    //Endless Scrolling in recyclerview
    private int page = 0;
    private boolean isScrolling = false;
    private int totalItems;
    private int totalVisiableItems;
    private int itemsOutsideScreen;
    private GetLoginInstanceFromDatabase loginInstanceAccessor;
    private UserLoginResponseEntity loginInstance;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        progressBar = view.findViewById(R.id.userlist_progressbar);
        swipeRefreshLayout=view.findViewById(R.id.swiperefreshstudent);
        swipeRefreshLayout.setOnRefreshListener(this);
        progressBar.setVisibility(View.VISIBLE);
        apiInterface = ApiClient.getRetrofit(getContext()).create(ApiInterface.class);
        SupportActionBarInitializer.setSupportActionBarTitle(((AppCompatActivity)getActivity()).getSupportActionBar(),"Students");
        recyclerView = view.findViewById(R.id.recyclerview);
        noStuText=view.findViewById(R.id.text_label);
        layoutManager=new LinearLayoutManager(getContext());
        studentDtoAdapter = new StudentDtoAdapter(R.layout.user_list_adapter, getContext());
        recyclerView.setAdapter(studentDtoAdapter);
        recyclerView.setLayoutManager(layoutManager);
        studentDtoAdapter.setStudentClickListener(StudentListFragment.this);
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


        //Getting loginInstance from database
        loginInstanceAccessor = new GetLoginInstanceFromDatabase(getContext());
        loginInstance = loginInstanceAccessor.getLoginInstance();


        Call<StudentListDto> call = apiInterface.listStudents(loginInstance.getCustomerId(),loginInstance.getToken(),
                loginInstance.getLoginId(),"firstName,asc",20,  page,"");
        call.enqueue(new Callback<StudentListDto>() {
            @Override
            public void onResponse(Call<StudentListDto> call, Response<StudentListDto> response) {
                int status = response.code();

                if (response.isSuccessful()) {
                    /*List<StudentListDto> studentListDtos;
                    for(StudentListDto x: response.body()){
                        studentListDtos.add(x.setFirstName());
                    }*/

                    studentListDtoResponse= response.body();
                    studentListDto = studentListDtoResponse.getData();
                    studentDtoAdapter.addStudentToList(studentListDto);
//                    studentDtoAdapter.addStudentToList(Arrays.asList(new ArrayDataForStudent().getData()));


                    if(studentDtoAdapter.getItemCount()==0 ){
                        search.setVisible(false);
                        noStuText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
//                        ShowNoContentPopUp.showMessage(getContext(),"There are no students available right now ");



                    } else {
                        search.setVisible(true);
                    }

                        progressBar.setVisibility(View.GONE);
                } else if (status==401){
                    StatusChecker.statusCheck(getContext());

                } else {
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
            public void onFailure(Call<StudentListDto> call, Throwable t) {
                Toast.makeText(getContext(), R.string.no_response, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void itemClicked(View v, StudentListDto.Datum studentlistDto, final int position) {

        String selectedUserId = studentlistDto.getId().toString();
        Intent studentDetailsActivity = new Intent(getContext(), StudentDetails.class);
        studentDetailsActivity.putExtra("selectedUserId", selectedUserId);
        studentDetailsActivity.putExtra("studentLoginId",(long) studentlistDto.getLoginId());
        startActivity(studentDetailsActivity);

    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //By uv to search students
        newText = newText.toLowerCase();
        List<StudentListDto.Datum> newList = new ArrayList<>();
        for (StudentListDto.Datum list : studentListDto) {
            String studentName = list.getFirstName().toLowerCase();
            String studentId = String.valueOf(list.getId());
            String studentLastName = list.getLastName().toLowerCase();
            if (studentName.contains(newText) || studentId.contains(newText) || studentLastName.contains(newText)) {
                newList.add(list);
            }
        }
        studentDtoAdapter.setFilter(newList);

        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Call<StudentListDto> call = apiInterface.listStudents(loginInstance.getCustomerId(), loginInstance.getToken(),
                loginInstance.getLoginId(), "firstName,asc", 20, page, query);
        String finalNewText = query;
        call.enqueue(new Callback<StudentListDto>() {
            @Override
            public void onResponse(Call<StudentListDto> call, Response<StudentListDto> response) {

                List<StudentListDto.Datum> newList = response.body().getData();
                for (StudentListDto.Datum list : studentListDto) {
                    String studentName = list.getFirstName().toLowerCase();
                    String studentId = String.valueOf(list.getId());
                    String studentLastName = list.getLastName().toLowerCase();
                    if (studentName.contains(finalNewText) || studentId.contains(finalNewText) || studentLastName.contains(finalNewText)) {
                        newList.add(list);
                    }
                }
//                studentDtoAdapter.setFilter(newList);

            }

            @Override
            public void onFailure(Call<StudentListDto> call, Throwable t) {
                Toast.makeText(getContext(), "No Student found", Toast.LENGTH_SHORT).show();

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
        ft.replace(R.id.content_frame, new StudentListFragment());
        ft.commit();
        swipeRefreshLayout.setRefreshing(false);
//        defineView(0);
    }

}
