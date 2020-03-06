package com.imaginology.texas.Users;

        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;
        import com.imaginology.texas.Team.TeamDto;

        import java.util.List;

public class UserDto {

    @SerializedName("user")
    @Expose
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public class User {

        @SerializedName("firstName")
        @Expose
        private String firstName;
        @SerializedName("middleName")
        @Expose
        private String middleName;

        public String getMiddleName() {
            return middleName;
        }

        public void setMiddleName(String middleName) {
            this.middleName = middleName;
        }

        @SerializedName("lastName")

        @Expose
        private String lastName;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("userRole")
        @Expose
        private String userRole;
        @SerializedName("username")
        @Expose
        private String userName;
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("profilePicture")
        @Expose
        private String profilePicture;
        @SerializedName("createdDate")
        @Expose
        private String createdDate;
        @SerializedName("createdBy")
        @Expose
        private Integer createdBy;
        @SerializedName("status")
        @Expose
        private String status;

        @SerializedName("mobileNumber")
        @Expose
        private String phone;
        @SerializedName("gender")
        @Expose
        private String gender;
        @SerializedName("team")
        @Expose
        private List<TeamDto.Content> teamList;

        public List<TeamDto.Content> getTeamList() {
            return teamList;
        }

        public void setTeamList(List<TeamDto.Content> teamList) {
            this.teamList = teamList;
        }


        public String getProfilePicture() {
            return profilePicture;
        }

        public void setProfilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
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

        public String getUserRole() {
            return userRole;
        }

        public void setUserRole(String userRole) {
            this.userRole = userRole;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

    }

}