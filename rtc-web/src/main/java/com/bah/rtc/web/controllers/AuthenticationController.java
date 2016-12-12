package com.bah.rtc.web.controllers;

import com.bah.rtc.web.RTCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by jamesflesher on 12/9/16.
 */
@RequestMapping("/authenticate")
@Controller
public class AuthenticationController extends BaseController {
    @Autowired
    private RTCService rtcService;

    @RequestMapping(path = "/login", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean login(@RequestParam final String userName, @RequestParam final String password) {
        rtcService.login(rtcURL, userName, password);
        return true;
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void logout() {
        rtcService.logout();
    }
}