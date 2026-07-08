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
}
