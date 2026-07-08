package com.course.eventnotificator.api.service;

import com.course.eventnotificator.api.dto.UnreadNotificationDto;
import com.course.eventnotificator.api.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public List<UnreadNotificationDto> getNotifications() {
        return repository.findByUserIdAndIsReadFalse();
    }
}
