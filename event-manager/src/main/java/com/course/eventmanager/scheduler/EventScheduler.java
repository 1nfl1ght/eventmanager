package com.course.eventmanager.scheduler;

import com.course.eventcommon.kafka.Change;
import com.course.eventcommon.kafka.EventMessage;
import com.course.eventcommon.kafka.KafkaEventType;
import com.course.eventmanager.model.event.Event;
import com.course.eventmanager.model.event.EventEntity;
import com.course.eventcommon.event.EventStatus;
import com.course.eventmanager.repository.EventRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class EventScheduler {

    private static final Logger log = LoggerFactory.getLogger(EventScheduler.class);
    private final EventRepository eventRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public EventScheduler(EventRepository eventRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.eventRepository = eventRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Scheduled(cron = "${event_status_cron}")
    @Transactional
    public void updateEventStatus() {
        log.info("update statuses timer started");

        eventRepository.findAllByStatus(EventStatus.WAIT_START).stream()
                .filter(event -> event.getStartAt().isBefore(LocalDateTime.now()))
                .forEach(event -> {
                    EventStatus oldStatus = event.getStatus();
                    event.setStatus(EventStatus.STARTED);
                    applicationEventPublisher.publishEvent(
                            buildStatusChangeMessage(event, oldStatus, EventStatus.STARTED));
                });

        eventRepository.findAllByStatus(EventStatus.STARTED).stream()
                .filter(event -> LocalDateTime.now().isAfter(event.getStartAt().plusMinutes(event.getDuration())))
                .forEach(event -> {
                    EventStatus oldStatus = event.getStatus();
                    event.setStatus(EventStatus.FINISHED);
                    applicationEventPublisher.publishEvent(
                            buildStatusChangeMessage(event, oldStatus, EventStatus.FINISHED));
                });
    }

    private EventMessage buildStatusChangeMessage(EventEntity event, EventStatus oldStatus, EventStatus newStatus) {
        Change change = new Change();
        change.setField("status");
        change.setOldValue(oldStatus.name());
        change.setNewValue(newStatus.name());

        List<Long> subscribers = event.getRegistrations().stream()
                .map(registration -> registration.getUser().getId())
                .collect(Collectors.toCollection(ArrayList::new));
        subscribers.add(event.getOwner().getId());

        List<Change> changes = new ArrayList<>();
        changes.add(change);

        EventMessage eventMessage = new EventMessage();
        eventMessage.setMessageId(UUID.randomUUID());
        eventMessage.setEventId(event.getId());
        eventMessage.setEventType(KafkaEventType.UPDATED);
        eventMessage.setEventName(event.getName());
        eventMessage.setOccurredAt(LocalDateTime.now());
        eventMessage.setChangedById(null);
        eventMessage.setOwnerId(event.getOwner().getId());
        eventMessage.setSubscribers(subscribers);
        eventMessage.setChanges(changes);
        return eventMessage;
    }
}
