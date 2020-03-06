package com.imaginology.texas.Team.TeamDetails.ManupulateDtos;

import android.content.Intent;

import java.util.List;

public class MemberDto {
    private Integer id;
    private String type;
    private String fullName;
    private String contact;
    private Integer stumemmberId;
    private Integer teacherMemberId;
    private Integer userMemberId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStumemmberId() {
        return stumemmberId;
    }

    public void setStumemmberId(Integer stumemmberId) {
        this.stumemmberId = stumemmberId;
    }

    public Integer getTeacherMemberId() {
        return teacherMemberId;
    }

    public void setTeacherMemberId(Integer teacherMemberId) {
        this.teacherMemberId = teacherMemberId;
    }

    public Integer getUserMemberId() {
        return userMemberId;
    }

    public void setUserMemberId(Integer userMemberId) {
        this.userMemberId = userMemberId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
