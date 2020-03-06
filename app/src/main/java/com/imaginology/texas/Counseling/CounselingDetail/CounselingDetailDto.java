package com.imaginology.texas.Counseling.CounselingDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CounselingDetailDto {
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("counseledBy")
    @Expose
    private String counseledBy;
    @SerializedName("courseId")
    @Expose
    private Integer courseId;
    @SerializedName("courseName")
    @Expose
    private String courseName;
    @SerializedName("createdByName")
    @Expose
    private String createdByName;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;
    @SerializedName("customerId")
    @Expose
    private Integer customerId;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("fullName")
    @Expose
    private String fullName;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("histories")
    @Expose
    private List<History> histories = null;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;
    @SerializedName("modifiedBy")
    @Expose
    private Integer modifiedBy;
    @SerializedName("modifiedDate")
    @Expose
    private String modifiedDate;
    @SerializedName("discountFee")
    @Expose
    private Long negotiatedFee;
    @SerializedName("profilePicture")
    @Expose
    private String profilePicture;
    @SerializedName("recommendedBy")
    @Expose
    private String recommendedBy;
    @SerializedName("remarks")
    @Expose
    private String remarks;
    @SerializedName("schoolName")
    @Expose
    private String schoolName;
    @SerializedName("stage")
    @Expose
    private String stage;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("totalFee")
    @Expose
    private Integer totalFee;

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

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<History> getHistories() {
        return histories;
    }

    public void setHistories(List<History> histories) {
        this.histories = histories;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Integer getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Integer modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Long getNegotiatedFee() {
        return negotiatedFee;
    }

    public void setNegotiatedFee(Long negotiatedFee) {
        this.negotiatedFee = negotiatedFee;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
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

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Integer totalFee) {
        this.totalFee = totalFee;
    }

}

