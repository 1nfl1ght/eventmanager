package com.course.eventmanager.repository;

import com.course.eventmanager.model.location.Location;
import com.course.eventmanager.model.location.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

}
