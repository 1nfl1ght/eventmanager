package com.course.eventnotificator.api.repository;

import com.course.eventnotificator.api.dto.UnreadNotificationDto;
import com.course.eventnotificator.kafka.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.management.Notification;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByUserIdAndIsReadFalse(Long userId);
    List<NotificationEntity> findByUserIdAndNotificationIdIn(Long userId, List<Long> notificationIds);
    @Modifying
    @Query("delete from NotificationEntity n where n.createdAt < :threshold")
    int deleteOlderThan(LocalDateTime threshold);

    Integer countByUserIdAndIsReadFalse(Long userId);
}
