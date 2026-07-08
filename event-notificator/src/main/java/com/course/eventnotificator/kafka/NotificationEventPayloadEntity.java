package com.course.eventnotificator.kafka;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification_event_payloads")
public class NotificationEventPayloadEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payload_id")
    private Long payloadId;
    @Column(name = "messageId")
    private UUID messageId;
    @Column(name = "event_type")
    private String eventType;
    @Column(name = "event_id")
    private Long eventId;
    @Column(name = "occurred_at")
    private LocalDateTime occurredAt;
    private Long changeById;
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private EventChangePayload payload;
}
