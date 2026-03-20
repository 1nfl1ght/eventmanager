package com.course.eventmanager.model.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SignUpRequest {

    @NotBlank(message = "Login must not be blank")
    @Size(min = 5, message = "Login must be more than 5 characters long")
    private String login;
    @NotBlank(message = "Password must not be blank")
    @Size(min = 5, message = "Password must be more than 5 characters long")
    private String password;
    @NotNull(message = "Age must not be null")
    @Min(value = 0, message = "Age must be not less than 0")
    private Integer age;

    public SignUpRequest() {
    }

    public SignUpRequest(String login, String password, Integer age) {
        this.login = login;
        this.password = password;
        this.age = age;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
