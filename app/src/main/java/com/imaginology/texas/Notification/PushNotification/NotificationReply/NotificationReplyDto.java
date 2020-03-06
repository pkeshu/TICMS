package com.imaginology.texas.Notification.PushNotification.NotificationReply;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.Time;
import java.util.List;

public class NotificationReplyDto {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    public List<Notifications> notifications;

    public NotificationReplyDto(List<Notifications> notifications) {
        this.notifications = notifications;
    }

    public List<Notifications> getNotifications() {
        return notifications;
    }

    public NotificationReplyDto(String message) {
        this.message = message;

    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class Notifications {
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("modified")
        @Expose
        private Boolean modified;
        @SerializedName("modifiedDate")
        @Expose
        private Long modifiedDate;
        @SerializedName("notificationId")
        @Expose
        private Long notificationId;
        @SerializedName("repliedDate")
        @Expose
        private Long repliedDate;
        @SerializedName("sender")
        @Expose
        private Sender sender;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Boolean getModified() {
            return modified;
        }

        public void setModified(Boolean modified) {
            this.modified = modified;
        }

        public Long getModifiedDate() {
            return modifiedDate;
        }

        public void setModifiedDate(Long modifiedDate) {
            this.modifiedDate = modifiedDate;
        }

        public Long getNotificationId() {
            return notificationId;
        }

        public void setNotificationId(Long notificationId) {
            this.notificationId = notificationId;
        }

        public Long getRepliedDate() {
            return repliedDate;
        }

        public void setRepliedDate(Long repliedDate) {
            this.repliedDate = repliedDate;
        }

        public Sender getSender() {
            return sender;
        }

        public void setSender(Sender sender) {
            this.sender = sender;
        }


        public Notifications(String message, Time currentTime, Sender sender) {
        }

        public Notifications(String message, Long repliedDate, Sender sender) {
            this.message = message;
            this.repliedDate = repliedDate;
            this.sender = sender;
        }
    }

    public static class Sender {

        @SerializedName("profilePicture")
        @Expose
        private String profilePicture;
        @SerializedName("senderId")
        @Expose
        private Integer senderId;
        @SerializedName("senderName")
        @Expose
        private String senderName;
        @SerializedName("userRole")
        @Expose
        private String userRole;

        public String getProfilePicture() {
            return profilePicture;
        }

        public void setProfilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
        }

        public Integer getSenderId() {
            return senderId;
        }

        public void setSenderId(Integer senderId) {
            this.senderId = senderId;
        }

        public String getSenderName() {
            return senderName;
        }

        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }

        public String getUserRole() {
            return userRole;
        }

        public void setUserRole(String userRole) {
            this.userRole = userRole;
        }

        public Sender(String profilePicture, Integer senderId, String senderName, String userRole) {
            this.profilePicture = profilePicture;
            this.senderId = senderId;
            this.senderName = senderName;
            this.userRole = userRole;
        }

        public Sender() {
        }
    }

}
