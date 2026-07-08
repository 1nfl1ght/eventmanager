package com.course.eventnotificator.kafka;

import com.course.eventcommon.kafka.Change;

import java.util.List;

public class EventChangePayload {

    private String eventName;
    private Long changedById;
    private List<Change> changes;
}
