package com.imaginology.texas.Team;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.MainActivity;
import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.SnackBar.CustomSnackbar;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;
import com.imaginology.texas.util.SupportActionBarInitializer;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddNewTeamFragment extends Fragment {

    private EditText teamName, teamDescription;
    private Spinner teamTypeSpnr;
    private TextView teamCreate;
    ApiInterface apiInterface;
    private UserLoginResponseEntity loginInstance;
    private final String[] type = {"Select Type","TEACHER", "STUDENT", "ACCOUNT", "FRONT_DESK", "COUNSELLING", "ADMIN", "USER"};
    String tType, tDescription, tName;
    private ConstraintLayout mainLayout;


    public AddNewTeamFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetLoginInstanceFromDatabase databaseInstanceAccessor = new GetLoginInstanceFromDatabase(getContext());
        loginInstance = databaseInstanceAccessor.getLoginInstance();
        apiInterface = ApiClient.getRetrofit(getContext()).create(ApiInterface.class);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_new_team, container, false);
//        SupportActionBarInitializer.setSupportActionBarTitle(((AppCompatActivity) getActivity()).getSupportActionBar(), "Create Team");
        teamName = view.findViewById(R.id.team_name);
        teamDescription = view.findViewById(R.id.team_description);
        teamTypeSpnr = view.findViewById(R.id.team_type_spnr);
        mainLayout = view.findViewById(R.id.main_layout_team);
        teamCreate = view.findViewById(R.id.create_txt);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_textview_select_semester, type);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teamTypeSpnr.setAdapter(adapter);
        getDetailsForNewTeam();
        return view;
    }

    private void getDetailsForNewTeam() {
        teamTypeSpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                tType = teamTypeSpnr.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        teamCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tName = teamName.getText().toString();
                tDescription = teamDescription.getText().toString();
                if (tName.length() > 0 && tDescription.length() > 0 && !tType.equals("Select Type"))
                    createTeamInServer(tName, tDescription, tType);
                else {
                    if (tName.isEmpty())
                        teamName.setError("Required");
                    if (tDescription.isEmpty())
                        teamDescription.setError("Required");
                    if(tType.equals("Select Type"))
                        CustomSnackbar.showFailureSnakeBar(mainLayout,"Select Type",getContext());
                }
            }
        });


    }

    private void createTeamInServer(String tName, String tDescription, String tType) {

        TeamCreationRequestDto teamCreationRequestDto = new TeamCreationRequestDto(tDescription, tName, tType);

        Call<ResponseBody> call = apiInterface.createTeams(loginInstance.getCustomerId(), loginInstance.getLoginId(),
                teamCreationRequestDto, loginInstance.getToken());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    CustomSnackbar.showSuccessSnakeBar(mainLayout, "Team Successfully Created", getContext());
//                    mainLayout.setVisibility(View.GONE);
//                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
////                    fragmentTransaction.remove(AddNewTeamFragment.this);
//                    fragmentTransaction.replace(R.id.new_team_framelauout, new TeamListFragment());
//                    fragmentTransaction.commit();
                    getActivity().startActivity(new Intent(getContext(),MainActivity.class));
                    getActivity().finish();
                } else {
                    //Maps the error message in ErrorMessageDto
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = null;
                    try {
                        mJson = parser.parse(response.errorBody().string());
                        Gson gson = new Gson();
                        ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                        CustomSnackbar.showFailureSnakeBar(mainLayout, errorMessageDto.getMessage(), getContext());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CustomSnackbar.checkErrorResponse(mainLayout, getContext());

            }
        });

    }


}
