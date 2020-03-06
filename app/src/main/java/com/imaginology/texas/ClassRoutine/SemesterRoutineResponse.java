package com.imaginology.texas.ClassRoutine;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SemesterRoutineResponse  implements Serializable {
    @SerializedName("semester")
    @Expose
    private String semester;
    @SerializedName("routines")
    @Expose
    private List<ClassRoutineDto> routines = null;

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public List<ClassRoutineDto> getRoutines() {
        return routines;
    }

    public void setRoutines(List<ClassRoutineDto> routines) {
        this.routines = routines;
    }
}


