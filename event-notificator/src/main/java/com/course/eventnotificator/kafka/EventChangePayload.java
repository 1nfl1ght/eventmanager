package com.course.eventnotificator.kafka;

import com.course.eventcommon.kafka.Change;

import java.util.List;

public class EventChangePayload {

    private String eventName;
    private Long changedById;
    private List<Change> changes;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Long getChangedById() {
        return changedById;
    }

    public void setChangedById(Long changedById) {
        this.changedById = changedById;
    }

    public List<Change> getChanges() {
        return changes;
    }

    public void setChanges(List<Change> changes) {
        this.changes = changes;
    }
}
