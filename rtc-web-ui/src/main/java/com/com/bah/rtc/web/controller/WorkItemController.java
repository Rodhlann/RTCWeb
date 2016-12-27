package com.com.bah.rtc.web.controller;

import com.bah.rtc.web.RTCService;
import com.bah.rtc.web.WorkItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by jamesflesher on 12/10/16.
 */
@RequestMapping("/workitem")
@Controller
public class WorkItemController {
    @Autowired
    private RTCService rtcService;

    @RequestMapping(path = "/findBySprint", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<WorkItem> getWorkItem(@RequestParam final String projectArea, @RequestParam final String sprint) {
        return rtcService.getWorkItems(projectArea, sprint);
    }
}