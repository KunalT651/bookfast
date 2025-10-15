package com.bookfast.backend.common.config;

import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .cors(withDefaults())
        .authorizeHttpRequests(auth -> auth
            // .requestMatchers(HttpMethod.GET, "/api/admin/categories").permitAll() // <-- add this line
            // .requestMatchers("/api/auth/**", "/api/services/categories").permitAll()
            // .requestMatchers("/api/admin/**").hasRole("ADMIN")
            // .requestMatchers("/api/provider/**").hasRole("PROVIDER")
            // .requestMatchers("/api/customer/**").hasRole("CUSTOMER")
            // .anyRequest().authenticated()
            .requestMatchers("/api/admin/categories/**").permitAll() // <-- allow all methods for categories
            .requestMatchers("/api/auth/**", "/api/services/categories").permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/provider/**").hasRole("PROVIDER")
            .requestMatchers("/api/customer/**").hasRole("CUSTOMER")
            .anyRequest().authenticated()
        );
    return http.build();
}
}