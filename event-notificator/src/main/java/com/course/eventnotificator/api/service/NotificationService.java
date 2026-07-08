package com.course.eventnotificator.api.service;

import com.course.eventnotificator.api.dto.UnreadNotificationDto;
import com.course.eventnotificator.api.repository.NotificationRepository;
import com.course.eventnotificator.kafka.NotificationEntity;
import com.course.eventnotificator.security.AuthenticatedUser;
import com.course.eventnotificator.util.notification.NotificationConverter;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationConverter notificationConverter;

    public NotificationService(NotificationRepository repository, NotificationConverter notificationConverter) {
        this.repository = repository;
        this.notificationConverter = notificationConverter;
    }

    public List<UnreadNotificationDto> getNotifications(Long userId) {
        List<NotificationEntity> notificationEntityList = repository.findByUserIdAndIsReadFalse(userId);
        return notificationEntityList
                .stream()
                .map(notificationConverter::entityToDto)
                .toList();
    }

    @Transactional
    public void readNotifications(Long userId) {
        List<NotificationEntity> unread = repository.findByUserIdAndIsReadFalse(userId);
        unread.forEach(n -> {
            n.setRead(true);
            n.setReadAt(LocalDateTime.now());
        });
    }
}
