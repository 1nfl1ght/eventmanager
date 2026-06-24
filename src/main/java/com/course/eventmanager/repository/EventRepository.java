package com.course.eventmanager.repository;

import com.course.eventmanager.model.event.EventEntity;
import com.course.eventmanager.model.event.EventSearchRequest;
import com.course.eventmanager.model.event.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {
    @Query("SELECT e FROM EventEntity e WHERE " +
            "(:#{#request.name} IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :#{#request.name}, '%'))) AND " +
            "(:#{#request.placesMin} IS NULL OR e.maxPlaces >= :#{#request.placesMin}) AND " +
            "(:#{#request.placesMax} IS NULL OR e.maxPlaces <= :#{#request.placesMax}) AND " +
            "(:#{#request.dateStartAfter} IS NULL OR e.startAt >= :#{#request.dateStartAfter}) AND " +
            "(:#{#request.dateStartBefore} IS NULL OR e.startAt <= :#{#request.dateStartBefore}) AND " +
            "(:#{#request.costMin} IS NULL OR e.cost >= :#{#request.costMin}) AND " +
            "(:#{#request.costMax} IS NULL OR e.cost <= :#{#request.costMax}) AND " +
            "(:#{#request.durationMin} IS NULL OR e.duration >= :#{#request.durationMin}) AND " +
            "(:#{#request.durationMax} IS NULL OR e.duration <= :#{#request.durationMax}) AND " +
            "(:#{#request.locationId} IS NULL OR e.location.id = :#{#request.locationId}) AND " +
            "(:#{#request.eventStatus} IS NULL OR e.status = :#{#request.eventStatus})")
    List<EventEntity> findAllByFilter(@Param("request") EventSearchRequest eventSearchRequest);

    @Query("SELECT e FROM EventEntity e WHERE e.owner.id = :userId")
    List<EventEntity> findAllByOwnerId(@Param("userId") Long userId);

    List<EventEntity> findAllByStatusAndStartAtBefore(EventStatus eventStatus, LocalDateTime startAt);

    List<EventEntity> findAllByStatusAndStartAtAfter(EventStatus eventStatus, LocalDateTime now);

    List<EventEntity> findAllByStatus(EventStatus eventStatus);
}
