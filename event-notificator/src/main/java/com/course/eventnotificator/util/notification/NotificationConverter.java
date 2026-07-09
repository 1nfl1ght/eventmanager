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
                null, // TODO: источник message в модели пока не определён — заполнить, когда решишь, откуда его брать
                payload.getPayload()
        );
    }
}
