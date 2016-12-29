package com.bah.rtc.web;

/**
 * Created by jamesflesher on 12/28/16.
 */
public enum WorkItemType {
    STORY("com.ibm.team.apt.workItemType.story"),
    TASK("task"),
    EPIC("com.ibm.team.apt.workItemType.epic"),
    DEFECT("com.ibm.team.workitem.workItemType.defect"),
    UNKNOWN("unknown");

    private String typeText;

    WorkItemType(String value) {
        typeText = value;
    }

    public String getTypeText() { return typeText; }

    public static WorkItemType fromString(String value) {
        WorkItemType returnValue = UNKNOWN;
        if(value != null) {
            for(WorkItemType type : WorkItemType.values()) {
                if(type.typeText.equalsIgnoreCase(value)) {
                    returnValue = type;
                    break;
                }
            }
        }

        return returnValue;
    }
}