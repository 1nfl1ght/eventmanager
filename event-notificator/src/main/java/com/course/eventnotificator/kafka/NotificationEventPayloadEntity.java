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

    public Long getPayloadId() {
        return payloadId;
    }

    public void setPayloadId(Long payloadId) {
        this.payloadId = payloadId;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public Long getChangeById() {
        return changeById;
    }

    public void setChangeById(Long changeById) {
        this.changeById = changeById;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public EventChangePayload getPayload() {
        return payload;
    }

    public void setPayload(EventChangePayload payload) {
        this.payload = payload;
    }
}
