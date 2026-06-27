package com.course.eventmanager.model.user;

import jakarta.validation.constraints.NotBlank;

public class SignInRequest {
    @NotBlank(message = "Login must not be blank")
    private String login;
    @NotBlank(message = "Password must not be blank")
    private String password;

    public SignInRequest() {
    }

    public SignInRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
