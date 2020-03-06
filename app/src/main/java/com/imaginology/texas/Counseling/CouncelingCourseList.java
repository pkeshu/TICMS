package com.imaginology.texas.Counseling;

import android.content.Context;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.imaginology.texas.Courses.CoursesDto;
//import com.imaginology.texas.Subjects.CourseDto;
import com.imaginology.texas.Error.ErrorMessageDto;
import com.imaginology.texas.R;
import com.imaginology.texas.SnackBar.CustomSnackbar;
import com.imaginology.texas.Subjects.CourseDto;
import com.imaginology.texas.service.ApiInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CouncelingCourseList {


    /*Long getCustomerId, userId;
    String token;*/

    public void listOfCourses(PopupMenu popupMenu, ApiInterface apiInterface, Long customerId, Long loginId, String token, Context context) {
        Call<List<CoursesDto>> call = apiInterface.getAllCourse(customerId,
                token,loginId);

        call.enqueue(new Callback<List<CoursesDto>>() {
            @Override
            public void onResponse(Call<List<CoursesDto>> call, Response<List<CoursesDto>> response) {
                if (response.isSuccessful()){
                    for (CoursesDto coursesDto: response.body()) {
                        popupMenu.getMenu().add(coursesDto.getId(), coursesDto.getId(), coursesDto.getId(),coursesDto.getName());
                    }
                } else {
                    //Maps the error message in ErrorMessageDto
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = null;
                    try {
                        mJson = parser.parse(response.errorBody().string());
                        Gson gson = new Gson();
                        ErrorMessageDto errorMessageDto = gson.fromJson(mJson, ErrorMessageDto.class);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CoursesDto>> call, Throwable t) {
                Toast.makeText(context, R.string.no_response, Toast.LENGTH_SHORT).show();
            }
        });
    }
}