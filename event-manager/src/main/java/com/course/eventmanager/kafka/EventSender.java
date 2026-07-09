package com.course.eventmanager.kafka;

import com.course.eventcommon.kafka.EventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EventSender {

    private static final Logger log = LoggerFactory.getLogger(EventSender.class);
    private final String topic;
    private final KafkaTemplate<Long, EventMessage> kafkaTemplate;



    public EventSender(@Value("${app.kafka.topic.domain-events}") String topic, KafkaTemplate<Long, EventMessage> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEventCommitted(EventMessage eventMessage) {
        sendEvent(eventMessage);
    }

    public void sendEvent(EventMessage eventMessage) {
        log.info("Sending event: event={}", eventMessage);
        var sendResult = kafkaTemplate.send(topic, eventMessage.getEventId(), eventMessage);

        sendResult.whenComplete((result, ex) -> {
            if (ex != null) log.error("Send failed for eventId={}", eventMessage.getEventId(), ex);
            else log.info("Send successful");
        });
    }
}
