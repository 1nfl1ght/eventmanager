package com.course.eventmanager.service;

import com.course.eventmanager.model.event.EventEntity;
import com.course.eventcommon.event.EventStatus;
import com.course.eventmanager.model.location.Location;
import com.course.eventmanager.model.location.LocationDto;
import com.course.eventmanager.model.location.LocationEntity;
import com.course.eventmanager.repository.EventRepository;
import com.course.eventmanager.repository.LocationRepository;
import com.course.eventmanager.util.location.LocationDtoConverter;
import com.course.eventmanager.util.location.LocationEntityConverter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final EventRepository eventRepository;
    private final LocationEntityConverter locationEntityConverter;
    private final LocationDtoConverter locationDtoConverter;

    public LocationService(LocationRepository locationRepository, EventRepository eventRepository, LocationEntityConverter locationEntityConverter, LocationDtoConverter locationDtoConverter) {
        this.locationRepository = locationRepository;
        this.eventRepository = eventRepository;
        this.locationEntityConverter = locationEntityConverter;
        this.locationDtoConverter = locationDtoConverter;
    }

    @Cacheable("locations")
    public List<LocationDto> getAllLocations() {
        List<LocationEntity> locations = locationRepository.findAll();
        return locations.stream()
                .map(locationEntityConverter::toDomain)
                .map(locationDtoConverter::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Cacheable(
            value = "locationById",
            key = "#id")
    public LocationDto getLocationById(Long id) {
        return locationDtoConverter.toDto(
                locationEntityConverter.toDomain(locationRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(String.format("Location with id %d not found", id)))));
    }

    @CacheEvict(value = "locations", allEntries = true)
    public Location createLocation(Location location) {
        return locationEntityConverter.toDomain(locationRepository.save(locationEntityConverter.toEntity(location)));
    }

    @Caching(evict = {
            @CacheEvict(value = "locations", allEntries = true),
            @CacheEvict(value = "locationById", key = "#id", beforeInvocation = false)
    })
    @Transactional
    public void deleteLocationById(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new EntityNotFoundException(String.format("Location with id %d not found", id));
        }
        if (eventRepository.existsByLocationId(id)) {
            throw new IllegalArgumentException("Cannot delete location with id " + id + ": there are events associated with it");
        }
        locationRepository.deleteById(id);
    }

    @Caching(evict = {
            @CacheEvict(value = "locations", allEntries = true),
            @CacheEvict(value = "locationById", key = "#id")
    })
    @Transactional
    public Location updateLocation(Long id, Location locationToUpdate) {
        LocationEntity locationEntity = locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Location with id %d not found", id)));

        List<EventEntity> activeEvents = eventRepository.findAllByLocationIdAndStatusIn(
                id, List.of(EventStatus.WAIT_START, EventStatus.STARTED));
        int requiredCapacity = activeEvents.stream()
                .mapToInt(EventEntity::getMaxPlaces)
                .max()
                .orElse(0);
        if (locationToUpdate.getCapacity() < requiredCapacity) {
            throw new IllegalArgumentException("Cannot set capacity to " + locationToUpdate.getCapacity() +
                    ": there are already scheduled events at this location requiring at least " + requiredCapacity + " places");
        }

        locationEntity.setName(locationToUpdate.getName());
        locationEntity.setAddress(locationToUpdate.getAddress());
        locationEntity.setCapacity(locationToUpdate.getCapacity());
        locationEntity.setDescription(locationToUpdate.getDescription());

        LocationEntity updatedLocation = locationRepository.save(locationEntity);
        return locationEntityConverter.toDomain(updatedLocation);
    }
}
