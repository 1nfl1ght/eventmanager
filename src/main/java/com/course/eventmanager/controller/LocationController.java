package com.course.eventmanager.controller;

import com.course.eventmanager.model.location.LocationDto;
import com.course.eventmanager.service.LocationService;
import com.course.eventmanager.util.location.LocationDtoConverter;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {


    private static final Logger log = LoggerFactory.getLogger(LocationController.class);

    private final LocationService locationService;
    private final LocationDtoConverter locationDtoConverter;

    public LocationController(LocationService locationService, LocationDtoConverter locationDtoConverter) {
        this.locationService = locationService;
        this.locationDtoConverter = locationDtoConverter;
    }

    @GetMapping
    public List<LocationDto> getAll() {
        return locationService.getAll().stream()
                .map(locationDtoConverter::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public LocationDto getLocation(@PathVariable("id") Long id) {
        log.info("Location request: id={}", id);
        return locationDtoConverter.toDto(locationService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationDto createLocation(@RequestBody @Valid LocationDto locationDto) {
        log.info("Location create: {}", locationDto.getName());
        return locationDtoConverter.toDto(locationService.create(locationDtoConverter.toDomain(locationDto)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocation(@PathVariable("id") Long id) {
        log.info("Location delete: id={}", id);
        locationService.deleteById(id);
    }

    @PutMapping("/{id}")
    public LocationDto updateLocation(@PathVariable("id") Long id, @RequestBody @Valid LocationDto locationDto) {
        log.info("Update location: id={}, name={}", id, locationDto.getName());
        return locationDtoConverter.toDto(locationService.update(id, locationDtoConverter.toDomain(locationDto)));
    }

}
