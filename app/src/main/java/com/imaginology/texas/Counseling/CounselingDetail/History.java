package com.imaginology.texas.Counseling.CounselingDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class History {
    @SerializedName("counseledDate")
    @Expose
    private String counseledDate;
    @SerializedName("counselledby")
    @Expose
    private String counselledby;
    @SerializedName("counsellerId")
    @Expose
    private Integer counsellerId;
    @SerializedName("profilePicture")
    @Expose
    private String profilePicture;
    @SerializedName("remarks")
    @Expose
    private String remarks;

    public String getCounseledDate() {
        return counseledDate;
    }

    public void setCounseledDate(String counseledDate) {
        this.counseledDate = counseledDate;
    }

    public String getCounselledby() {
        return counselledby;
    }

    public void setCounselledby(String counselledby) {
        this.counselledby = counselledby;
    }

    public Integer getCounsellerId() {
        return counsellerId;
    }

    public void setCounsellerId(Integer counsellerId) {
        this.counsellerId = counsellerId;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
