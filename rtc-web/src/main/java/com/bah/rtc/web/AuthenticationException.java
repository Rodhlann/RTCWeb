package com.bah.rtc.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by jamesflesher on 12/10/16.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AuthenticationException extends RuntimeException {
}
