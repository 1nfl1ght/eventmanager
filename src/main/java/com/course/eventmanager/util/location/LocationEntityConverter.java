package com.course.eventmanager.util.location;

import com.course.eventmanager.model.location.Location;
import com.course.eventmanager.model.location.LocationEntity;
import org.springframework.stereotype.Component;

@Component
public class LocationEntityConverter {

    public LocationEntity toEntity(Location location) {
        return new LocationEntity(
                location.getId(),
                location.getName(),
                location.getAddress(),
                location.getCapacity(),
                location.getDescription()
        );
    }

    public Location toDomain(LocationEntity locationEntity) {
        return new Location(
                locationEntity.getId(),
                locationEntity.getName(),
                locationEntity.getAddress(),
                locationEntity.getCapacity(),
                locationEntity.getDescription()
        );
    }
}
