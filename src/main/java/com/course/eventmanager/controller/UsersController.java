package com.course.eventmanager.controller;

import com.course.eventmanager.model.user.*;
import com.course.eventmanager.security.jwt.AuthenticationService;
import com.course.eventmanager.service.UserService;
import com.course.eventmanager.util.user.UserConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UserService userService;
    private final UserConverter userConverter;
    private final AuthenticationService authenticationService;
    private final Logger log = LoggerFactory.getLogger(UsersController.class);

    public UsersController(UserService userService, UserConverter userConverter, AuthenticationService authenticationService) {
        this.userService = userService;
        this.userConverter = userConverter;
        this.authenticationService = authenticationService;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto registerUser(@RequestBody SignUpRequest signUpRequest) {
        log.info("Request for sign-up: login={}", signUpRequest.getLogin());
        User user = userService.registerUser(signUpRequest);
        return userConverter.domainToDto(user);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") Long id) {
        User user = userService.findUserById(id);
        return userConverter.domainToDto(user);
    }

    @PostMapping("/auth")
    public JwtTokenResponse authenticate(@RequestBody SignInRequest signInRequest) {
        log.info("Request for sign-in: login={}", signInRequest.getLogin());
        String token = authenticationService.authenticateUser(signInRequest);
        return new JwtTokenResponse(token);
    }
}
