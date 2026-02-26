package com.course.eventmanager.model.error;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String message;
    private String detailMessage;
    private LocalDateTime dateTime;

    public ErrorResponse(String message, String detailMessage) {
        this.message = message;
        this.detailMessage = detailMessage;
        this.dateTime = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    public void setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
