package com.course.eventcommon.kafka;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class EventMessage {
    private UUID messageId;
    private KafkaEventType eventType;
    private Long eventId;
    private String eventName;
    private LocalDateTime occurredAt;
    private Long ownerId;
    private Long changedById;
    private List<Long> subscribers;
    private List<Change> changes;

    public EventMessage() {
    }

    public EventMessage(UUID messageId, KafkaEventType eventType, Long eventId, String eventName, LocalDateTime occurredAt, Long ownerId, Long changedById, List<Long> subscribers, List<Change> changes) {
        this.messageId = messageId;
        this.eventType = eventType;
        this.eventId = eventId;
        this.eventName = eventName;
        this.occurredAt = occurredAt;
        this.ownerId = ownerId;
        this.changedById = changedById;
        this.subscribers = subscribers;
        this.changes = changes;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public KafkaEventType getEventType() {
        return eventType;
    }

    public void setEventType(KafkaEventType eventType) {
        this.eventType = eventType;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getChangedById() {
        return changedById;
    }

    public void setChangedById(Long changedById) {
        this.changedById = changedById;
    }

    public List<Long> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<Long> subscribers) {
        this.subscribers = subscribers;
    }

    public List<Change> getChanges() {
        return changes;
    }

    public void setChanges(List<Change> changes) {
        this.changes = changes;
    }
}
