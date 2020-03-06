package com.imaginology.texas.Team;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.SnackBar.CustomSnackbar;
import com.imaginology.texas.Team.TeamDetails.TeamDetails;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeamListFragment extends Fragment implements AllTeamAdapter.ClickListener{
    private RecyclerView recyclerView;
    private FloatingActionButton addTeamFlotBtn;
    private RecyclerView.LayoutManager layoutManager;
    private ApiInterface apiInterface;
    private GetLoginInstanceFromDatabase getLoginInstanceFromDatabase;
    private UserLoginResponseEntity loginInstance;
    private List<TeamDto.Content> contentsList = new ArrayList<>();
    private AllTeamAdapter allTeamAdapter;
    private CoordinatorLayout coordinatorLayout;
    private ProgressBar teamListProgress;


    public TeamListFragment() {
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
        View view = inflater.inflate(R.layout.fragment_add_team, container, false);
        recyclerView = view.findViewById(R.id.team_recyclerview);
        addTeamFlotBtn = view.findViewById(R.id.add_team_floating_btn);
        teamListProgress = view.findViewById(R.id.team_list_progress);
        addTeamFlotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewTeam();
            }
        });

        coordinatorLayout = view.findViewById(R.id.team_coordinator_layout);
        layoutManager = new LinearLayoutManager(getContext());
        apiInterface = ApiClient.getRetrofit(getContext()).create(ApiInterface.class);
        getLoginInstanceFromDatabase = new GetLoginInstanceFromDatabase(getContext());
        loginInstance = getLoginInstanceFromDatabase.getLoginInstance();
        allTeamAdapter = new AllTeamAdapter(getContext());
        allTeamAdapter.setTeamClickListener(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(allTeamAdapter);
        recyclerView.setLayoutManager(layoutManager);
        if(loginInstance.getUserRole().equals("USER"))
            addTeamFlotBtn.setVisibility(View.GONE);
        callServerForTeamList();
        return view;
    }

    public void addNewTeam() {
        recyclerView.setVisibility(View.GONE);
        addTeamFlotBtn.setVisibility(View.GONE);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.team_fragment_container, new AddNewTeamFragment());
        ft.addToBackStack("previous");
        ft.commit();

    }

    private void callServerForTeamList() {
        Call<TeamDto> call = apiInterface.getTeams(loginInstance.getCustomerId(),
                loginInstance.getToken(),
                loginInstance.getLoginId(), 0, 20, "id,asc", "");
        teamListProgress.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<TeamDto>() {
            @Override
            public void onResponse(Call<TeamDto> call, Response<TeamDto> response) {
                if (response.isSuccessful()) {
                    contentsList = response.body().getContents();
                    allTeamAdapter.addTeamToList(contentsList);
                    teamListProgress.setVisibility(View.GONE);
                    CustomSnackbar.showSuccessSnakeBar(coordinatorLayout, "You get all team list", getContext());

                } else {
                    //Maps the error message in ErrorMessageDto
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = null;
                    try {
                        mJson = parser.parse(response.errorBody().string());
                        Gson gson = new Gson();
                        ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                        CustomSnackbar.showFailureSnakeBar(coordinatorLayout, errorMessageDto.getMessage(), getContext());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    teamListProgress.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<TeamDto> call, Throwable t) {
                CustomSnackbar.checkErrorResponse(coordinatorLayout, getContext());
                teamListProgress.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public void itemClicked(View v, TeamDto.Content teamDto, int position) {
    int teamId=teamDto.getId();
        Intent teamDetailsIntent=new Intent(getContext(),TeamDetails.class);
        teamDetailsIntent.putExtra("teamId",teamId);
        teamDetailsIntent.putExtra("teamName",teamDto.getName());
        startActivity(teamDetailsIntent);
    }
}
