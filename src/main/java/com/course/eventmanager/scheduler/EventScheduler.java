package com.course.eventmanager.scheduler;

import com.course.eventmanager.model.event.Event;
import com.course.eventmanager.model.event.EventEntity;
import com.course.eventmanager.model.event.EventStatus;
import com.course.eventmanager.repository.EventRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class EventScheduler {

    private static final Logger log = LoggerFactory.getLogger(EventScheduler.class);
    private final EventRepository eventRepository;

    public EventScheduler(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Scheduled(cron = "${event_status_cron}")
    @Transactional
    public void updateEventStatus() {
        log.info("update statuses timer started");

        List<EventEntity> waitStartEvents = eventRepository.findAllByStatus(
                EventStatus.WAIT_START
        );

        waitStartEvents.stream()
                .filter(event -> event.getStartAt().isBefore(LocalDateTime.now()))
                .forEach(event -> event.setStatus(EventStatus.STARTED));

        List<EventEntity> startedEvents = eventRepository.findAllByStatus(
                EventStatus.STARTED
        );

        startedEvents.stream()
                .filter(event -> LocalDateTime.now().isAfter(event.getStartAt().plusMinutes(event.getDuration())))
                .forEach(event -> event.setStatus(EventStatus.FINISHED));
    }
}
