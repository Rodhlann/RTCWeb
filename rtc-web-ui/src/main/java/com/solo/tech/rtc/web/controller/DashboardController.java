package com.solo.tech.rtc.web.controller;

import com.solo.tech.rtc.web.Category;
import com.solo.tech.rtc.web.ProjectArea;
import com.solo.tech.rtc.web.RTCService;
import com.solo.tech.rtc.web.Sprint;
import com.solo.tech.rtc.web.TeamArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by jamesflesher on 12/10/16.
 */
@RequestMapping("/dashboard")
@Controller
public class DashboardController {
    @Autowired
    private RTCService rtcService;

    @RequestMapping(path = "/getProjectAreas", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ProjectArea> getProjectAreas() {
        return rtcService.getProjectAreas();
    }

    @RequestMapping(path = "/getTeamAreas", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TeamArea> getTeamAreas() {
        return rtcService.getTeamAreas();
    }

    @RequestMapping(path = "/getSprints", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Sprint> getSprints(String projectAreaName) { return rtcService.getSprints(projectAreaName); }

    @RequestMapping(path = "/getCategories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Category> getCategories(String projectAreaName) { return rtcService.getCategories(projectAreaName); }
}