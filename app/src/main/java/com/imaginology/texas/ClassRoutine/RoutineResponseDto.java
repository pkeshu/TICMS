package com.imaginology.texas.ClassRoutine;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RoutineResponseDto implements Serializable{
    @SerializedName("courseId")
    @Expose
    private Integer courseId;
    @SerializedName("courseName")
    @Expose
    private String courseName;
    @SerializedName("semesterRoutineResponse")
    @Expose
    private List<SemesterRoutineResponse> semesterRoutineResponse = null;

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public List<SemesterRoutineResponse> getSemesterRoutineResponse() {
        return semesterRoutineResponse;
    }

    public void setSemesterRoutineResponse(List<SemesterRoutineResponse> semesterRoutineResponse) {
        this.semesterRoutineResponse = semesterRoutineResponse;
    }
}
