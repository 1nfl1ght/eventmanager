package com.course.eventmanager.model.event;

import com.course.eventcommon.event.EventStatus;
import com.course.eventmanager.model.location.LocationEntity;
import com.course.eventmanager.model.registration.RegistrationEntity;
import com.course.eventmanager.model.user.UserEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "start_at")
    private LocalDateTime startAt;
    @Column(name = "cost")
    private BigDecimal cost;
    @Column(name = "max_places")
    private Integer maxPlaces;
    @Column(name = "duration_minutes")
    private Integer duration;
    @Column(name = "occupied_places")
    private Integer occupiedPlaces;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EventStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private LocationEntity location;

    @OneToMany(mappedBy = "event", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    private List<RegistrationEntity> registrations = new ArrayList<>();

    public void addRegistration(RegistrationEntity registration) {
        registrations.add(registration);
        registration.setEvent(this);
    }

    public void removeRegistration(RegistrationEntity registration) {
        registrations.remove(registration);
        registration.setEvent(null);
    }

    public EventEntity() {
    }

    public EventEntity(String name, LocalDateTime startAt, BigDecimal cost, Integer maxPlaces, Integer duration, Integer occupiedPlaces, EventStatus status, UserEntity owner, LocationEntity location) {
        this.name = name;
        this.startAt = startAt;
        this.cost = cost;
        this.maxPlaces = maxPlaces;
        this.duration = duration;
        this.occupiedPlaces = occupiedPlaces;
        this.status = status;
        this.owner = owner;
        this.location = location;
    }

    public LocationEntity getLocation() {
        return location;
    }

    public void setLocation(LocationEntity location) {
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getMaxPlaces() {
        return maxPlaces;
    }

    public void setMaxPlaces(Integer maxPlaces) {
        this.maxPlaces = maxPlaces;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public Integer getOccupiedPlaces() {
        return occupiedPlaces;
    }

    public void setOccupiedPlaces(Integer occupiedPlaces) {
        this.occupiedPlaces = occupiedPlaces;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    public List<RegistrationEntity> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<RegistrationEntity> registrations) {
        this.registrations = registrations;
    }
}
