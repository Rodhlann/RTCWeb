package com.solo.tech.rtc.web;

import org.springframework.util.StringUtils;

/**
 * Created by jamesflesher on 12/29/16.
 */
public enum WorkItemStatus {
    READY_FOR_DEVELOPMENT("Ready For Development"),
    IN_PROGRESS("In Progress"),
    IN_SCRUM_TEST("In Scrum Test"),
    DONE("Done"),
    UNKNOWN("Unknown");

    private String statusText;

    WorkItemStatus(String value) {
        statusText = value;
    }

    public String getStatusText() { return statusText; }

    public static WorkItemStatus fromString(String value) {
        WorkItemStatus status = UNKNOWN;

        if(!StringUtils.isEmpty(value)) {
            for(WorkItemStatus type : WorkItemStatus.values()) {
                if(type.statusText.equalsIgnoreCase(value)) {
                    status = type;
                    break;
                }
            }
        }

        return status;
    }

    @Override
    public String toString() {
        return statusText;
    }
}
