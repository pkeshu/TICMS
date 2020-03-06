package com.imaginology.texas.Teachers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TeacherListDto implements Parcelable {

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

    public final static Creator<TeacherListDto> CREATOR = new Creator<TeacherListDto>() {

        @SuppressWarnings({
                "unchecked"
        })
        public TeacherListDto createFromParcel(Parcel in) {
            TeacherListDto instance = new TeacherListDto();
            instance.total = ((Integer) in.readValue((Integer.class.getClassLoader())));
            in.readList(instance.data, (TeacherListDto.Datum.class.getClassLoader()));
            return instance;
        }

        public TeacherListDto[] newArray(int size) {
            return (new TeacherListDto[size]);
        }

    };

    public Integer getSize() {
        return total;
    }

    public void setSize(Integer size) {
        this.total = size;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(total);
        dest.writeList(data);
    }

    public int describeContents() {
        return 0;
    }

    public static class Datum implements Parcelable {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("firstName")
        @Expose
        private String firstName;
        @SerializedName("middleName")
        @Expose
        private String middleName;
        @SerializedName("lastName")
        @Expose
        private String lastName;
        @SerializedName("phoneNumber")
        @Expose
        private String phoneNumber;
        @SerializedName("gender")
        @Expose
        private String gender;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("addresses")
        @Expose
        private List<Address> addresses = null;
        @SerializedName("loginId")
        @Expose
        private Integer loginId;
        @SerializedName("username")
        @Expose
        private String username;
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
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

        public List<Address> getAddresses() {
            return addresses;
        }

        public void setAddresses(List<Address> addresses) {
            this.addresses = addresses;
        }

        public Integer getLoginId() {
            return loginId;
        }

        public void setLoginId(Integer loginId) {
            this.loginId = loginId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
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




        public static final Creator<Datum> CREATOR = new Creator<Datum>() {


            @SuppressWarnings({
                    "unchecked"
            })
            public Datum createFromParcel(Parcel in) {
                Datum instance = new Datum();
                instance.firstName = ((String) in.readValue((String.class.getClassLoader())));
                instance.lastName = ((String) in.readValue((String.class.getClassLoader())));
                instance.email = ((String) in.readValue((String.class.getClassLoader())));
                instance.username = ((String) in.readValue((String.class.getClassLoader())));
                instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
                instance.profilePicture=((String) in.readValue((String.class.getClassLoader())));
                instance.status = ((String) in.readValue((String.class.getClassLoader())));
                return instance;
            }

            public Datum[] newArray(int size) {
                return (new Datum[size]);
            }

        };

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(firstName);
            dest.writeValue(lastName);
            dest.writeValue(email);
            dest.writeValue(username);
            dest.writeValue(id);
            dest.writeValue(status);
            dest.writeValue(profilePicture);
        }

        public int describeContents() {
            return 0;
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

}