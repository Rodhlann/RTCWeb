package com.com.bah.rtc.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @author James Flesher
 * Created on 12/16/16.
 */
@RestController
public class AuthenticationController {
    @RequestMapping("/auth")
    public Principal user(Principal credentials) {
        return credentials;
    }
}