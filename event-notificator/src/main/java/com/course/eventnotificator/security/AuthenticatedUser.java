package com.course.eventnotificator.security;

public class AuthenticatedUser {
    private Long userId;
    private String role;
    private String login;

    public AuthenticatedUser(Long userId, String role, String login) {
        this.userId = userId;
        this.role = role;
        this.login = login;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
