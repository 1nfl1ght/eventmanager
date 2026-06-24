package com.course.eventmanager.model.registration;

import com.course.eventmanager.model.event.Event;
import com.course.eventmanager.model.user.User;

import java.time.LocalDateTime;

public class Registration {

    private Long id;
    private Event event;
    private User user;
    private LocalDateTime createdAt;

    public Registration(Event event, User user) {
        this.event = event;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
