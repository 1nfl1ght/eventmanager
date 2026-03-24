package com.course.eventmanager.repository;

import com.course.eventmanager.model.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByLogin(String login);

    Optional<UserEntity> findByLogin(String login);
}
