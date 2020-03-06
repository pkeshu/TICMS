package com.imaginology.texas.Counseling.CreateStudentCounseling;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateStudentCounselingDto {
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
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;
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
    @SerializedName("totalFee")
    @Expose
    private Long totalFee;

    public CreateStudentCounselingDto(String address, String counseledBy, Integer courseId, String email, String fullName, String gender, String mobileNumber, String recommendedBy, String schoolName, Long totalFee) {
        this.address = address;
        this.counseledBy = counseledBy;
        this.courseId = courseId;
        this.email = email;
        this.fullName = fullName;
        this.gender = gender;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
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

    public Long getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Long totalFee) {
        this.totalFee = totalFee;
    }
}
