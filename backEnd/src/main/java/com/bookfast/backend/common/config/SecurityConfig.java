
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
        
        // Read from CORS_ALLOWED_ORIGINS environment variable
        String corsOrigins = System.getenv("CORS_ALLOWED_ORIGINS");
        java.util.List<String> allowedOrigins = new java.util.ArrayList<>();

        if (corsOrigins != null && !corsOrigins.isEmpty()) {
            // Split by comma and add all origins
            String[] origins = corsOrigins.split(",");
            for (String origin : origins) {
                allowedOrigins.add(origin.trim());
            }
        } else {
            // Fallback to localhost for development
            allowedOrigins.add("http://localhost:4200");
        }

        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type", "X-XSRF-TOKEN"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // Cache preflight for 1 hour
        
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source; // Production CORS configuration
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtService jwtService) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers(
                                "/api/auth/**",
                                "/api/admin/**", // Allow all admin operations without CSRF
                                "/api/reviews/**",
                                "/api/payments/create-intent",
                                "/api/bookings/**", // Allow all booking operations without CSRF
                                "/api/resources/**", // Allow all resource operations without CSRF
                                "/api/customers/**", // Allow customer operations without CSRF
                                "/api/provider/**", // Allow provider operations without CSRF
                                "/api/test/**",
                                "/api/database/**",
                                "/api/test-resource/**",
                                "/api/cleanup/**"
                        )
                )
                .cors(withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll() // Allow access to uploaded files
                        .requestMatchers(HttpMethod.GET, "/api/categories", "/api/resources/**").permitAll()
                        .requestMatchers("/api/auth/**", "/api/auth/logout", "/api/admin/create-admin", "/api/admin/check-admin-exists", "/api/test/**", "/api/database/**", "/api/test-resource/**", "/api/cleanup/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/bookings").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/bookings/provider/me").hasRole("PROVIDER") // Providers can get their own bookings
                        .requestMatchers(HttpMethod.GET, "/api/bookings/**").authenticated() // Authenticated users can view bookings
                        .requestMatchers("/api/admin/categories").hasRole("ADMIN") // Ensure admin categories are protected
                        .requestMatchers("/api/resources/**").hasRole("PROVIDER") // Providers manage their resources
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/provider/**").hasRole("PROVIDER")
                        .requestMatchers("/api/customer/**").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/reviews/provider/me").hasRole("PROVIDER") // Providers can get their own reviews
                        .requestMatchers("/api/reviews/**").hasRole("CUSTOMER") // Customers can manage reviews
                        .requestMatchers("/api/payments/create-intent").hasRole("CUSTOMER")
                        .requestMatchers("/api/payments/**").authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}