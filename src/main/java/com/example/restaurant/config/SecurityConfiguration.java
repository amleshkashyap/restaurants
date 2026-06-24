package com.example.restaurant.config;

import com.example.restaurant.filter.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfiguration(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz ->
                        authz
                                .requestMatchers("/api/v1/users/auth/login").permitAll()
                                .requestMatchers("/swagger-ui/**", "swagger-ui.html", "/v3/api-docs/**").permitAll()
                                .requestMatchers(HttpMethod.GET,"/api/v1/users/{id}").authenticated()
                                .requestMatchers("/api/v1/users").hasAuthority("ROLE_ADMIN")
                                .requestMatchers("/api/v1/users/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.POST,"/api/v1/restaurants").hasAnyAuthority("ROLE_ADMIN", "ROLE_OWNER")
                                .requestMatchers(HttpMethod.PUT, "/api/v1/restaurants/{id}").hasAnyAuthority("ROLE_ADMIN", "ROLE_OWNER")
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/restaurants/{id}").hasAnyAuthority("ROLE_ADMIN", "ROLE_OWNER")
                                .requestMatchers("/api/v1/restaurants/{restaurantId}/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_OWNER")
                                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(this.jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}