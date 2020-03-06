package com.imaginology.texas.Team.AddingMemberInTeam;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.SnackBar.CustomSnackbar;
import com.imaginology.texas.Students.StudentListDto;
import com.imaginology.texas.Teachers.TeacherListDto;
import com.imaginology.texas.Team.TeamDetails.ManupulateDtos.MemberDto;
import com.imaginology.texas.Team.TeamDetails.TeamDetails;
import com.imaginology.texas.Team.TeamDetails.TeamMemberDto;
import com.imaginology.texas.Team.TeamDetails.TeamMembersListResponseDto;
import com.imaginology.texas.Team.TeamDetails.TeamResponseDto;
import com.imaginology.texas.Users.UserListDto;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;
import com.imaginology.texas.util.SupportActionBarInitializer;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMemberInTeamActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Toolbar toolbar;
    private Button stuBtn, teacherBtn, userBtn;
    private RecyclerView recyclerView;
    private ApiInterface apiInterface;
    private UserLoginResponseEntity loginInstance;
    private List<UserListDto.Datum> userList = new ArrayList<>();
    private List<StudentListDto.Datum> studentList = new ArrayList<>();
    private List<TeacherListDto.Datum> teacherList = new ArrayList<>();
    private AddUserInTeamAdapter addTeamMemberAdapter;
    private AddStudentInTeamAdapter addStudentInTeamAdapter;
    private AddTeacherInTeamAdapter addTeacherInTeamAdapter;
    private ConstraintLayout mainLayout;
    private FloatingActionButton addMemberToTeamBtn;
    private List<Integer> userIds = new ArrayList<>();
    private List<Integer> studentIds = new ArrayList<>();
    private List<Integer> teacherIds = new ArrayList<>();
    private int teamId;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    //Endless Scrolling in recyclerview
    private int page = 0;
    private boolean isScrolling = false;
    private int totalItems;
    private int totalVisiableItems;
    private int itemsOutsideScreen;
    private RecyclerView.LayoutManager layoutManager;
    private List<MemberDto> memberDtos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member_in_team);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SupportActionBarInitializer.setUpSupportActionBar(getSupportActionBar(), "Add Member", true);
        apiInterface = ApiClient.getRetrofit(this).create(ApiInterface.class);
        GetLoginInstanceFromDatabase getLoginInstanceFromDatabase = new GetLoginInstanceFromDatabase(this);
        loginInstance = getLoginInstanceFromDatabase.getLoginInstance();
        recyclerView = findViewById(R.id.recyclerview);
        stuBtn = findViewById(R.id.stu_btn);
        teacherBtn = findViewById(R.id.teacher_btn);
        userBtn = findViewById(R.id.user_btn);
        addMemberToTeamBtn = findViewById(R.id.add_member);
        mainLayout = findViewById(R.id.main_layout);
        progressBar = findViewById(R.id.progressbar);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        layoutManager = new LinearLayoutManager(this);
        Intent intent = getIntent();
        teamId = intent.getExtras().getInt("teamId");
        addTeamMemberAdapter = new AddUserInTeamAdapter(this);
        addStudentInTeamAdapter = new AddStudentInTeamAdapter(this);
        addTeacherInTeamAdapter = new AddTeacherInTeamAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(addStudentInTeamAdapter);
        getMemberId();
//        callServerForAllStudent(page);
//        stuBtn.setBackgroundColor(getResources().getColor(R.color.colorfade));
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
//                    isScrolling = true;
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                totalItems = layoutManager.getItemCount();
//                totalVisiableItems = layoutManager.getChildCount();
//                itemsOutsideScreen = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
//
//                if (isScrolling && (totalVisiableItems + itemsOutsideScreen == totalItems)
//                        ) {
//                    isScrolling = false;
//                    page = page + 1;
//                    callServerForAllStudent(page);
//                }
//            }
//
//
//        });
//        userBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                stuBtn.setBackgroundColor(getResources().getColor(R.color.colorWhite));
//                teacherBtn.setBackgroundColor(getResources().getColor(R.color.colorWhite));
//                userBtn.setBackgroundColor(getResources().getColor(R.color.colorfade));
//                recyclerView.setAdapter(addTeamMemberAdapter);
//                page = 0;
//                callServerForAllUser(page);
//                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                    @Override
//                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                        super.onScrollStateChanged(recyclerView, newState);
//                        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
//                            isScrolling = true;
//                    }
//
//                    @Override
//                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                        super.onScrolled(recyclerView, dx, dy);
//                        totalItems = layoutManager.getItemCount();
//                        totalVisiableItems = layoutManager.getChildCount();
//                        itemsOutsideScreen = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
//
//                        if (isScrolling && (totalVisiableItems + itemsOutsideScreen == totalItems)
//                                ) {
//                            isScrolling = false;
//                            page = page + 1;
//                            callServerForAllUser(page);
//                        }
//                    }
//
//
//                });
//            }
//        });
//        stuBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                userBtn.setBackgroundColor(getResources().getColor(R.color.colorWhite));
//                teacherBtn.setBackgroundColor(getResources().getColor(R.color.colorWhite));
//                stuBtn.setBackgroundColor(getResources().getColor(R.color.colorfade));
//                recyclerView.setAdapter(addStudentInTeamAdapter);
//                page = 0;
//                callServerForAllStudent(page);
//                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                    @Override
//                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                        super.onScrollStateChanged(recyclerView, newState);
//                        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
//                            isScrolling = true;
//                    }
//
//                    @Override
//                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                        super.onScrolled(recyclerView, dx, dy);
//                        totalItems = layoutManager.getItemCount();
//                        totalVisiableItems = layoutManager.getChildCount();
//                        itemsOutsideScreen = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
//
//                        if (isScrolling && (totalVisiableItems + itemsOutsideScreen == totalItems)
//                                ) {
//                            isScrolling = false;
//                            page = page + 1;
//                            callServerForAllStudent(page);
//                        }
//                    }
//
//
//                });
//            }
//        });
//        teacherBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                stuBtn.setBackgroundColor(getResources().getColor(R.color.colorWhite));
//                userBtn.setBackgroundColor(getResources().getColor(R.color.colorWhite));
//                teacherBtn.setBackgroundColor(getResources().getColor(R.color.colorfade));
//                recyclerView.setAdapter(addTeacherInTeamAdapter);
//                page = 0;
//                callServerForAllTeacher(page);
//                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                    @Override
//                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                        super.onScrollStateChanged(recyclerView, newState);
//                        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
//                            isScrolling = true;
//                    }
//
//                    @Override
//                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                        super.onScrolled(recyclerView, dx, dy);
//                        totalItems = layoutManager.getItemCount();
//                        totalVisiableItems = layoutManager.getChildCount();
//                        itemsOutsideScreen = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
//
//                        if (isScrolling && (totalVisiableItems + itemsOutsideScreen == totalItems)
//                                ) {
//                            isScrolling = false;
//                            page = page + 1;
//                            callServerForAllTeacher(page);
//                        }
//                    }
//
//
//                });
//            }
//        });


    }

    private void getMemberId() {
        Call<TeamResponseDto> call = apiInterface.getAllMemberFromTeam(loginInstance.getCustomerId(),
                loginInstance.getLoginId(),
                teamId,
                loginInstance.getToken()
        );
        call.enqueue(new Callback<TeamResponseDto>() {
            @Override
            public void onResponse(Call<TeamResponseDto> call, Response<TeamResponseDto> response) {
                List<MemberDto> memberDtosAdp = new ArrayList<>();
                int teacherId, studentId, userId;
                if (response.isSuccessful()) {
                    TeamResponseDto teamResponseDto = response.body();
                    if (teamResponseDto != null) {
                        if (!teamResponseDto.getTeamMembers().isEmpty()) {
                            for (TeamMemberDto teamMemberDto : teamResponseDto.getTeamMembers()) {
//
                                for (TeamMembersListResponseDto teamMembersListResponseDto : teamMemberDto.getTeamMembersListResponseDtos()) {
                                    //for student
                                    MemberDto stuMemberDto = new MemberDto();
                                    stuMemberDto.setType("Student");
                                    stuMemberDto.setFullName(teamMembersListResponseDto.getFullName());
                                    stuMemberDto.setContact(teamMembersListResponseDto.getPhoneNumber());
                                    stuMemberDto.setStumemmberId(teamMembersListResponseDto.getMemberId());
                                    //for Teacher
                                    MemberDto teacherMemberDto = new MemberDto();
                                    teacherMemberDto.setType("Teacher");
                                    teacherMemberDto.setFullName(teamMembersListResponseDto.getFullName());
                                    teacherMemberDto.setContact(teamMembersListResponseDto.getPhoneNumber());
                                    teacherMemberDto.setTeacherMemberId(teamMembersListResponseDto.getMemberId());
                                    //for User
                                    MemberDto userMemberDto = new MemberDto();
                                    userMemberDto.setType("User");
                                    userMemberDto.setFullName(teamMembersListResponseDto.getFullName());
                                    userMemberDto.setContact(teamMembersListResponseDto.getPhoneNumber());
                                    userMemberDto.setUserMemberId(teamMembersListResponseDto.getMemberId());
                                    //adding Member to list
                                    if (teamMemberDto.getType().equalsIgnoreCase("Student"))
                                        memberDtosAdp.add(stuMemberDto);
                                    if (teamMemberDto.getType().equalsIgnoreCase("Teacher"))
                                        memberDtosAdp.add(teacherMemberDto);
                                    if (teamMemberDto.getType().equalsIgnoreCase("User"))
                                        memberDtosAdp.add(userMemberDto);


                                }


                            }
                            List<Integer> studentIds = new ArrayList<>();
                            List<Integer> teacherIds = new ArrayList<>();
                            List<Integer> userIds = new ArrayList<>();

                            for (MemberDto memberDto : memberDtosAdp) {
                                if (memberDto.getType().equalsIgnoreCase("Student"))
                                    studentIds.add(memberDto.getStumemmberId());
                                if (memberDto.getType().equalsIgnoreCase("Teacher"))
                                    teacherIds.add(memberDto.getTeacherMemberId());
                                if (memberDto.getType().equalsIgnoreCase("User"))
                                    userIds.add(memberDto.getUserMemberId());
                            }
                            callServerForAllStudent(page,studentIds,teacherIds,userIds);
                            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                    super.onScrollStateChanged(recyclerView, newState);
                                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                                        isScrolling = true;
                                }

                                @Override
                                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);
                                    totalItems = layoutManager.getItemCount();
                                    totalVisiableItems = layoutManager.getChildCount();
                                    itemsOutsideScreen = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                                    if (isScrolling && (totalVisiableItems + itemsOutsideScreen == totalItems)
                                            ) {
                                        isScrolling = false;
                                        page = page + 1;
                                        callServerForAllStudent(page,studentIds,teacherIds,userIds);
                                    }
                                }


                            });
                            stuBtn.setBackgroundColor(getResources().getColor(R.color.colorfade));
                            userBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    stuBtn.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                                    teacherBtn.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                                    userBtn.setBackgroundColor(getResources().getColor(R.color.colorfade));
                                    recyclerView.setAdapter(addTeamMemberAdapter);
                                    page = 0;
                                    callServerForAllUser(page,studentIds,teacherIds,userIds);
                                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                        @Override
                                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                            super.onScrollStateChanged(recyclerView, newState);
                                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                                                isScrolling = true;
                                        }

                                        @Override
                                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                            super.onScrolled(recyclerView, dx, dy);
                                            totalItems = layoutManager.getItemCount();
                                            totalVisiableItems = layoutManager.getChildCount();
                                            itemsOutsideScreen = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                                            if (isScrolling && (totalVisiableItems + itemsOutsideScreen == totalItems)
                                                    ) {
                                                isScrolling = false;
                                                page = page + 1;
                                                callServerForAllUser(page,studentIds,teacherIds,userIds);
                                            }
                                        }


                                    });
                                }
                            });
                            stuBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    userBtn.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                                    teacherBtn.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                                    stuBtn.setBackgroundColor(getResources().getColor(R.color.colorfade));
                                    recyclerView.setAdapter(addStudentInTeamAdapter);
                                    page = 0;
                                    callServerForAllUser(page,studentIds,teacherIds,userIds);
                                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                        @Override
                                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                            super.onScrollStateChanged(recyclerView, newState);
                                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                                                isScrolling = true;
                                        }

                                        @Override
                                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                            super.onScrolled(recyclerView, dx, dy);
                                            totalItems = layoutManager.getItemCount();
                                            totalVisiableItems = layoutManager.getChildCount();
                                            itemsOutsideScreen = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                                            if (isScrolling && (totalVisiableItems + itemsOutsideScreen == totalItems)
                                                    ) {
                                                isScrolling = false;
                                                page = page + 1;
                                                callServerForAllUser(page,studentIds,teacherIds,userIds);
                                            }
                                        }


                                    });
                                }
                            });
                            teacherBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    stuBtn.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                                    userBtn.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                                    teacherBtn.setBackgroundColor(getResources().getColor(R.color.colorfade));
                                    recyclerView.setAdapter(addTeacherInTeamAdapter);
                                    page = 0;
                                    callServerForAllTeacher(page,studentIds,teacherIds,userIds);
                                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                        @Override
                                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                            super.onScrollStateChanged(recyclerView, newState);
                                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                                                isScrolling = true;
                                        }

                                        @Override
                                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                            super.onScrolled(recyclerView, dx, dy);
                                            totalItems = layoutManager.getItemCount();
                                            totalVisiableItems = layoutManager.getChildCount();
                                            itemsOutsideScreen = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                                            if (isScrolling && (totalVisiableItems + itemsOutsideScreen == totalItems)
                                                    ) {
                                                isScrolling = false;
                                                page = page + 1;
                                                callServerForAllTeacher(page,studentIds,teacherIds,userIds);
                                            }
                                        }


                                    });
                                }
                            });

                        } else {
//                            CustomSnackbar.showSuccessSnakeBar(mainLayout, "You dont have any member", TeamDetails.this);
                        }

                    }


                }
            }

            @Override
            public void onFailure(Call<TeamResponseDto> call, Throwable t) {
//                CustomSnackbar.checkErrorResponse(mainLayout, TeamDetails.this);
            }
        });
    }

    public void getMemberIdFromTeam(List<MemberDto> newMemberList) {
        memberDtos.addAll(newMemberList);
    }

    public AddMemberInTeamActivity() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void callServerForAllUser(int page,List<Integer> studentIds,List<Integer> teacherIds,List<Integer> userIds) {

        Call<UserListDto> call = apiInterface.listUsers(loginInstance.getLoginId(),
                loginInstance.getCustomerId(),
                loginInstance.getToken(),
                "firstName,asc",
                20, page, "");
        progressBar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<UserListDto>() {
            @Override
            public void onResponse(Call<UserListDto> call, Response<UserListDto> response) {

                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    userList = response.body().getData();
                    addTeamMemberAdapter.addUserToLIst(userList);
//                    CustomSnackbar.showSuccessSnakeBar(mainLayout, "You get all User", AddMemberInTeamActivity.this);
                    addMemberToTeamBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for (UserListDto.Datum data : addTeamMemberAdapter.checkedLIst) {
                                userIds.add(data.getLoginId());
                            }
                            if (addTeamMemberAdapter.checkedLIst.size() > 0) {
                                addMemberToTeam(userIds, studentIds, teacherIds);
                            } else {
                                Toast.makeText(AddMemberInTeamActivity.this, "Please select player", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
//                    swipeRefreshLayout.setRefreshing(true);

                }

            }

            @Override
            public void onFailure(Call<UserListDto> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                CustomSnackbar.checkErrorResponse(mainLayout, AddMemberInTeamActivity.this);

            }
        });
    }

    private void callServerForAllTeacher(int page,List<Integer> studentIds,List<Integer> teacherIds,List<Integer> userIds) {
        Call<TeacherListDto> call = apiInterface.listTeachers(loginInstance.getLoginId(),
                loginInstance.getCustomerId(),
                "firstName,asc",
                20, page, "",
                loginInstance.getToken());
        progressBar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<TeacherListDto>() {
            @Override
            public void onResponse(Call<TeacherListDto> call, Response<TeacherListDto> response) {

                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    addTeacherInTeamAdapter.addTeacherToLIst(response.body().getData());
//                    CustomSnackbar.showSuccessSnakeBar(mainLayout, "You get all Teacher", AddMemberInTeamActivity.this);
                    addMemberToTeamBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for (TeacherListDto.Datum data : addTeacherInTeamAdapter.checkedLIst) {
                                teacherIds.add(data.getLoginId());
                            }
                            if (addTeacherInTeamAdapter.checkedLIst.size() > 0) {
                                addMemberToTeam(userIds, studentIds, teacherIds);
                            } else {
                                Toast.makeText(AddMemberInTeamActivity.this, "Please select Member", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
//                    swipeRefreshLayout.setRefreshing(true);

                }

            }

            @Override
            public void onFailure(Call<TeacherListDto> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                CustomSnackbar.checkErrorResponse(mainLayout, AddMemberInTeamActivity.this);

            }
        });


    }

    private void callServerForAllStudent(int page,List<Integer> studentIds,List<Integer> teacherIds,List<Integer> userIds) {

        Call<StudentListDto> call = apiInterface.listStudents(loginInstance.getCustomerId(), loginInstance.getToken(),
                loginInstance.getLoginId(), "firstName,asc", 20, page, "");
        progressBar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<StudentListDto>() {
            @Override
            public void onResponse(Call<StudentListDto> call, Response<StudentListDto> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    addStudentInTeamAdapter.addStudentToLIst(response.body().getData());
                    addStudentInTeamAdapter.addCheckedItemToList(studentIds);
//                    CustomSnackbar.showSuccessSnakeBar(mainLayout, "You get all student", AddMemberInTeamActivity.this);
                    addMemberToTeamBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for (StudentListDto.Datum data : addStudentInTeamAdapter.checkedLIst) {
                                studentIds.add(data.getLoginId());
                            }
                            if (addStudentInTeamAdapter.checkedLIst.size() > 0) {
                                addMemberToTeam(userIds, studentIds, teacherIds);
                            } else {
                                Toast.makeText(AddMemberInTeamActivity.this, "Please select member", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
//                    swipeRefreshLayout.setRefreshing(true);

                }

            }

            @Override
            public void onFailure(Call<StudentListDto> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                CustomSnackbar.checkErrorResponse(mainLayout, AddMemberInTeamActivity.this);

            }
        });
    }

    private void addMemberToTeam(List<Integer> userIds, List<Integer> studentIds, List<Integer> teacherIds) {
        AddMemberInTeamDto addMemberInTeamDto = new AddMemberInTeamDto(studentIds, teacherIds, userIds);
        Call<ResponseBody> call = apiInterface.addMemberToTeam(loginInstance.getCustomerId(),
                loginInstance.getLoginId(),
                teamId, addMemberInTeamDto, loginInstance.getToken());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddMemberInTeamActivity.this, "Member is added successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddMemberInTeamActivity.this, TeamDetails.class);
                    intent.putExtra("teamId", teamId);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CustomSnackbar.checkErrorResponse(mainLayout, AddMemberInTeamActivity.this);

            }
        });
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);


    }


}
