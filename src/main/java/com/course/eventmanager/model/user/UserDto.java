package com.course.eventmanager.model.user;

public class UserDto {

    private Long id;
    private String login;
    private Integer age;
    private Roles role;

    public UserDto() {
    }

    public UserDto(Long id, java.lang.String login, Integer age, Roles role) {
        this.id = id;
        this.login = login;
        this.age = age;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public java.lang.String getLogin() {
        return login;
    }

    public void setLogin(java.lang.String login) {
        this.login = login;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }
}


