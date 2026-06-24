package com.course.eventmanager.service;

import com.course.eventmanager.model.event.Event;
import com.course.eventmanager.model.event.EventEntity;
import com.course.eventmanager.model.event.EventStatus;
import com.course.eventmanager.model.registration.Registration;
import com.course.eventmanager.model.registration.RegistrationEntity;
import com.course.eventmanager.model.user.Roles;
import com.course.eventmanager.model.user.User;
import com.course.eventmanager.repository.EventRepository;
import com.course.eventmanager.repository.RegistrationRepository;
import com.course.eventmanager.util.event.EventConverter;
import com.course.eventmanager.util.user.UserConverter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final EventConverter eventConverter;
    private final UserConverter userConverter;

    public RegistrationService(RegistrationRepository registrationRepository, EventRepository eventRepository, EventConverter eventConverter, UserConverter userConverter) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.eventConverter = eventConverter;
        this.userConverter = userConverter;
    }

    @Transactional
    public void registerUserAtEvent(Long eventId, User user) {
        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found"));

        if (eventEntity.getStatus() != EventStatus.WAIT_START) {
            throw new IllegalStateException("Cannot register for event with status: " + eventEntity.getStatus());
        }

        boolean alreadyRegistered = eventEntity.getRegistrations().stream()
                .anyMatch(reg -> reg.getUser().getId().equals(user.getId()));
        if (alreadyRegistered) {
            throw new IllegalStateException("User already registered for this event");
        }

        if (eventEntity.getOccupiedPlaces() >= eventEntity.getMaxPlaces()) {
            throw new IllegalStateException("No available places for this event");
        }

        RegistrationEntity registrationEntity = new RegistrationEntity();
        registrationEntity.setEvent(eventEntity);
        registrationEntity.setUser(userConverter.domainToEntity(user));
        registrationEntity.setCreatedAt(LocalDateTime.now());

        eventEntity.addRegistration(registrationEntity);

        eventEntity.setOccupiedPlaces(eventEntity.getOccupiedPlaces() + 1);

        eventRepository.save(eventEntity);
    }

    public void cancelEvent(Long eventId, User currentUser) {
        EventEntity eventEntity = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found"));

        if (!eventEntity.getOwner().getId().equals(currentUser.getId()) && !currentUser.getRole().equals(Roles.ADMIN)) {
            throw new AccessDeniedException("Access denied");
        }

        if (eventEntity.getStatus() == EventStatus.WAIT_START) {
            eventEntity.setStatus(EventStatus.CANCELLED);
        } else {
            throw new IllegalStateException("Cannot cancel event with status " + eventEntity.getStatus());
        }
        eventRepository.save(eventEntity);
    }
}
