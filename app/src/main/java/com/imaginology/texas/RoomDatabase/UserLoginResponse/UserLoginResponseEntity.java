package com.imaginology.texas.RoomDatabase.UserLoginResponse;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "login_instance")
public class UserLoginResponseEntity {
    @PrimaryKey
   private Long id;

   private Long userId;
   private Long loginId;
   private Long customerId;
   private String firstName;
   private String lastName;
   private String email;
   private String token;
   private String userRole;
   private String loginType;
   private String deviceId;
   private String userName;
   private String profilePicture;
   private String status;
   private String mobileNumber;
   private String gender;
   private Long courseId;
   private String courseName;


    public UserLoginResponseEntity() {
    }

    public UserLoginResponseEntity(Long userId, Long loginId, Long customerId,
                                   String firstName, String lastName, String email, String token,
                                   String userRole, String loginType, String deviceId, String userName,
                                   String profilePicture, String status, String mobileNumber, String gender,
                                   Long courseId, String courseName) {
        this.userId = userId;
        this.loginId = loginId;
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.token = token;
        this.userRole = userRole;
        this.loginType = loginType;
        this.deviceId = deviceId;
        this.userName = userName;
        this.profilePicture = profilePicture;
        this.status = status;
        this.mobileNumber=mobileNumber;
        this.gender=gender;
        this.courseId = courseId;
        this.courseName = courseName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getLoginId() {
        return loginId;
    }

    public void setLoginId(Long loginId) {
        this.loginId = loginId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

}
