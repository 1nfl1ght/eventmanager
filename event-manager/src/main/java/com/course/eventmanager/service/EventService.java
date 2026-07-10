package com.course.eventmanager.service;

import com.course.eventcommon.event.EventStatus;
import com.course.eventcommon.kafka.Change;
import com.course.eventcommon.kafka.EventMessage;
import com.course.eventcommon.kafka.KafkaEventType;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final LocationEntityConverter locationEntityConverter;
    private final EventConverter eventConverter;
    private final PermissionService permissionService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public EventService(EventRepository eventRepository, LocationRepository locationRepository, LocationEntityConverter locationEntityConverter, EventConverter eventConverter, PermissionService permissionService, ApplicationEventPublisher applicationEventPublisher) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.locationEntityConverter = locationEntityConverter;
        this.eventConverter = eventConverter;
        this.permissionService = permissionService;
        this.applicationEventPublisher = applicationEventPublisher;
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
        eventMessage.setSubscribers(eventEntity.getRegistrations().stream().map(e -> e.getUser().getId()).collect(Collectors.toCollection(ArrayList::new)));
        eventMessage.getSubscribers().add(eventEntity.getOwner().getId());
        eventMessage.setChanges(new ArrayList<>());
        eventMessage.getChanges().add(new Change());
        eventMessage.getChanges().getFirst().setField("status");
        eventMessage.getChanges().getFirst().setOldValue(eventEntity.getStatus().name());
        eventEntity.setStatus(EventStatus.CANCELLED);
        eventMessage.getChanges().getFirst().setNewValue(eventEntity.getStatus().name());
        eventRepository.save(eventEntity);
        applicationEventPublisher.publishEvent(eventMessage);
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

        List<Change> changes = new ArrayList<>();
        addChange(changes, "name", eventEntity.getName(), eventUpdateRequest.getName());
        addChange(changes, "maxPlaces", eventEntity.getMaxPlaces(), eventUpdateRequest.getMaxPlaces());
        addChange(changes, "date", eventEntity.getStartAt(), eventUpdateRequest.getDate());
        addChange(changes, "cost", eventEntity.getCost(), eventUpdateRequest.getCost());
        addChange(changes, "duration", eventEntity.getDuration(), eventUpdateRequest.getDuration());
        addChange(changes, "location", eventEntity.getLocation().getId(), location.getId());

        eventEntity.setName(eventUpdateRequest.getName());
        eventEntity.setMaxPlaces(eventUpdateRequest.getMaxPlaces());
        eventEntity.setStartAt(eventUpdateRequest.getDate());
        eventEntity.setCost(eventUpdateRequest.getCost());
        eventEntity.setDuration(eventUpdateRequest.getDuration());
        eventEntity.setLocation(location);
        EventEntity savedEvent = eventRepository.save(eventEntity);

        if (!changes.isEmpty()) {
            EventMessage eventMessage = new EventMessage();
            eventMessage.setMessageId(UUID.randomUUID());
            eventMessage.setChangedById(currentUser.getId());
            eventMessage.setEventId(savedEvent.getId());
            eventMessage.setEventType(KafkaEventType.UPDATED);
            eventMessage.setEventName(savedEvent.getName());
            eventMessage.setOccurredAt(LocalDateTime.now());
            eventMessage.setOwnerId(savedEvent.getOwner().getId());
            eventMessage.setSubscribers(savedEvent.getRegistrations().stream().map(e -> e.getUser().getId()).collect(Collectors.toCollection(ArrayList::new)));
            eventMessage.getSubscribers().add(savedEvent.getOwner().getId());
            eventMessage.setChanges(changes);
            applicationEventPublisher.publishEvent(eventMessage);
        }
        return eventConverter.entityToDomain(savedEvent);
    }

    private void addChange(List<Change> changes, String field, Object oldValue, Object newValue) {
        boolean changed;
        if (oldValue instanceof BigDecimal oldNumber && newValue instanceof BigDecimal newNumber) {
            changed = oldNumber.compareTo(newNumber) != 0;
        } else {
            changed = !Objects.equals(oldValue, newValue);
        }
        if (changed) {
            Change change = new Change();
            change.setField(field);
            change.setOldValue(oldValue == null ? null : String.valueOf(oldValue));
            change.setNewValue(newValue == null ? null : String.valueOf(newValue));
            changes.add(change);
        }
    }
}
