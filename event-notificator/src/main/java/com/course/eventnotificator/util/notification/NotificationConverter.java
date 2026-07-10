package com.course.eventnotificator.util.notification;

import com.course.eventnotificator.api.dto.UnreadNotificationDto;
import com.course.eventnotificator.kafka.NotificationEntity;
import com.course.eventnotificator.kafka.NotificationEventPayloadEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationConverter {

    public UnreadNotificationDto entityToDto(NotificationEntity entity) {
        NotificationEventPayloadEntity payload = entity.getPayloadId();
        return new UnreadNotificationDto(
                entity.getNotificationId(),
                payload.getEventType().name(),
                payload.getEventId(),
                entity.getCreatedAt(),
                entity.getRead(),
                buildMessage(payload),
                payload.getPayload()
        );
    }

    private String buildMessage(NotificationEventPayloadEntity payload) {
        String action = switch (payload.getEventType()) {
            case CREATED -> "создано";
            case UPDATED -> "изменено";
            case CANCELLED -> "отменено";
            case REMOVED -> "удалено";
        };
        String eventName = payload.getPayload() != null ? payload.getPayload().getEventName() : null;
        if (eventName == null) {
            return "Мероприятие " + action;
        }
        return "Мероприятие \"" + eventName + "\" " + action;
    }
}
