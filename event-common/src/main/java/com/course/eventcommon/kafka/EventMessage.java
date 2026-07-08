package com.course.eventcommon.kafka;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class EventMessage {
    private UUID messageId;
    private String eventType;
    private Long eventId;
    private LocalDateTime occurredAt;
    private Long ownerId;
    private Long changedById;
    private ArrayList<Long> subscribers;
    private ArrayList<Change> changes;

    public EventMessage(UUID messageId, String eventType, Long eventId, LocalDateTime occurredAt, Long ownerId, Long changedById, ArrayList<Long> subscribers, ArrayList<Change> changes) {
        this.messageId = messageId;
        this.eventType = eventType;
        this.eventId = eventId;
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

    public ArrayList<Long> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(ArrayList<Long> subscribers) {
        this.subscribers = subscribers;
    }

    public ArrayList<Change> getChanges() {
        return changes;
    }

    public void setChanges(ArrayList<Change> changes) {
        this.changes = changes;
    }
}
