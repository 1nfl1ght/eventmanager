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
}
