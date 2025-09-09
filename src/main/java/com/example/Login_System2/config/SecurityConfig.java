package com.example.Login_System2.config;

import com.example.Login_System2.infrastructure.config.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/swagger-ui.html",
                    "/webjars/**",
                    "/",
                    "/register",
                    "/login",
                    "/admin",
                    "/admin/logs",
                    "/manager",
                    "/dashboard",
                    "/tasks",
                    "/logs",
                    "/notifications",
                    "/favicon.ico",         
                    "/profile",
                    "/css/**",
                    "/js/**",
                    "/images/**"
                ).permitAll()
                .requestMatchers("/api/tasks/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                .requestMatchers("/api/user/profile/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                .requestMatchers("/api/task-assignments/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                .requestMatchers("/api/task-comments/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                .requestMatchers("/api/notifications/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                .requestMatchers("/api/task-logs/**").hasRole("ADMIN")
                .requestMatchers("/api/auth/users/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        // Security headers (CSP, Frame, Referrer, Permissions)
        http.headers(h -> h
            .contentSecurityPolicy(csp -> csp.policyDirectives(
                "default-src 'self'; " +
                "img-src 'self' data:; " +
                "script-src 'self' 'unsafe-inline'; " +
                "style-src 'self' 'unsafe-inline'; " +
                "base-uri 'self'; " +
                "object-src 'none'; " +
                "frame-ancestors 'none'"
            ))
            .frameOptions(frame -> frame.deny())
            .referrerPolicy(ref -> ref.policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN))
            .addHeaderWriter(new StaticHeadersWriter("Permissions-Policy", "geolocation=(), camera=(), microphone=()"))
        );
        
        return http.build();
    }
}