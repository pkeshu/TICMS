package com.imaginology.texas.Team.AddingMemberInTeam;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddMemberInTeamDto {
    @SerializedName("studentId")
    @Expose
    private List<Integer> studentId = null;
    @SerializedName("teacherId")
    @Expose
    private List<Integer> teacherId = null;
    @SerializedName("userId")
    @Expose
    private List<Integer> userId = null;

    public AddMemberInTeamDto(List<Integer> studentId, List<Integer> teacherId, List<Integer> userId) {
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.userId = userId;
    }

    public List<Integer> getStudentId() {
        return studentId;
    }

    public void setStudentId(List<Integer> studentId) {
        this.studentId = studentId;
    }

    public List<Integer> getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(List<Integer> teacherId) {
        this.teacherId = teacherId;
    }

    public List<Integer> getUserId() {
        return userId;
    }

    public void setUserId(List<Integer> userId) {
        this.userId = userId;
    }
}
