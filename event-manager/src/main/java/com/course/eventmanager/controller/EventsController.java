package com.course.eventmanager.controller;

import com.course.eventmanager.model.event.EventUpdateRequest;
import com.course.eventmanager.model.event.EventCreateRequest;
import com.course.eventmanager.model.event.EventDto;
import com.course.eventmanager.model.event.EventSearchRequest;
import com.course.eventmanager.model.user.User;
import com.course.eventmanager.security.jwt.AuthenticationService;
import com.course.eventmanager.service.EventService;
import com.course.eventmanager.util.event.EventConverter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventsController {

    private final EventService eventService;
    private final EventConverter eventConverter;
    private final AuthenticationService authenticationService;

    public EventsController(EventService eventService, EventConverter eventConverter, AuthenticationService authenticationService) {
        this.eventService = eventService;
        this.eventConverter = eventConverter;
        this.authenticationService = authenticationService;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto creteEvent(@RequestBody @Valid EventCreateRequest eventCreateRequest) {
        User owner = authenticationService.getCurrentAuthenticationOrThrow();
        return eventConverter.domainToDto(eventService.createEvent(eventCreateRequest, owner));
    }

    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable("eventId") Long eventId) {
        User currentUser = authenticationService.getCurrentAuthenticationOrThrow();
        eventService.deleteEvent(eventId, currentUser);
    }

    @GetMapping("/{eventId}")
    public EventDto getEvent(@PathVariable("eventId") Long eventId) {
        return eventService.getEventById(eventId);
    }

    @PutMapping("/{eventId}")
    public EventDto updateEvent(@PathVariable("eventId") Long eventId, @RequestBody @Valid EventUpdateRequest eventUpdateRequest) {
        User currentUser = authenticationService.getCurrentAuthenticationOrThrow();
        return eventConverter.domainToDto(eventService.updateEvent(eventId, eventUpdateRequest, currentUser));
    }

    @PostMapping("/search")
    public List<EventDto> searchEvents(@RequestBody @Valid EventSearchRequest eventSearchRequest) {
        return eventService.searchEvents(eventSearchRequest).stream()
                .map(eventConverter::domainToDto)
                .toList();
    }

    @GetMapping("/my")
    public List<EventDto> getUserCreatedEvents() {
        User owner = authenticationService.getCurrentAuthenticationOrThrow();
        return eventService.getCurrentUserEvents(owner.getId()).stream()
                .map(eventConverter::domainToDto)
                .toList();
    }

}
