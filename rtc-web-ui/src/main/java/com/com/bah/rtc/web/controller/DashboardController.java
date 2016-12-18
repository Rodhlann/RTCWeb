package com.com.bah.rtc.web.controller;

import com.bah.rtc.web.ProjectArea;
import com.bah.rtc.web.RTCService;
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
}