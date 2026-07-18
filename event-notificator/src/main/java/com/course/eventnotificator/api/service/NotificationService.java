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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository repository;
    private final NotificationConverter notificationConverter;
    private final NotificationEventPayloadRepository notificationEventPayloadRepository;
    private final StringRedisTemplate stringRedisTemplate;

    public NotificationService(NotificationRepository repository, NotificationConverter notificationConverter, NotificationEventPayloadRepository notificationEventPayloadRepository, StringRedisTemplate stringRedisTemplate) {
        this.repository = repository;
        this.notificationConverter = notificationConverter;
        this.notificationEventPayloadRepository = notificationEventPayloadRepository;
        this.stringRedisTemplate = stringRedisTemplate;
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
        stringRedisTemplate.opsForValue().set("notif:unread:" + userId, String.valueOf(unread.size()));
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

        message.getSubscribers()
                .forEach(sub -> {
                    stringRedisTemplate.opsForValue().increment("notif:unread:" + sub);
                });
    }

    @Transactional
    public Integer getUnreadNotifications(Long userId) {
        try {
            String count =  stringRedisTemplate.opsForValue().get("notif:unread:" + userId);
            return count != null ? Integer.parseInt(count) : 0;
        } catch (Exception e) {
            log.warn("Redis get error, falling back: ", e);
            return repository.countByUserIdAndIsReadFalse(userId);
        }
    }
}
