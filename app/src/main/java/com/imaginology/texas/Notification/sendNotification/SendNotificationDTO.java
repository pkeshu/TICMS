package com.imaginology.texas.Notification.sendNotification;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendNotificationDTO {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("teamId")
    @Expose
    private ArrayList<Long> teamId = null;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("receiverId")
    @Expose
    private Long receiverId;
    public SendNotificationDTO(String message, ArrayList<Long> teamId, String title) {
        this.message = message;
        this.teamId = teamId;
        this.title = title;
    }

    public SendNotificationDTO(String message,Long receiverId,String title) {
        this.message = message;
        this.receiverId = receiverId;
        this.title=title;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Long> getTeamId() {
        return teamId;
    }

    public void setTeamId(ArrayList<Long> teamId) {
        this.teamId = teamId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}