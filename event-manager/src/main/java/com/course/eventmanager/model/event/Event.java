package com.course.eventmanager.model.event;

import com.course.eventcommon.event.EventStatus;
import com.course.eventmanager.model.location.Location;
import com.course.eventmanager.model.registration.Registration;
import com.course.eventmanager.model.user.User;
import com.course.eventmanager.model.user.UserEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Event {

    private Long id;
    private String name;
    private LocalDateTime startAt;
    private BigDecimal cost;
    private Integer maxPlaces;
    private Integer duration;
    private Integer occupiedPlaces;
    private EventStatus status;
    private User owner;
    private Location location;
    private List<Registration> registrations;

    private Event(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.startAt = builder.startAt;
        this.cost = builder.cost;
        this.maxPlaces = builder.maxPlaces;
        this.duration = builder.duration;
        this.occupiedPlaces = builder.occupiedPlaces;
        this.status = builder.status;
        this.owner = builder.owner;
        this.location = builder.location;
        this.registrations = builder.registrations != null
                ? new ArrayList<>(builder.registrations)
                : new ArrayList<>();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public LocalDateTime getStartAt() { return startAt; }
    public BigDecimal getCost() { return cost; }
    public Integer getMaxPlaces() { return maxPlaces; }
    public Integer getDuration() { return duration; }
    public Integer getOccupiedPlaces() { return occupiedPlaces; }
    public EventStatus getStatus() { return status; }
    public User getOwner() { return owner; }
    public Location getLocation() { return location; }
    public List<Registration> getRegistrations() {
        return Collections.unmodifiableList(registrations);
    }

    public static class Builder {
        private Long id;
        private String name;
        private LocalDateTime startAt;
        private BigDecimal cost;
        private Integer maxPlaces;
        private Integer duration;
        private Integer occupiedPlaces;
        private EventStatus status;
        private User owner;
        private Location location;
        private List<Registration> registrations = new ArrayList<>();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder startAt(LocalDateTime startAt) {
            this.startAt = startAt;
            return this;
        }

        public Builder cost(BigDecimal cost) {
            this.cost = cost;
            return this;
        }

        public Builder maxPlaces(Integer maxPlaces) {
            this.maxPlaces = maxPlaces;
            return this;
        }

        public Builder duration(Integer duration) {
            this.duration = duration;
            return this;
        }

        public Builder occupiedPlaces(Integer occupiedPlaces) {
            this.occupiedPlaces = occupiedPlaces;
            return this;
        }

        public Builder status(EventStatus status) {
            this.status = status;
            return this;
        }

        public Builder owner(User owner) {
            this.owner = owner;
            return this;
        }

        public Builder location(Location location) {
            this.location = location;
            return this;
        }

        public Builder registrations(List<Registration> registrations) {
            this.registrations.clear();
            if (registrations != null) {
                this.registrations.addAll(registrations);
            }
            return this;
        }

        public Builder addRegistration(Registration registration) {
            if (registration != null) {
                this.registrations.add(registration);
            }
            return this;
        }

        public Event build() {
            return new Event(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public void setMaxPlaces(Integer maxPlaces) {
        this.maxPlaces = maxPlaces;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setOccupiedPlaces(Integer occupiedPlaces) {
        this.occupiedPlaces = occupiedPlaces;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setRegistrations(List<Registration> registrations) {
        this.registrations = registrations;
    }
}
