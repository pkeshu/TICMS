package com.imaginology.texas.Counseling.EditCounseling;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EditCounselingDto {
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("counseledBy")
    @Expose
    private String counseledBy;
    @SerializedName("courseId")
    @Expose
    private Integer courseId;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("fullName")
    @Expose
    private String fullName;
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;
    @SerializedName("recommendedBy")
    @Expose
    private String recommendedBy;
    @SerializedName("remarks")
    @Expose
    private String remarks;
    @SerializedName("schoolName")
    @Expose
    private String schoolName;
    @SerializedName("stageType")
    @Expose
    private String stageType;
    @SerializedName("totalFee")
    @Expose
    private Long totalFee;

    public EditCounselingDto(String address, String counseledBy, String email, String fullName, Long id, String mobileNumber, String recommendedBy, String schoolName, Long totalFee) {
        this.address = address;
        this.counseledBy = counseledBy;
        this.email = email;
        this.fullName = fullName;
        this.id = id;
        this.mobileNumber = mobileNumber;
        this.recommendedBy = recommendedBy;
        this.schoolName = schoolName;
        this.totalFee = totalFee;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCounseledBy() {
        return counseledBy;
    }

    public void setCounseledBy(String counseledBy) {
        this.counseledBy = counseledBy;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getRecommendedBy() {
        return recommendedBy;
    }

    public void setRecommendedBy(String recommendedBy) {
        this.recommendedBy = recommendedBy;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getStageType() {
        return stageType;
    }

    public void setStageType(String stageType) {
        this.stageType = stageType;
    }

    public Long getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Long totalFee) {
        this.totalFee = totalFee;
    }
}
