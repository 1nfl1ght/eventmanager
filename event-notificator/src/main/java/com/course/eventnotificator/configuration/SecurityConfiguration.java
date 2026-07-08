package com.course.eventnotificator.configuration;

import com.course.eventnotificator.security.CustomAccessDeniedHandler;
import com.course.eventnotificator.security.CustomAuthenticatorEntryPoint;
import com.course.eventnotificator.security.jwt.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
public class SecurityConfiguration {

    private final JwtTokenFilter jwtTokenFilter;
    private final CustomAuthenticatorEntryPoint authenticatorEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfiguration(JwtTokenFilter jwtTokenFilter, CustomAuthenticatorEntryPoint authenticatorEntryPoint, CustomAccessDeniedHandler accessDeniedHandler) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.authenticatorEntryPoint = authenticatorEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers(HttpMethod.GET, "/notifications/**")
                                .hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.POST, "/notifications/**")
                                .hasAnyAuthority("ADMIN", "USER")

                                .anyRequest().authenticated()

                )
                .exceptionHandling(exception -> {
                    exception
                            .authenticationEntryPoint(authenticatorEntryPoint)
                            .accessDeniedHandler(accessDeniedHandler);
                })
                .addFilterBefore(jwtTokenFilter, AnonymousAuthenticationFilter.class)
                .build();
    }
}
