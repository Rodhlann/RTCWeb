package com.solo.tech.rtc.web;

import javax.naming.AuthenticationException;

import java.util.List;

/**
 * Created by jamesflesher on 12/9/16.
 */
public interface RTCService {
    void login(String url, String username, String password) throws AuthenticationException;
    void logout();
    boolean isAuthenticated();
    List<WorkItem> getWorkItems(String projectAreaName, String sprint, String team, List<String> tags);
    List<ProjectArea> getProjectAreas();
    List<TeamArea> getTeamAreas();
    List<Category> getCategories(String projectAreaName);
    List<Sprint> getSprints(String projectAreaName);
}