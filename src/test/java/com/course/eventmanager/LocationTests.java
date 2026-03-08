package com.course.eventmanager;

import com.course.eventmanager.model.location.Location;
import com.course.eventmanager.model.location.LocationDto;
import com.course.eventmanager.service.LocationService;
import com.course.eventmanager.util.location.LocationDtoConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LocationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationDtoConverter locationDtoConverter;

    @MockBean
    private LocationService locationService;

    private final ObjectMapper mapper = new ObjectMapper();

    private LocationDto inputDto;
    private Location domainObject;
    private LocationDto responseDto;

    @BeforeEach
    void setUp() {
        inputDto = new LocationDto(null, "Test Location", "Test Address", 100, "Test Description");

        domainObject = new Location(1L, "Test Location", "Test Address", 100, "Test Description");

        responseDto = new LocationDto(1L, "Test Location", "Test Address", 100, "Test Description");
    }

    @Test
    void successCreateLocation() throws Exception {
        when(locationDtoConverter.toDomain(any(LocationDto.class))).thenReturn(domainObject);
        when(locationService.create(any(Location.class))).thenReturn(domainObject);
        when(locationDtoConverter.toDto(any(Location.class))).thenReturn(responseDto);

        mockMvc.perform(post("/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(inputDto.getName()))
                .andExpect(jsonPath("$.address").value("Test Address"))
                .andExpect(jsonPath("$.id").value(1L))
                .andDo(print());
    }

    @Test
    void unsuccessCreateLocationWithNullFields() throws Exception {
        LocationDto locationDto = new LocationDto(
                null,
                null,
                null,
                null,
                null
        );

        Location location = new Location(
                null,
                null,
                null,
                null,
                null
        );

        when(locationDtoConverter.toDomain(any(LocationDto.class))).thenReturn(location);
        when(locationService.create(any(Location.class))).thenReturn(domainObject);

        String locationJson = mapper.writeValueAsString(locationDto);

        mockMvc.perform(post("/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(locationJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andDo(print());

    }

    @Test
    void successGetLocation() throws Exception {
        when(locationDtoConverter.toDomain(any(LocationDto.class))).thenReturn(domainObject);
        when(locationDtoConverter.toDto(any(Location.class))).thenReturn(responseDto);
        when(locationService.getById(1L)).thenReturn(domainObject);

        mockMvc.perform(get("/locations/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Location"))
                .andExpect(jsonPath("$.address").value("Test Address"))
                .andDo(print());
    }

    @Test
    void successUpdateLocation() throws Exception {
        Location updatedLocation = new Location(
                1L,
                "updated name",
                "updated address",
                123,
                null
        );

        LocationDto updatedLocationDto = new LocationDto(
                1L,
                "updated name",
                "updated address",
                123,
                null
        );

        when(locationDtoConverter.toDomain(any(LocationDto.class))).thenReturn(updatedLocation);
        when(locationDtoConverter.toDto(any(Location.class))).thenReturn(updatedLocationDto);


        when(locationService.update(eq(1L), any(Location.class))).thenReturn(updatedLocation);

        mockMvc.perform(put("/locations/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedLocationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated name"))
                .andExpect(jsonPath("$.address").value("updated address"))
                .andExpect(jsonPath("$.id").value(1L))
                .andDo(print());
    }

    @Test
    void successDeleteLocation() throws Exception {
        doNothing().when(locationService).deleteById(1L);

        mockMvc.perform(delete("/locations/{id}", 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""))
                .andDo(print());
    }
}
