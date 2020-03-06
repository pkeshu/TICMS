package com.imaginology.texas.Counseling.ProcessCounseling;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imaginology.texas.Counseling.CounselingDetail.CounselingDetail;
import com.imaginology.texas.Counseling.CounselingDetail.CounselingDetailDto;
import com.imaginology.texas.Counseling.CounselingDetail.History;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.service.ApiClient;
import com.imaginology.texas.service.ApiInterface;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView backTxt;
    private Long counselingId;
    private CounselHistoryAdapter counselHistoryAdapter;
    private ApiInterface apiInterface;
    private UserLoginResponseEntity loginInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_history);
        recyclerView=findViewById(R.id.recyclerview);
        backTxt=findViewById(R.id.back);

        //get Intent value
        Intent intent=getIntent();
        counselingId=intent.getExtras().getLong("selectedCounsellingId");
        counselHistoryAdapter=new CounselHistoryAdapter(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(counselHistoryAdapter);
        apiInterface=ApiClient.getRetrofit(this).create(ApiInterface.class);
        loginInstance=new GetLoginInstanceFromDatabase(this).getLoginInstance();
        showHistory();
        backTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent=new Intent(ShowHistoryActivity.this,CounselingDetail.class);
                backIntent.putExtra("selectedCounsellingId",counselingId);
                startActivity(backIntent);
                finish();
            }
        });

    }

    private void showHistory() {
        Call<CounselingDetailDto> call = apiInterface.counsellingInfo(loginInstance.getCustomerId(), loginInstance.getLoginId(), counselingId,
                loginInstance.getToken());
        call.enqueue(new Callback<CounselingDetailDto>() {
            @Override
            public void onResponse(Call<CounselingDetailDto> call, Response<CounselingDetailDto> response) {
                if(response.isSuccessful()){
                    List<History> historyList=response.body().getHistories();
                    counselHistoryAdapter.addHistoryInList(historyList);
                }
                else {
                    //Maps the error message in ErrorMessageDto
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = null;
                    try {
                        mJson = parser.parse(response.errorBody().string());
                        Gson gson = new Gson();
                        ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                        Toast.makeText(getApplicationContext(), errorMessageDto.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CounselingDetailDto> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.no_response, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
