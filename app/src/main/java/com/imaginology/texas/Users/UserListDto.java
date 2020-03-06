package com.imaginology.texas.Users;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserListDto implements Parcelable {

    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    @SerializedName("page")
    @Expose
    private Integer page;

    public final static Parcelable.Creator<UserListDto> CREATOR = new Creator<UserListDto>() {


        @SuppressWarnings({"unchecked"})
        public UserListDto createFromParcel(Parcel in) {
            return new UserListDto(in);
        }

        public UserListDto[] newArray(int size) {
            return (new UserListDto[size]);
        }

    };

    protected UserListDto(Parcel in) {
        this.total = ((Integer) in.readValue((Integer.class.getClassLoader())));
        in.readList(this.data, (UserListDto.Datum.class.getClassLoader()));
    }

    public UserListDto() {
    }

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

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(total);
        dest.writeValue(page);
        dest.writeList(data);
    }

    public int describeContents() {
        return 0;
    }

    public class Datum implements Parcelable {

        @SerializedName("createdByName")
        @Expose
        private String createdByName;
        @SerializedName("firstName")
        @Expose
        private String firstName;
        @SerializedName("middleName")
        @Expose
        private String middleName;
        @SerializedName("lastName")
        @Expose
        private String lastName;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("username")
        @Expose
        private String username;
        @SerializedName("userRole")
        @Expose
        private String userRole;
        @SerializedName("mobileNumber")
        @Expose
        private String mobileNumber;
        @SerializedName("customerId")
        @Expose
        private Integer customerId;
        @SerializedName("loginId")
        @Expose
        private Integer loginId;
        @SerializedName("profilePicture")
        @Expose
        private String profilePicture;
        @SerializedName("gender")
        @Expose
        private String gender;
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("createdDate")
        @Expose
        private String createdDate;
        @SerializedName("createdBy")
        @Expose
        private Integer createdBy;
        @SerializedName("status")
        @Expose
        private String status;

        public final Parcelable.Creator<Datum> CREATOR = new Creator<Datum>() {


            @SuppressWarnings({
                    "unchecked"
            })
            public Datum createFromParcel(Parcel in) {
                return new Datum(in);
            }

            public Datum[] newArray(int size) {
                return (new Datum[size]);
            }

        };

        protected Datum(Parcel in) {
            this.firstName = ((String) in.readValue((String.class.getClassLoader())));
            this.middleName = ((String) in.readValue((String.class.getClassLoader())));
            this.lastName = ((String) in.readValue((String.class.getClassLoader())));
            this.email = ((String) in.readValue((String.class.getClassLoader())));
            this.username = ((String) in.readValue((String.class.getClassLoader())));
            this.userRole = ((String) in.readValue((String.class.getClassLoader())));
            this.mobileNumber = ((String) in.readValue((String.class.getClassLoader())));
            this.gender =((String) in.readValue(String.class.getClassLoader()));
            this.profilePicture = ((String) in.readValue((String.class.getClassLoader())));
            this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            this.status = ((String) in.readValue((String.class.getClassLoader())));
        }

        public Datum() {
        }

        public String getCreatedByName() {
            return createdByName;
        }

        public void setCreatedByName(String createdByName) {
            this.createdByName = createdByName;
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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUserRole() {
            return userRole;
        }

        public void setUserRole(String userRole) {
            this.userRole = userRole;
        }

        public String getMobileNumber() {
            return mobileNumber;
        }

        public void setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
        }

        public Integer getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Integer customerId) {
            this.customerId = customerId;
        }

        public Integer getLoginId() {
            return loginId;
        }

        public void setLoginId(Integer loginId) {
            this.loginId = loginId;
        }

        public String getProfilePicture() {
            return profilePicture;
        }

        public void setProfilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
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

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(firstName);
            dest.writeValue(middleName);
            dest.writeValue(lastName);
            dest.writeValue(email);
            dest.writeValue(username);
            dest.writeValue(userRole);
            dest.writeValue(mobileNumber);
            dest.writeValue(gender);
            dest.writeValue(profilePicture);
            dest.writeValue(id);
            dest.writeValue(status);
        }

        public int describeContents() {
            return 0;
        }

    }

}