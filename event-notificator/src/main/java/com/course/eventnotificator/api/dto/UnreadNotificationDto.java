package com.course.eventnotificator.api.dto;

import com.course.eventnotificator.kafka.EventChangePayload;

import java.time.LocalDateTime;

public class UnreadNotificationDto {

    private Long notificationId;
    private String type;
    private Long eventId;
    private LocalDateTime createdAt;
    private Boolean isRead;
    private String message;
    private EventChangePayload payload;
}
