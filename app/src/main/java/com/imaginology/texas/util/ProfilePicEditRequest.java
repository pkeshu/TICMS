package com.imaginology.texas.util;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfilePicEditRequest {
    private String profilePic;

    public ProfilePicEditRequest(String profilePic){
        this.profilePic = profilePic;
    }
    public String getProfilePic(){
        return this.profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
