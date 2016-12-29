package com.solo.tech.rtc.web;

import com.solo.tech.rtc.web.RTCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Provider to provide forms authentication to the RTC Web application while authentication with and starting
 * an RTC Restful session through the RTC JAVA SDK
 * @author James Flesher
 * Created on 12/16/16.
 */
@Component
public class RTCAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private RTCService rtcService;

    /*
     * URL for the RTC Restful Endpoint //TODO: maybe make this configurable
     */
    @Value("${rtc.server.url}")
    protected String rtcURL;

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            /*
             * Log into and startup a RTC session through the RTC JAVA SDK
             */
            rtcService.login(rtcURL, name, password);
            return new UsernamePasswordAuthenticationToken(name, password, new ArrayList());
        } catch (javax.naming.AuthenticationException ex)
        {
            // Failed to authenticate - this will cause an exception to be thrown through Spring Security
            return null;
        }
    }

    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}