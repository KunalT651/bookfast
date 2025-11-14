
package com.bookfast.backend.common.config;

import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import com.bookfast.backend.common.auth.service.JwtAuthenticationFilter;
import com.bookfast.backend.common.auth.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

@Configuration
public class SecurityConfig {
    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOrigins(java.util.List.of("http://localhost:4200"));
        configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type", "X-XSRF-TOKEN"));
        configuration.setAllowCredentials(true);
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtService jwtService) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/register-provider",
                                "/api/auth/logout",
                                "/api/reviews/**",
                                "/api/payments/create-intent",
                                "/api/bookings", // Allow POST for bookings without CSRF
                                "/api/admin/create-admin",
                                "/api/admin/check-admin-exists",
                                "/api/test/**",
                                "/api/database/**",
                                "/api/test-resource/**",
                                "/api/cleanup/**"
                        )
                )
                .cors(withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/categories", "/api/resources/**").permitAll()
                        .requestMatchers("/api/auth/**", "/api/auth/logout", "/api/admin/create-admin", "/api/admin/check-admin-exists", "/api/test/**", "/api/database/**", "/api/test-resource/**", "/api/cleanup/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/bookings").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/bookings/**").authenticated() // Authenticated users can view bookings
                        .requestMatchers("/api/admin/categories").hasRole("ADMIN") // Ensure admin categories are protected
                        .requestMatchers("/api/resources/**").hasRole("PROVIDER") // Providers manage their resources
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/provider/**").hasRole("PROVIDER")
                        .requestMatchers("/api/customer/**").hasRole("CUSTOMER")
                        .requestMatchers("/api/reviews/**").hasRole("CUSTOMER")
                        .requestMatchers("/api/payments/create-intent").hasRole("CUSTOMER")
                        .requestMatchers("/api/payments/**").authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}