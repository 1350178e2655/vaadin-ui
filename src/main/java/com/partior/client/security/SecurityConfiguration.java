package com.partior.client.security;

import com.partior.client.views.login.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurityConfigurerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurityConfigurerAdapter {

    public static final String LOGOUT_URL = "/";

    private static final String LOGIN_PROCESSING_URL = "/login";
    private static final String LOGIN_FAILURE_URL = "/login?error";
    private static final String LOGIN_URL = "/login";
    private static final String LOGOUT_SUCCESS_URL = "/login";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .requestCache().requestCache(new CustomRequestCache())

        ;

                //.ignoringAntMatchers("/**");
        super.configure(http);
        setLoginView(http, LoginView.class, LOGOUT_URL);
    }


    /**
     * Require login to access internal pages and configure login form.
     */
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        // Vaadin handles CSRF internally
//        http.csrf().disable()
//
//                // Register our CustomRequestCache, which saves unauthorized access attempts, so the user is redirected after login.
//                .requestCache().requestCache(new CustomRequestCache())
//
//                // Restrict access to our application.
//                .and().authorizeRequests()
//
//                // Allow all Vaadin internal requests.
//                .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()
//
//                // Allow all requests by logged-in users.
//                .anyRequest().authenticated()
//
//                // Configure the login page.
//                .and().formLogin()
//                .loginPage(LOGIN_URL).permitAll()
//                .loginProcessingUrl(LOGIN_PROCESSING_URL)
//                .failureUrl(LOGIN_FAILURE_URL)
//
//                // Configure logout
//                .and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL);
//    }

    @Override
    public void configure(WebSecurity web) throws Exception {

//        super.configure(web);
        web.ignoring().antMatchers("/images/*.*", "/ui/*.*"
                ,"/favicon.ico", "/images/logos/*.*", "/images/logos/*", "/sw.js","/VAADIN/**",
                "/manifest.webmanifest",
                "/sw.js",
                "/offline.html",

                // icons and images
                "/icons/**",
                "/images/**",
                "/styles/**"
                );

//        web.ignoring().antMatchers(
//                // Client-side JS
//                "/VAADIN/**",
//
//                // the standard favicon URI
//                "/favicon.ico",
//
//                // the robots exclusion standard
//                "/robots.txt",
//
//                // web application manifest
//                "/manifest.webmanifest",
//                "/sw.js",
//                "/offline.html",
//
//                // icons and images
//                "/icons/**",
//                "/images/**",
//                "/styles/**",
//
//                // (development mode) H2 debugging console
//                "/h2-console/**");
    }
}
