package com.bah.rtc.web;

import javax.naming.AuthenticationException;

import java.util.List;

/**
 * Created by jamesflesher on 12/9/16.
 */
public interface RTCService {
    void login(String url, String username, String password) throws AuthenticationException;
    void logout();
    boolean isAuthenticated();
    WorkItem getWorkItem(String projectAreaName, String workItemId);
    List<ProjectArea> getProjectAreas();
    List<TeamArea> getTeamAreas();
}