package com.bah.rtc.web;

import java.util.List;

/**
 * Created by jamesflesher on 12/9/16.
 */
public interface RTCService {
    void login(String url, String username, String password);
    void logout();
    boolean isAuthenticated();
    WorkItem getWorkItem(String projectAreaName, String workItemId);
    List<ProjectArea> getProjectAreas();
}