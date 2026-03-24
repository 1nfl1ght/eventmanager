package com.course.eventmanager.service;

import com.course.eventmanager.model.user.Roles;
import com.course.eventmanager.model.user.SignUpRequest;
import com.course.eventmanager.model.user.User;
import com.course.eventmanager.model.user.UserEntity;
import com.course.eventmanager.repository.UserRepository;
import com.course.eventmanager.util.user.UserConverter;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserConverter userConverter;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserConverter userConverter) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userConverter = userConverter;
    }

    public User registerUser(SignUpRequest signUpRequest) {
        if (userRepository.existsByLogin(signUpRequest.getLogin())) {
            throw new IllegalArgumentException("Username already taken");
        }
        java.lang.String hashedPassword = passwordEncoder.encode(signUpRequest.getPassword());
        UserEntity userToSave = new UserEntity(
                null,
                signUpRequest.getLogin(),
                hashedPassword,
                signUpRequest.getAge(),
                Roles.USER.name()
        );
        UserEntity savedUser = userRepository.save(userToSave);
        return userConverter.entityToDomain(savedUser);
    }

    public User findByLogin(String login) {
        UserEntity user = userRepository.findByLogin(login).orElseThrow(() -> new UsernameNotFoundException("User with login" + login + " not found"));
        return userConverter.entityToDomain(user);
    }

    public User findUserById(Long id) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
        return userConverter.entityToDomain(user);
    }

    public boolean isExistsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }

    public void saveUser(User user) {
        userRepository.save(userConverter.domainToEntity(user));
    }
}
