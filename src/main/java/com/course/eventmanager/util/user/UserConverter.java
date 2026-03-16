package com.course.eventmanager.util.user;

import com.course.eventmanager.model.user.Roles;
import com.course.eventmanager.model.user.User;
import com.course.eventmanager.model.user.UserDto;
import com.course.eventmanager.model.user.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    public UserDto domainToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getLogin(),
                user.getAge(),
                user.getRole()
        );
    }

    public User dtoToDomain(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getLogin(),
                null,
                userDto.getAge(),
                null
        );
    }

    public UserEntity domainToEntity(User user) {
        return new UserEntity(
                user.getId(),
                user.getLogin(),
                user.getPassword(),
                user.getAge(),
                user.getRole().name()
        );
    }

    public User entityToDomain(UserEntity userEntity) {
        return new User(
                userEntity.getId(),
                userEntity.getLogin(),
                userEntity.getPassword(),
                userEntity.getAge(),
                Roles.valueOf(userEntity.getRole())
        );
    }
}
