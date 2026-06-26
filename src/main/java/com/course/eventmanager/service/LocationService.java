package com.course.eventmanager.service;

import com.course.eventmanager.model.event.EventEntity;
import com.course.eventmanager.model.event.EventStatus;
import com.course.eventmanager.model.location.Location;
import com.course.eventmanager.model.location.LocationEntity;
import com.course.eventmanager.repository.EventRepository;
import com.course.eventmanager.repository.LocationRepository;
import com.course.eventmanager.util.location.LocationEntityConverter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final EventRepository eventRepository;
    private final LocationEntityConverter locationEntityConverter;

    public LocationService(LocationRepository locationRepository, EventRepository eventRepository, LocationEntityConverter locationEntityConverter) {
        this.locationRepository = locationRepository;
        this.eventRepository = eventRepository;
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

    @Transactional
    public Location updateLocation(Long id, Location locationToUpdate) {
        // Загружаем управляемую (managed) сущность и правим её поля напрямую, а не пересобираем
        // новый LocationEntity через конвертер. У нового объекта events была бы пустым списком
        // по умолчанию, и save()/merge() с orphanRemoval=true на этой коллекции удалил бы
        // ВСЕ существующие события этой локации.
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
