package com.course.eventmanager;

import com.course.eventmanager.model.location.Location;
import com.course.eventmanager.model.location.LocationDto;
import com.course.eventmanager.service.LocationService;
import com.course.eventmanager.util.location.LocationDtoConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
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
    private static final long LOCATION_ID = 1L;

    @BeforeEach
    void setUp() {
        inputDto = new LocationDto(null, "Test Location", "Test Address", 100, "Test Description");

        domainObject = new Location(1L, "Test Location", "Test Address", 100, "Test Description");

        responseDto = new LocationDto(1L, "Test Location", "Test Address", 100, "Test Description");
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void successCreateLocation() throws Exception {
        when(locationDtoConverter.toDomain(any(LocationDto.class))).thenReturn(domainObject);
        when(locationService.createLocation(any(Location.class))).thenReturn(domainObject);
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
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void unsuccessCreateLocationWithNullFields() throws Exception {
        LocationDto locationDto = new LocationDto();
        Location location = new Location();

        when(locationDtoConverter.toDomain(any(LocationDto.class))).thenReturn(location);
        when(locationService.createLocation(any(Location.class))).thenReturn(domainObject);

        String locationJson = mapper.writeValueAsString(locationDto);

        mockMvc.perform(post("/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(locationJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andDo(print());

    }

    @Test
    @WithMockUser(username = "user", authorities = "USER")
    void successGetLocation() throws Exception {
        when(locationDtoConverter.toDomain(any(LocationDto.class))).thenReturn(domainObject);
        when(locationDtoConverter.toDto(any(Location.class))).thenReturn(responseDto);
        when(locationService.getLocationById(LOCATION_ID)).thenReturn(domainObject);

        mockMvc.perform(get("/locations/{id}", LOCATION_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Location"))
                .andExpect(jsonPath("$.address").value("Test Address"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
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


        when(locationService.updateLocation(eq(LOCATION_ID), any(Location.class))).thenReturn(updatedLocation);

        mockMvc.perform(put("/locations/{id}", LOCATION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedLocationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated name"))
                .andExpect(jsonPath("$.address").value("updated address"))
                .andExpect(jsonPath("$.id").value(LOCATION_ID))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void successDeleteLocation() throws Exception {
        doNothing().when(locationService).deleteLocationById(LOCATION_ID);

        mockMvc.perform(delete("/locations/{id}", LOCATION_ID))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""))
                .andDo(print());
    }
}
