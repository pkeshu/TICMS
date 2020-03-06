package com.imaginology.texas.Team.TeamDetails;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.SnackBar.CustomSnackbar;
import com.imaginology.texas.Team.AddingMemberInTeam.AddMemberInTeamActivity;
import com.imaginology.texas.Team.TeamDetails.ManupulateDtos.MemberDto;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;
import com.imaginology.texas.util.SupportActionBarInitializer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamDetails extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TeamDetailsAdapter teamDetailsAdapter;
    private ApiInterface apiInterface;
    private int teamId;
    private UserLoginResponseEntity loginInstance;
    private ConstraintLayout mainLayout;
    private FloatingActionButton addMemberBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_details);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        apiInterface = ApiClient.getRetrofit(this).create(ApiInterface.class);
        mainLayout = findViewById(R.id.main_layout);
        recyclerView = findViewById(R.id.recyclerview);
        addMemberBtn = findViewById(R.id.add_member_btn);
        teamDetailsAdapter = new TeamDetailsAdapter();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(teamDetailsAdapter);
        Intent intent = getIntent();
        teamId = intent.getExtras().getInt("teamId");
        String teamName = intent.getExtras().getString("teamName");
        SupportActionBarInitializer.setUpSupportActionBar(getSupportActionBar(), teamName, true);
        GetLoginInstanceFromDatabase getLoginInstanceFromDatabase = new GetLoginInstanceFromDatabase(this);
        loginInstance = getLoginInstanceFromDatabase.getLoginInstance();
        callServerForMemberOfTeams(teamId);
        addMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(TeamDetails.this, AddMemberInTeamActivity.class);
//                intent.putExtra("teamId", teamId);
//                startActivity(intent);
//                finish();
                CustomSnackbar.showSuccessSnakeBar(mainLayout,"You can't Add members now",TeamDetails.this);
            }
        });

    }

    private void callServerForMemberOfTeams(int teamId) {

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
                            teamDetailsAdapter.addMemberInTeam(memberDtosAdp);
                            AddMemberInTeamActivity addMemberInTeamActivity = new AddMemberInTeamActivity();
                            addMemberInTeamActivity.getMemberIdFromTeam(memberDtosAdp);


                        } else {
                            CustomSnackbar.showSuccessSnakeBar(mainLayout, "You dont have any member", TeamDetails.this);
                        }

                    }


                }
            }

            @Override
            public void onFailure(Call<TeamResponseDto> call, Throwable t) {
                CustomSnackbar.checkErrorResponse(mainLayout, TeamDetails.this);
            }
        });

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
}
