package com.course.eventmanager.service;

import com.course.eventcommon.event.EventStatus;
import com.course.eventmanager.model.event.*;
import com.course.eventmanager.model.location.Location;
import com.course.eventmanager.model.location.LocationEntity;
import com.course.eventmanager.model.user.User;
import com.course.eventmanager.repository.EventRepository;
import com.course.eventmanager.repository.LocationRepository;
import com.course.eventmanager.util.event.EventConverter;
import com.course.eventmanager.util.location.LocationEntityConverter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final LocationEntityConverter locationEntityConverter;
    private final EventConverter eventConverter;
    private final PermissionService permissionService;

    public EventService(EventRepository eventRepository, LocationRepository locationRepository, LocationEntityConverter locationEntityConverter, EventConverter eventConverter, PermissionService permissionService) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.locationEntityConverter = locationEntityConverter;
        this.eventConverter = eventConverter;
        this.permissionService = permissionService;
    }

    @Transactional
    public Event createEvent(EventCreateRequest eventCreateRequest, User owner) {

        if (eventCreateRequest.getDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("The event has already ended");
        }

        Location location = locationEntityConverter.toDomain(locationRepository.findById(eventCreateRequest.getLocationId())
                .orElseThrow(() -> new EntityNotFoundException("Location with id " +eventCreateRequest.getLocationId() + " not found")));

        if (eventCreateRequest.getMaxPlaces() > location.getCapacity()) {
            throw new IllegalArgumentException("Max places (" + eventCreateRequest.getMaxPlaces() +
                    ") exceeds location capacity (" + location.getCapacity() + ")");
        }

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

    @Transactional
    public void deleteEvent(Long eventId, User currentUser) {
        EventEntity eventEntity = eventRepository.findByIdForUpdate(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found"));

        permissionService.checkOwnerOrAdmin(eventEntity.getOwner().getId(), currentUser);

        if (eventEntity.getStatus() != EventStatus.WAIT_START) {
            throw new IllegalStateException("Cannot cancel event with status " + eventEntity.getStatus());
        }

        eventEntity.setStatus(EventStatus.CANCELLED);
        eventRepository.save(eventEntity);
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
    public Event updateEvent(Long eventId, EventUpdateRequest eventUpdateRequest, User currentUser) {

        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found"));

        permissionService.checkOwnerOrAdmin(eventEntity.getOwner().getId(), currentUser);

        LocationEntity location = locationRepository.findById(eventUpdateRequest.getLocationId())
                .orElseThrow(() -> new EntityNotFoundException("Location with id " + eventUpdateRequest.getLocationId() + " not found"));

        if (eventUpdateRequest.getMaxPlaces() > location.getCapacity()) {
            throw new IllegalArgumentException("Max places (" + eventUpdateRequest.getMaxPlaces() +
                    ") exceeds location capacity (" + location.getCapacity() + ")");
        }
        if (eventUpdateRequest.getMaxPlaces() < eventEntity.getOccupiedPlaces()) {
            throw new IllegalArgumentException("Max places (" + eventUpdateRequest.getMaxPlaces() +
                    ") cannot be less than already occupied places (" + eventEntity.getOccupiedPlaces() + ")");
        }

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
