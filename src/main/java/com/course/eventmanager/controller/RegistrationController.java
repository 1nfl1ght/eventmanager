package com.course.eventmanager.controller;

import com.course.eventmanager.model.event.EventDto;
import com.course.eventmanager.model.user.User;
import com.course.eventmanager.security.jwt.AuthenticationService;
import com.course.eventmanager.service.RegistrationService;
import com.course.eventmanager.util.event.EventConverter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final AuthenticationService authenticationService;
    private final EventConverter eventConverter;

    public RegistrationController(RegistrationService registrationService, AuthenticationService authenticationService, EventConverter eventConverter) {
        this.registrationService = registrationService;
        this.authenticationService = authenticationService;
        this.eventConverter = eventConverter;
    }

    @PostMapping("/{eventId}")
    public void registerUserAtEvent(@PathVariable("eventId") Long eventId) {
        User user = authenticationService.getCurrentAuthenticationOrThrow();
        registrationService.registerUserAtEvent(eventId, user);
    }

    @DeleteMapping("/cancel/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelRegistration(@PathVariable("eventId") Long eventId) {
        User currentUser = authenticationService.getCurrentAuthenticationOrThrow();
        registrationService.cancelRegistration(eventId, currentUser);
    }

    @GetMapping("/my")
    public List<EventDto> getUserRegisteredEvents() {
        User currentUser = authenticationService.getCurrentAuthenticationOrThrow();
        return registrationService.getUserRegisteredEvents(currentUser.getId()).stream()
                .map(eventConverter::domainToDto)
                .toList();
    }
}
