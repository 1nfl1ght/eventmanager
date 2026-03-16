package com.course.eventmanager.configuration;

import com.course.eventmanager.model.user.Roles;
import com.course.eventmanager.model.user.User;
import com.course.eventmanager.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserInitializer {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public DefaultUserInitializer(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initUsers() {
        createUserIfNotExists("admin", "admin", Roles.ADMIN);
        createUserIfNotExists("user", "user", Roles.USER);
    }

    private void createUserIfNotExists(String login, String password, Roles role) {
        if (userService.isExistsByLogin(login)) {
            return;
        }
        String hashedPass = passwordEncoder.encode(password);
        User user = new User(
                null,
                login,
                hashedPass,
                25,
                role
        );
        userService.saveUser(user);
    }


}
