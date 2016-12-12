package com.bah.rtc.web.controllers;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by jamesflesher on 12/11/16.
 */
public abstract class BaseController {
    @Value("${rtc.server.url}")
    protected String rtcURL;
}
