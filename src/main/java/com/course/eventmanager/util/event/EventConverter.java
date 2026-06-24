package com.course.eventmanager.util.event;

import com.course.eventmanager.model.event.Event;
import com.course.eventmanager.model.event.EventDto;
import com.course.eventmanager.model.event.EventEntity;
import com.course.eventmanager.model.event.EventStatus;
import com.course.eventmanager.util.location.LocationEntityConverter;
import com.course.eventmanager.util.user.UserConverter;
import org.springframework.stereotype.Component;

@Component
public class EventConverter {

    private final LocationEntityConverter locationEntityConverter;
    private final UserConverter userConverter;

    public EventConverter(LocationEntityConverter locationEntityConverter, UserConverter userConverter) {
        this.locationEntityConverter = locationEntityConverter;
        this.userConverter = userConverter;
    }

    public Event dtoToDomain(EventDto eventDto) {
        return Event.builder()
                .name(eventDto.getName())
                .startAt(eventDto.getStartAt())
                .maxPlaces(eventDto.getMaxPlaces())
                .duration(eventDto.getDuration())
                .cost(eventDto.getCost())
                .build();
    }

    public EventDto domainToDto(Event event) {
        return new EventDto(
                event.getId(),
                event.getName(),
                event.getStartAt(),
                event.getMaxPlaces(),
                event.getDuration(),
                event.getOccupiedPlaces(),
                event.getStatus(),
                event.getOwner().getId(),
                event.getCost(),
                event.getLocation().getId()
        );
    }

    public EventEntity domainToEntity(Event event) {
        return new EventEntity(
                event.getName(),
                event.getStartAt(),
                event.getCost(),
                event.getMaxPlaces(),
                event.getDuration(),
                event.getOccupiedPlaces(),
                event.getStatus(),
                userConverter.domainToEntity(event.getOwner()),
                locationEntityConverter.toEntity(event.getLocation())
        );
    }

    public Event entityToDomain(EventEntity eventEntity) {
        return Event.builder()
                .id(eventEntity.getId())
                .name(eventEntity.getName())
                .startAt(eventEntity.getStartAt())
                .cost(eventEntity.getCost())
                .maxPlaces(eventEntity.getMaxPlaces())
                .duration(eventEntity.getDuration())
                .occupiedPlaces(eventEntity.getOccupiedPlaces())
                .status(eventEntity.getStatus())
                .owner(userConverter.entityToDomain(eventEntity.getOwner()))
                .location(locationEntityConverter.toDomain(eventEntity.getLocation()))
                .build();
    }
}
