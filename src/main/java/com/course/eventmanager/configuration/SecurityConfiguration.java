package com.course.eventmanager.configuration;

import com.course.eventmanager.security.CustomAccessDeniedHandler;
import com.course.eventmanager.security.CustomAuthenticatorEntryPoint;
import com.course.eventmanager.security.CustomUserDetailService;
import com.course.eventmanager.security.jwt.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
public class SecurityConfiguration {

    private final CustomUserDetailService customUserDetailService;
    private final JwtTokenFilter jwtTokenFilter;
    private final CustomAuthenticatorEntryPoint authenticatorEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfiguration(CustomUserDetailService customUserDetailService, JwtTokenFilter jwtTokenFilter, CustomAuthenticatorEntryPoint authenticatorEntryPoint, CustomAccessDeniedHandler accessDeniedHandler) {
        this.customUserDetailService = customUserDetailService;
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
                                .requestMatchers("/css/**",
                                        "/js/**",
                                        "/img/**",
                                        "/lib/**",
                                        "/favicon.ico",
                                        "/swagger-ui/**",
                                        "/v2/api-docs",
                                        "/v3/api-docs",
                                        "/configuration/ui",
                                        "/swagger-resources/**",
                                        "/configuration/security",
                                        "/swagger-ui.html",
                                        "/webjars/**",
                                        "/v3/api-docs/swagger-config",
                                        "/event-manager-openapi.yaml")
                                .permitAll()

                                .requestMatchers(HttpMethod.POST, "/locations")
                                .hasAnyAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/locations/**")
                                .hasAnyAuthority("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/locations/**")
                                .hasAnyAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/locations/**")
                                .hasAnyAuthority("ADMIN", "USER")

                                .requestMatchers(HttpMethod.POST, "/users")
                                .permitAll()
                                .requestMatchers(HttpMethod.POST, "/users/auth")
                                .permitAll()

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

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
