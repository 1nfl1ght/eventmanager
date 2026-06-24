package com.course.eventmanager.model.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EventSearchRequest {
    private String name;
    private Integer placesMin;
    private Integer placesMax;
    private LocalDateTime dateStartAfter;
    private LocalDateTime dateStartBefore;
    private BigDecimal costMin;
    private BigDecimal costMax;
    private Integer durationMin;
    private Integer durationMax;
    private Long locationId;
    private EventStatus eventStatus;

    public EventSearchRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPlacesMin() {
        return placesMin;
    }

    public void setPlacesMin(Integer placesMin) {
        this.placesMin = placesMin;
    }

    public Integer getPlacesMax() {
        return placesMax;
    }

    public void setPlacesMax(Integer placesMax) {
        this.placesMax = placesMax;
    }

    public LocalDateTime getDateStartAfter() {
        return dateStartAfter;
    }

    public void setDateStartAfter(LocalDateTime dateStartAfter) {
        this.dateStartAfter = dateStartAfter;
    }

    public LocalDateTime getDateStartBefore() {
        return dateStartBefore;
    }

    public void setDateStartBefore(LocalDateTime dateStartBefore) {
        this.dateStartBefore = dateStartBefore;
    }

    public BigDecimal getCostMin() {
        return costMin;
    }

    public void setCostMin(BigDecimal costMin) {
        this.costMin = costMin;
    }

    public BigDecimal getCostMax() {
        return costMax;
    }

    public void setCostMax(BigDecimal costMax) {
        this.costMax = costMax;
    }

    public Integer getDurationMin() {
        return durationMin;
    }

    public void setDurationMin(Integer durationMin) {
        this.durationMin = durationMin;
    }

    public Integer getDurationMax() {
        return durationMax;
    }

    public void setDurationMax(Integer durationMax) {
        this.durationMax = durationMax;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }
}
