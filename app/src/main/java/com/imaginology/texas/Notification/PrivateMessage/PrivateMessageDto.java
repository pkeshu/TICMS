package com.imaginology.texas.Notification.PrivateMessage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PrivateMessageDto {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("receiverId")
    @Expose
    private Long receiverId;

    public PrivateMessageDto(String message, Long receiverId) {
        this.message = message;
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }
}
