package com.imaginology.texas.Students;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StudentListDto {
    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    @SerializedName("page")
    @Expose
    private Object page;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public Object getPage() {
        return page;
    }

    public void setPage(Object page) {
        this.page = page;
    }

    public class Datum {
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("batch")
        @Expose
        private Integer batch;
        @SerializedName("firstName")
        @Expose
        private String firstName;
        @SerializedName("middleName")
        @Expose
        private String middleName;
        @SerializedName("lastName")
        @Expose
        private String lastName;
        @SerializedName("gender")
        @Expose
        private String gender;
        @SerializedName("phoneNumber")
        @Expose
        private String phoneNumber;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("courseId")
        @Expose
        private Integer courseId;
        @SerializedName("rollNumber")
        @Expose
        private String rollNumber;
        @SerializedName("semester")
        @Expose
        private String semester;
        @SerializedName("loginId")
        @Expose
        private Integer loginId;
        @SerializedName("courseName")
        @Expose
        private String courseName;
        @SerializedName("userRole")
        @Expose
        private String userRole;
        @SerializedName("team")
        @Expose
        private List<Team> team = null;
        @SerializedName("parents")
        @Expose
        private List<Parent> parents = null;
        @SerializedName("addresses")
        @Expose
        private List<Address> addresses = null;
        @SerializedName("createdDate")
        @Expose
        private String createdDate;
        @SerializedName("createdBy")
        @Expose
        private Integer createdBy;
        @SerializedName("createdByName")
        @Expose
        private String createdByName;
        @SerializedName("profilePicture")
        @Expose
        private String profilePicture;
        @SerializedName("modifiedDate")
        @Expose
        private String modifiedDate;
        @SerializedName("modifiedBy")
        @Expose
        private Integer modifiedBy;
        @SerializedName("modifiedByName")
        @Expose
        private String modifiedByName;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getBatch() {
            return batch;
        }

        public void setBatch(Integer batch) {
            this.batch = batch;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getMiddleName() {
            return middleName;
        }

        public void setMiddleName(String middleName) {
            this.middleName = middleName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Integer getCourseId() {
            return courseId;
        }

        public void setCourseId(Integer courseId) {
            this.courseId = courseId;
        }

        public String getRollNumber() {
            return rollNumber;
        }

        public void setRollNumber(String rollNumber) {
            this.rollNumber = rollNumber;
        }

        public String getSemester() {
            return semester;
        }

        public void setSemester(String semester) {
            this.semester = semester;
        }

        public Integer getLoginId() {
            return loginId;
        }

        public void setLoginId(Integer loginId) {
            this.loginId = loginId;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public String getUserRole() {
            return userRole;
        }

        public void setUserRole(String userRole) {
            this.userRole = userRole;
        }

        public List<Team> getTeam() {
            return team;
        }

        public void setTeam(List<Team> team) {
            this.team = team;
        }

        public List<Parent> getParents() {
            return parents;
        }

        public void setParents(List<Parent> parents) {
            this.parents = parents;
        }

        public List<Address> getAddresses() {
            return addresses;
        }

        public void setAddresses(List<Address> addresses) {
            this.addresses = addresses;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public Integer getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(Integer createdBy) {
            this.createdBy = createdBy;
        }

        public String getCreatedByName() {
            return createdByName;
        }

        public void setCreatedByName(String createdByName) {
            this.createdByName = createdByName;
        }

        public String getProfilePicture() {
            return profilePicture;
        }

        public void setProfilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
        }

        public String getModifiedDate() {
            return modifiedDate;
        }

        public void setModifiedDate(String modifiedDate) {
            this.modifiedDate = modifiedDate;
        }

        public Integer getModifiedBy() {
            return modifiedBy;
        }

        public void setModifiedBy(Integer modifiedBy) {
            this.modifiedBy = modifiedBy;
        }

        public String getModifiedByName() {
            return modifiedByName;
        }

        public void setModifiedByName(String modifiedByName) {
            this.modifiedByName = modifiedByName;
        }

    }

    public class Parent {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("fullName")
        @Expose
        private String fullName;
        @SerializedName("mobileNumber")
        @Expose
        private String mobileNumber;
        @SerializedName("homeOfficeNumber")
        @Expose
        private String homeOfficeNumber;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("relation")
        @Expose
        private String relation;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getMobileNumber() {
            return mobileNumber;
        }

        public void setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
        }

        public String getHomeOfficeNumber() {
            return homeOfficeNumber;
        }

        public void setHomeOfficeNumber(String homeOfficeNumber) {
            this.homeOfficeNumber = homeOfficeNumber;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRelation() {
            return relation;
        }

        public void setRelation(String relation) {
            this.relation = relation;
        }

    }

    public class Address {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("zone")
        @Expose
        private String zone;
        @SerializedName("district")
        @Expose
        private String district;
        @SerializedName("vdc")
        @Expose
        private String vdc;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("wardNo")
        @Expose
        private Integer wardNo;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getZone() {
            return zone;
        }

        public void setZone(String zone) {
            this.zone = zone;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getVdc() {
            return vdc;
        }

        public void setVdc(String vdc) {
            this.vdc = vdc;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getWardNo() {
            return wardNo;
        }

        public void setWardNo(Integer wardNo) {
            this.wardNo = wardNo;
        }

    }

    public class Team {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("type")
        @Expose
        private String type;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }
}