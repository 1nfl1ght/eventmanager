package com.course.eventmanager.service;

import com.course.eventmanager.model.location.Location;
import com.course.eventmanager.model.location.LocationEntity;
import com.course.eventmanager.repository.LocationRepository;
import com.course.eventmanager.util.location.LocationEntityConverter;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationEntityConverter locationEntityConverter;

    public LocationService(LocationRepository locationRepository, LocationEntityConverter locationEntityConverter) {
        this.locationRepository = locationRepository;
        this.locationEntityConverter = locationEntityConverter;
    }

    public List<Location> getAllLocations() {
        List<LocationEntity> locations = locationRepository.findAll();
        return locations.stream()
                .map(locationEntityConverter::toDomain)
                .toList();
    }

    public Location getLocationById(Long id) {
        return locationEntityConverter.toDomain(locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Location with id %d not found", id))));
    }

    public Location createLocation(Location location) {
        return locationEntityConverter.toDomain(locationRepository.save(locationEntityConverter.toEntity(location)));
    }

    public void deleteLocationById(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new EntityNotFoundException(String.format("Location with id %d not found", id));
        }
        locationRepository.deleteById(id);
    }

    public Location updateLocation(Long id, Location locationToUpdate) {
        if (!locationRepository.existsById(id)) {
            throw new EntityNotFoundException(String.format("Location with id %d not found", id));
        }
        locationToUpdate.setId(id);
        LocationEntity locationEntityToUpdate = locationEntityConverter.toEntity(locationToUpdate);
        LocationEntity updatedLocation = locationRepository.save(locationEntityToUpdate);
        return locationEntityConverter.toDomain(updatedLocation);
    }
}
