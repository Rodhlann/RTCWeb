package com.bah.rtc.web;

import com.bah.rtc.web.controllers.AuthenticationController;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jamesflesher on 12/10/16.
 */
@Aspect
@Component
public class AuthenticationAspect    {
    @Autowired
    private RTCService rtcService;

    @Before("execution( * com.bah.rtc.web.controllers..*.*(..))")
    public void checkAuthenticationState(JoinPoint joinPoint) {
        if(joinPoint.getTarget() instanceof AuthenticationController) {
            System.out.println("Ignore Authentication Method Calls");
        }
        else {
            if(!rtcService.isAuthenticated()) {
                System.out.println("NOT AUTHENTICATED!!");
                throw new AuthenticationException();
            } else {
                System.out.println("Authenticated");
            }
        }
    }
}