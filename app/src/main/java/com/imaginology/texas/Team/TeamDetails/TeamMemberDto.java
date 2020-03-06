package com.imaginology.texas.Team.TeamDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TeamMemberDto {
    @SerializedName("type")
    @Expose
    private String  type;
    @SerializedName("teamMembersListResponseDto")
    @Expose
    private List<TeamMembersListResponseDto> teamMembersListResponseDtos;

    public List<TeamMembersListResponseDto> getTeamMembersListResponseDtos() {
        return teamMembersListResponseDtos;
    }

    public void setTeamMembersListResponseDtos(List<TeamMembersListResponseDto> teamMembersListResponseDtos) {
        this.teamMembersListResponseDtos = teamMembersListResponseDtos;
    }

    public String getType() {
        return type;
    }

    public void setType(String  type) {
        this.type = type;
    }

}
