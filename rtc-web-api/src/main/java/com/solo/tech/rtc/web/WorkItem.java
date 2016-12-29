package com.solo.tech.rtc.web;

import java.io.Serializable;

/**
 * Created by jamesflesher on 12/11/16.
 */
public class WorkItem implements Serializable {
    private Long id;
    private String title;
    private String description;
    private User owner;
    private String status;
    private WorkItemType type;
    private String filedAgainst;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public WorkItemType getType() {
        return type;
    }

    public void setType(WorkItemType type) {
        this.type = type;
    }

    public String getFiledAgainst() {
        return filedAgainst;
    }

    public void setFiledAgainst(String filedAgainst) {
        this.filedAgainst = filedAgainst;
    }
}