package com.course.eventnotificator.api.controller;

import com.course.eventnotificator.api.dto.UnreadNotificationDto;
import com.course.eventnotificator.api.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    public List<UnreadNotificationDto> getNotifications() {

        return service.getNotifications();
    }

    @PostMapping
    public void readNotifications() {
        service.readNotifications();
    }
}
