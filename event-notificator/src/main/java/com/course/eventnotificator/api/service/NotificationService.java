package com.course.eventnotificator.api.service;

import com.course.eventcommon.kafka.EventMessage;
import com.course.eventnotificator.api.dto.UnreadNotificationDto;
import com.course.eventnotificator.api.repository.NotificationEventPayloadRepository;
import com.course.eventnotificator.api.repository.NotificationRepository;
import com.course.eventnotificator.kafka.EventChangePayload;
import com.course.eventnotificator.kafka.NotificationEntity;
import com.course.eventnotificator.kafka.NotificationEventPayloadEntity;
import com.course.eventnotificator.util.notification.NotificationConverter;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationConverter notificationConverter;
    private final NotificationEventPayloadRepository notificationEventPayloadRepository;

    public NotificationService(NotificationRepository repository, NotificationConverter notificationConverter, NotificationEventPayloadRepository notificationEventPayloadRepository) {
        this.repository = repository;
        this.notificationConverter = notificationConverter;
        this.notificationEventPayloadRepository = notificationEventPayloadRepository;
    }

    public List<UnreadNotificationDto> getNotifications(Long userId) {
        List<NotificationEntity> notificationEntityList = repository.findByUserIdAndIsReadFalse(userId);
        return notificationEntityList
                .stream()
                .map(notificationConverter::entityToDto)
                .toList();
    }

    @Transactional
    public void readNotifications(Long userId, List<Long> notifications) {
        List<NotificationEntity> unread = repository.findByUserIdAndNotificationIdIn(userId, notifications);
        unread.forEach(n -> {
            n.setRead(true);
            n.setReadAt(LocalDateTime.now());
        });
    }

    @Transactional
    public void savePayloadAndInbox(EventMessage message) {
        NotificationEventPayloadEntity eventPayloadEntity = new NotificationEventPayloadEntity();
        eventPayloadEntity.setEventId(message.getEventId());
        eventPayloadEntity.setEventType(message.getEventType());
        eventPayloadEntity.setChangedById(message.getChangedById());
        eventPayloadEntity.setMessageId(message.getMessageId());
        eventPayloadEntity.setOwnerId(message.getOwnerId());
        eventPayloadEntity.setOccurredAt(message.getOccurredAt());
        eventPayloadEntity.setPayload(new EventChangePayload());
        eventPayloadEntity.getPayload().setEventName(message.getEventName());
        eventPayloadEntity.getPayload().setChangedById(message.getChangedById());
        eventPayloadEntity.getPayload().setChanges(message.getChanges());
        NotificationEventPayloadEntity savedNotificationEventPayloadEntity = notificationEventPayloadRepository.save(eventPayloadEntity);

        message.getSubscribers()
                .forEach(sub -> {
                    NotificationEntity notificationEntity = new NotificationEntity();
                    notificationEntity.setCreatedAt(LocalDateTime.now());
                    notificationEntity.setRead(false);
                    notificationEntity.setPayloadId(savedNotificationEventPayloadEntity);
                    notificationEntity.setReadAt(null);
                    notificationEntity.setUserId(sub);

                    repository.save(notificationEntity);
                });
    }
}
