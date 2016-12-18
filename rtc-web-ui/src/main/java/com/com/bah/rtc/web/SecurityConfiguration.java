package com.com.bah.rtc.web;

import com.bah.rtc.web.RTCWebImplConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

/**
 * Spring Application Configuration
 * @author James Flesher
 * Created on 12/16/16.
 */
@Configuration
@ComponentScan("com.com.bah.rtc.web")
@Import(RTCWebImplConfig.class)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private RTCAuthenticationProvider authenticationProvider;

    @Autowired
    private LogoutSuccessProvider logoutSuccessProvider;

    /**
     * Configures custom authentication provider which allows the RTC Web application to have forms authentication
     * while performing the actual authentication with the RTC Restful Endpoint
     * @param auth
     * @throws Exception
     */
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    /**
     * Configure Spring Web Security to determine which parts of the appilcation requires authentication
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .httpBasic()
            .and()
            .authorizeRequests()
                .antMatchers("/index.html", "/home.html", "/login.html", "/", "/auth", "/css/**", "/fonts/**", "/images/**", "/js/**")
                .permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class)
            .csrf()
                .csrfTokenRepository(csrfTokenRepository())
            .and()
            .logout()
                .logoutSuccessHandler(logoutSuccessProvider);
    }

    /**
     * Provide CSRF Protection: borrowed from https://spring.io/blog/2015/01/12/the-login-page-angular-js-and-spring-security-part-ii
     * @return
     */
    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }
}