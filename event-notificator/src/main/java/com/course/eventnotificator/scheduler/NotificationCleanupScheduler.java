package com.course.eventnotificator.scheduler;

import com.course.eventnotificator.api.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotificationCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(NotificationCleanupScheduler.class);
    private final NotificationRepository notificationRepository;

    public NotificationCleanupScheduler(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Scheduled(cron = "${notification_cleanup_cron}")
    @Transactional
    public void cleanupNotifications() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(7);
        int deleted = notificationRepository.deleteOlderThan(threshold);
        log.info("Deleted {} notifications older than {}", deleted, threshold);
    }
}
