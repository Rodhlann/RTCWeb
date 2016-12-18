package com.com.bah.rtc.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by jamesflesher on 12/16/16.
 */
@RestController
public class HomeController {
    @RequestMapping("/resource")
    public Map<String, Object> home() {
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("id", UUID.randomUUID().toString());
        model.put("content", "Hello World");
        return model;
    }

    @RequestMapping("/auth")
    public Principal user(Principal credentials) {
        return credentials;
    }
}