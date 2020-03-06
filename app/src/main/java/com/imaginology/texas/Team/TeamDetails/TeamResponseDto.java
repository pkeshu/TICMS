package com.imaginology.texas.Team.TeamDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TeamResponseDto {
    @SerializedName("teamId")
    @Expose
    private Integer teamId;
    @SerializedName("teamName")
    @Expose
    private String teamName;
    @SerializedName("createdBy")
    @Expose
    private Integer createdBy;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;
    @SerializedName("teamMembers")
    @Expose
    private List<TeamMemberDto> teamMembers = null;

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public List<TeamMemberDto> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<TeamMemberDto> teamMembers) {
        this.teamMembers = teamMembers;
    }
}
