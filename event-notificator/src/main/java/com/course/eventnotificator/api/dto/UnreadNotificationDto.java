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

    public UnreadNotificationDto() {
    }

    public UnreadNotificationDto(Long notificationId, String type, Long eventId, LocalDateTime createdAt, Boolean isRead, String message, EventChangePayload payload) {
        this.notificationId = notificationId;
        this.type = type;
        this.eventId = eventId;
        this.createdAt = createdAt;
        this.isRead = isRead;
        this.message = message;
        this.payload = payload;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EventChangePayload getPayload() {
        return payload;
    }

    public void setPayload(EventChangePayload payload) {
        this.payload = payload;
    }
}
