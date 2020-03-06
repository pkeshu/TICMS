package com.imaginology.texas.Login;

public class ChangePasswordDto {
    private String username;
    private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;

    public ChangePasswordDto(String username, String oldPassword, String newPassword, String confirmNewPassword) {
        this.username = username;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }
}
