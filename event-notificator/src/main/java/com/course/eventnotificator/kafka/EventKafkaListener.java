package com.course.eventnotificator.kafka;

import com.course.eventcommon.kafka.EventMessage;
import com.course.eventcommon.kafka.KafkaEventType;
import com.course.eventnotificator.api.repository.NotificationEventPayloadRepository;
import com.course.eventnotificator.api.repository.NotificationRepository;
import com.course.eventnotificator.api.service.NotificationService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class EventKafkaListener {

    private final NotificationEventPayloadRepository notificationEventPayloadRepository;
    private final NotificationService notificationService;

    private static final Logger log = LoggerFactory.getLogger(EventKafkaListener.class);

    public EventKafkaListener(NotificationEventPayloadRepository notificationEventPayloadRepository, NotificationService notificationService) {
        this.notificationEventPayloadRepository = notificationEventPayloadRepository;
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "${app.kafka.topic.domain-events}", containerFactory = "kafkaListenerContainerFactory")
    public void listenEvents(ConsumerRecord<Long, EventMessage> record) {
        EventMessage message = record.value();
        try {
            if (message == null) {
                log.error("Broken message at offset {}, skipping", record.offset());
                return;
            }
            if (message.getMessageId() == null || message.getEventId() == null || message.getSubscribers() == null) {
                log.error("Invalid message content: {}", message);
                return;
            }
            log.info("Event received: {}", message);
            if (notificationEventPayloadRepository.existsByMessageId(message.getMessageId())) {
                log.debug("Message {} already processed, skipping", message.getMessageId());
                return;
            }

            notificationService.savePayloadAndInbox(message);

        } catch (Exception e) {
            log.error("Failed to process message {}", message != null ? message.getMessageId() : "null", e);
        }
    }
}
