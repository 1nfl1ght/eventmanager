package com.course.eventmanager.model.event;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EventCreateRequest {
    @NotBlank
    private String name;
    @NotNull
    private Integer maxPlaces;
    @NotNull
    private LocalDateTime date;
    @NotNull
    @Min(0)
    private BigDecimal cost;
    @NotNull
    @Min(30)
    private Integer duration;
    @NotNull
    private Long locationId;

    public EventCreateRequest() {
    }

    public String getName() {
        return name;
    }

    public Integer getMaxPlaces() {
        return maxPlaces;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public Integer getDuration() {
        return duration;
    }

    public Long getLocationId() {
        return locationId;
    }
}
