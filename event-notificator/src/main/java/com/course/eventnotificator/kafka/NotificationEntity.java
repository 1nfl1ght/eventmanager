package com.course.eventnotificator.kafka;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;
    @Column(name = "user_id")
    private Long userId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payload_id")
    private NotificationEventPayloadEntity payloadId;
    @Column(name = "is_read")
    private Boolean isRead;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "read_at")
    private LocalDateTime readAt;

    public NotificationEntity() {
    }

    public NotificationEntity(Long notificationId, Long userId, NotificationEventPayloadEntity payloadId, Boolean isRead, LocalDateTime createdAt, LocalDateTime readAt) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.payloadId = payloadId;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.readAt = readAt;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public NotificationEventPayloadEntity getPayloadId() {
        return payloadId;
    }

    public void setPayloadId(NotificationEventPayloadEntity payloadId) {
        this.payloadId = payloadId;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}
