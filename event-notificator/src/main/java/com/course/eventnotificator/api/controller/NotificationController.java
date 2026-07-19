package com.course.eventnotificator.api.controller;

import com.course.eventnotificator.api.dto.UnreadNotificationDto;
import com.course.eventnotificator.api.dto.UnreadNotificationsCountResponse;
import com.course.eventnotificator.api.service.NotificationService;
import com.course.eventnotificator.kafka.NotificationEntity;
import com.course.eventnotificator.security.AuthenticatedUser;
import com.course.eventnotificator.security.AuthenticationService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;
    private final AuthenticationService authenticationService;

    public NotificationController(NotificationService service, AuthenticationService authenticationService) {
        this.service = service;
        this.authenticationService = authenticationService;
    }

    @GetMapping
    public List<UnreadNotificationDto> getNotifications() {
        AuthenticatedUser user = authenticationService.getCurrentAuthenticationOrThrow();
        return service.getNotifications(user.getUserId());
    }

    @PostMapping
    public void readNotifications(@RequestBody List<Long> notifications) {
        AuthenticatedUser user = authenticationService.getCurrentAuthenticationOrThrow();
        service.readNotifications(user.getUserId(), notifications);
    }

    @GetMapping("/unread-count")
    public UnreadNotificationsCountResponse getUnreadNotificationsCount() {
        AuthenticatedUser authenticatedUser = authenticationService.getCurrentAuthenticationOrThrow();
        Integer unreadNotifications = service.getUnreadNotifications(authenticatedUser.getUserId());
        return new UnreadNotificationsCountResponse(unreadNotifications);
    }
}
