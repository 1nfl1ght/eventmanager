package com.course.eventmanager.model.event;

import com.course.eventcommon.event.EventStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EventDto {

    private Long id;
    private String name;
    private LocalDateTime startAt;
    private Integer maxPlaces;
    private Integer duration;
    private Integer occupiedPlaces;
    private EventStatus status;
    private Long ownerId;
    private BigDecimal cost;
    private Long locationId;

    public EventDto() {
    }

    public EventDto(Long id, String name, LocalDateTime startAt, Integer maxPlaces, Integer duration, Integer occupiedPlaces, EventStatus status, Long ownerId, BigDecimal cost, Long locationId) {
        this.id = id;
        this.name = name;
        this.startAt = startAt;
        this.maxPlaces = maxPlaces;
        this.duration = duration;
        this.occupiedPlaces = occupiedPlaces;
        this.status = status;
        this.ownerId = ownerId;
        this.cost = cost;
        this.locationId = locationId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public Integer getMaxPlaces() {
        return maxPlaces;
    }

    public void setMaxPlaces(Integer maxPlaces) {
        this.maxPlaces = maxPlaces;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getOccupiedPlaces() {
        return occupiedPlaces;
    }

    public void setOccupiedPlaces(Integer occupiedPlaces) {
        this.occupiedPlaces = occupiedPlaces;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }
}
