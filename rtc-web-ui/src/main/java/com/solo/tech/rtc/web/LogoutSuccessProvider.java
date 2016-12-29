package com.solo.tech.rtc.web;

import com.solo.tech.rtc.web.RTCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Successful Logout Provider to allow the proper shutdown of the current user's RTC session
 * @author James Flesher
 * Created on 12/17/16.
 */
@Component
public class LogoutSuccessProvider extends SimpleUrlLogoutSuccessHandler {
    @Autowired
    private RTCService rtcService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            rtcService.logout();
        } catch (Exception ex) {
            //TODO: Setup logging and send error to server log
        }
        super.onLogoutSuccess(request, response, authentication);
    }
}