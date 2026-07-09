package com.course.eventmanager.service;

import com.course.eventcommon.event.EventStatus;
import com.course.eventcommon.kafka.Change;
import com.course.eventcommon.kafka.EventMessage;
import com.course.eventcommon.kafka.KafkaEventType;
import com.course.eventmanager.kafka.EventSender;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final LocationEntityConverter locationEntityConverter;
    private final EventConverter eventConverter;
    private final PermissionService permissionService;
    private final EventSender eventSender;

    public EventService(EventRepository eventRepository, LocationRepository locationRepository, LocationEntityConverter locationEntityConverter, EventConverter eventConverter, PermissionService permissionService, EventSender eventSender) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.locationEntityConverter = locationEntityConverter;
        this.eventConverter = eventConverter;
        this.permissionService = permissionService;
        this.eventSender = eventSender;
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
        EventMessage eventMessage = new EventMessage();
        EventEntity eventEntity = eventRepository.findByIdForUpdate(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found"));

        permissionService.checkOwnerOrAdmin(eventEntity.getOwner().getId(), currentUser);

        if (eventEntity.getStatus() != EventStatus.WAIT_START) {
            throw new IllegalStateException("Cannot cancel event with status " + eventEntity.getStatus());
        }
        eventMessage.setEventId(eventId);
        eventMessage.setEventType(KafkaEventType.CANCELLED);
        eventMessage.setEventName(eventEntity.getName());
        eventMessage.setMessageId(UUID.randomUUID());
        eventMessage.setChangedById(currentUser.getId());
        eventMessage.setOccurredAt(LocalDateTime.now());
        eventMessage.setOwnerId(eventEntity.getOwner().getId());
        eventMessage.setSubscribers(eventEntity.getRegistrations().stream().map(e -> e.getUser().getId()).toList());
        eventMessage.getSubscribers().add(eventEntity.getOwner().getId());
        eventMessage.setChanges(new ArrayList<>());
        eventMessage.getChanges().add(new Change());
        eventMessage.getChanges().getFirst().setField("status");
        eventMessage.getChanges().getFirst().setOldValue(eventEntity.getStatus().name());
        eventEntity.setStatus(EventStatus.CANCELLED);
        eventMessage.getChanges().getFirst().setNewValue(eventEntity.getStatus().name());
        eventRepository.save(eventEntity);
        eventSender.sendEvent(eventMessage);
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
