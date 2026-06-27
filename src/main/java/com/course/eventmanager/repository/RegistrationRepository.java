package com.course.eventmanager.repository;

import com.course.eventmanager.model.event.EventEntity;
import com.course.eventmanager.model.registration.RegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<RegistrationEntity, Long> {

    @Query("SELECT r.event FROM RegistrationEntity r WHERE r.user.id = :userId")
    List<EventEntity> findEventsByUserId(@Param("userId") Long userId);
}
