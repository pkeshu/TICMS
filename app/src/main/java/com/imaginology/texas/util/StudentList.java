package com.imaginology.texas.util;

public class StudentList {
        int id;
        String status;
        Long batch;
        String firstName;
        String middleName;
        String lastName;
        String gender;
        String phoneNumber;
        String mobileNumber;
        String email;
        Long courseId;
        String rollNumber;
        String semester;
        Long loginId;
        String courseName;

    public StudentList(int id, String status, Long batch, String firstName, String middleName, String lastName, String gender, String phoneNumber, String mobileNumber, String email, Long courseId, String rollNumber, String semester, Long loginId, String courseName) {
        this.id = id;
        this.status = status;
        this.batch = batch;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.courseId = courseId;
        this.rollNumber = rollNumber;
        this.semester = semester;
        this.loginId = loginId;
        this.courseName = courseName;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public Long getBatch() {
        return batch;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public String getSemester() {
        return semester;
    }

    public Long getLoginId() {
        return loginId;
    }

    public String getCourseName() {
        return courseName;
    }
}

