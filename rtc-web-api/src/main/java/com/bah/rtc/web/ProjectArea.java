package com.bah.rtc.web;

import java.util.UUID;

/**
 * Created by jamesflesher on 12/11/16.
 */
public class ProjectArea {
    private UUID id;
    private String name;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
