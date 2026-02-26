package com.course.eventmanager.util.location;

import com.course.eventmanager.model.location.Location;
import com.course.eventmanager.model.location.LocationDto;
import org.springframework.stereotype.Component;

@Component
public class LocationDtoConverter {

    public LocationDto toDto(Location location) {
        return new LocationDto(
                location.getId(),
                location.getName(),
                location.getAddress(),
                location.getCapacity(),
                location.getDescription()
        );
    }

    public Location toDomain(LocationDto locationDto) {
        return new Location(
                null,
                locationDto.getName(),
                locationDto.getAddress(),
                locationDto.getCapacity(),
                locationDto.getDescription()
        );
    }
}
