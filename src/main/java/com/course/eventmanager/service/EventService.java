package com.course.eventmanager.service;

import com.course.eventmanager.model.event.*;
import com.course.eventmanager.model.location.Location;
import com.course.eventmanager.model.location.LocationEntity;
import com.course.eventmanager.model.user.Roles;
import com.course.eventmanager.model.user.User;
import com.course.eventmanager.repository.EventRepository;
import com.course.eventmanager.repository.LocationRepository;
import com.course.eventmanager.util.event.EventConverter;
import com.course.eventmanager.util.location.LocationEntityConverter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final LocationEntityConverter locationEntityConverter;
    private final EventConverter eventConverter;

    public EventService(EventRepository eventRepository, LocationRepository locationRepository, LocationEntityConverter locationEntityConverter, EventConverter eventConverter) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.locationEntityConverter = locationEntityConverter;
        this.eventConverter = eventConverter;
    }

    @Transactional
    public Event createEvent(EventCreateRequest eventCreateRequest, User owner) {

        if (eventCreateRequest.getDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("The event has already ended");
        }

        Location location = locationEntityConverter.toDomain(locationRepository.findById(eventCreateRequest.getLocationId())
                .orElseThrow(() -> new EntityNotFoundException("Location with id " +eventCreateRequest.getLocationId() + " not found")));

        Event event = Event.builder()
                .name(eventCreateRequest.getName())
                .maxPlaces(eventCreateRequest.getMaxPlaces())
                .startAt(eventCreateRequest.getDate())
                .cost(eventCreateRequest.getCost())
                .duration(eventCreateRequest.getDuration())
                .occupiedPlaces(0)
                .location(location)
                .status(EventStatus.WAIT_START)
                .owner(owner)
                .build();

        EventEntity savedEvent = eventRepository.save(eventConverter.domainToEntity(event));
        return eventConverter.entityToDomain(savedEvent);
    }

    public void deleteEvent(Long eventId, User currentUser) throws AccessDeniedException {

        Event event = eventConverter.entityToDomain(eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found")));

        if (!event.getOwner().getId().equals(currentUser.getId()) && !currentUser.getRole().equals(Roles.ADMIN)) {
            throw new AccessDeniedException("Access denied");
        }

        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event with id " + eventId + " not found");
        }
        eventRepository.deleteById(eventId);
    }

    public Event getEventById(Long eventId) {
        return eventConverter.entityToDomain(eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found")));
    }

    public List<Event> searchEvents(EventSearchRequest eventSearchRequest) {
        List<EventEntity> events = eventRepository.findAllByFilter(eventSearchRequest);
        return events.stream()
                .map(eventConverter::entityToDomain)
                .toList();
    }

    public List<Event> getCurrentUserEvents(Long userId) {
        List<EventEntity> events = eventRepository.findAllByOwnerId(userId);
        return events.stream()
                .map(eventConverter::entityToDomain)
                .toList();
    }

    @Transactional
    public Event updateEvent(Long eventId, EventUpdateRequest eventUpdateRequest, User currentUser) throws AccessDeniedException {

        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found"));

        if (!eventEntity.getOwner().getId().equals(currentUser.getId()) && !currentUser.getRole().equals(Roles.ADMIN)) {
            throw new AccessDeniedException("Access denied");
        }

        LocationEntity location = locationRepository.findById(eventUpdateRequest.getLocationId())
                .orElseThrow(() -> new EntityNotFoundException("Location with id " + eventUpdateRequest.getLocationId() + " not found"));

        eventEntity.setName(eventUpdateRequest.getName());
        eventEntity.setMaxPlaces(eventUpdateRequest.getMaxPlaces());
        eventEntity.setStartAt(eventUpdateRequest.getDate());
        eventEntity.setCost(eventUpdateRequest.getCost());
        eventEntity.setDuration(eventUpdateRequest.getDuration());
        eventEntity.setLocation(location);

        EventEntity savedEvent = eventRepository.save(eventEntity);
        return eventConverter.entityToDomain(savedEvent);
    }
}
