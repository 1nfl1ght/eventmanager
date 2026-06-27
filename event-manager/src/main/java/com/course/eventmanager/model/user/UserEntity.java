package com.course.eventmanager.model.user;

import com.course.eventmanager.model.registration.RegistrationEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "login")
    private String login;
    @Column(name = "password")
    private String password;
    @Column(name = "age")
    private Integer age;
    @Column(name = "role")
    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistrationEntity> registrations = new ArrayList<>();

    public void addRegistration(RegistrationEntity registration) {
        registrations.add(registration);
        registration.setUser(this);
    }

    public void removeRegistration(RegistrationEntity registration) {
        registrations.remove(registration);
        registration.setUser(null);
    }

    public UserEntity() {
    }

    public UserEntity(Long id, String login, String password, Integer age, String role) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.age = age;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
