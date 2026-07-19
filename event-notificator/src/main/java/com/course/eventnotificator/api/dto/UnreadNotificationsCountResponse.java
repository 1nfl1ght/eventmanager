package com.course.eventnotificator.api.dto;

public class UnreadNotificationsCountResponse {
    private Integer count;

    public UnreadNotificationsCountResponse() {
    }

    public UnreadNotificationsCountResponse(Integer count) {
        this.count = count;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
