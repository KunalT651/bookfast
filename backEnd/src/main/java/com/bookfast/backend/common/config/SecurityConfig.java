
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
            // Fallback: Allow localhost for development and Vercel domains for production
            allowedOrigins.add("http://localhost:4200");
            allowedOrigins.add("http://localhost:3000");
            // Add Vercel pattern - matches any Vercel deployment
            String vercelUrl = System.getenv("FRONTEND_URL");
            if (vercelUrl != null && !vercelUrl.isEmpty()) {
                allowedOrigins.add(vercelUrl);
            }
        }
        
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type", "X-XSRF-TOKEN"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // Cache preflight for 1 hour
        
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
                                "/api/reviews",
                                "/api/reviews/**",
                                "/api/payments/create-intent",
                                "/api/bookings", // Allow POST for bookings without CSRF
                                "/api/bookings/*/cancel", // Allow cancel booking without CSRF
                                "/api/bookings/provider/*/edit/*", // Allow provider edit booking without CSRF
                                "/api/bookings/provider/*/cancel/*", // Allow provider cancel booking without CSRF
                                "/api/resources", // Allow POST/PUT/DELETE for resources without CSRF
                                "/api/resources/*/availability", // Allow availability operations without CSRF
                                "/api/admin/**", // Allow all admin operations without CSRF
                                "/api/customers/**", // Allow customer operations without CSRF (for PUT/PATCH requests)
                                "/api/test",
                                "/api/database",
                                "/api/test-resource",
                                "/api/cleanup"
                        )
                )
                .cors(withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow all OPTIONS requests for CORS preflight
                        .requestMatchers(HttpMethod.GET, "/api/categories", "/api/resources").permitAll()
                        .requestMatchers("/uploads/**").permitAll() // Allow public access to uploaded images
                        .requestMatchers("/api/auth", "/api/auth/**", "/api/admin/create-admin", "/api/admin/check-admin-exists", "/api/test", "/api/database", "/api/test-resource", "/api/cleanup").permitAll()
                        .requestMatchers("/api/calendar/**").authenticated() // Calendar endpoints require authentication
                        
                        // Admin endpoints (most specific first)
                        .requestMatchers("/api/admin/categories/**").hasRole("ADMIN") // Admin category management
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // All other admin operations
                        
                        // Provider endpoints
                        .requestMatchers("/api/provider/**").hasRole("PROVIDER") // All provider operations
                        
                        // Provider booking endpoints (specific first)
                        .requestMatchers("/api/bookings/provider/**").hasRole("PROVIDER") // All provider booking operations
                        .requestMatchers(HttpMethod.GET, "/api/bookings/provider/me").hasRole("PROVIDER") // Providers can view their bookings
                        
                        // Booking endpoints
                        .requestMatchers(HttpMethod.POST, "/api/bookings").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/bookings").authenticated() // Authenticated users can view bookings
                        .requestMatchers(HttpMethod.PUT, "/api/bookings/*/cancel").hasRole("CUSTOMER") // Customers can cancel their bookings
                        .requestMatchers(HttpMethod.DELETE, "/api/bookings/*").hasAnyRole("CUSTOMER", "PROVIDER") // Both customers and providers can delete bookings
                        
                        // Resource endpoints
                        .requestMatchers(HttpMethod.POST, "/api/resources").hasRole("PROVIDER") // Providers can create/update resources
                        .requestMatchers(HttpMethod.PUT, "/api/resources").hasRole("PROVIDER") // Providers can update resources
                        .requestMatchers(HttpMethod.DELETE, "/api/resources").hasRole("PROVIDER") // Providers can delete resources
                        .requestMatchers(HttpMethod.GET, "/api/resources/*/availability").authenticated() // Both customers and providers can view availability
                        .requestMatchers(HttpMethod.POST, "/api/resources/*/availability").hasRole("PROVIDER") // Only providers can create availability
                        .requestMatchers(HttpMethod.PUT, "/api/resources/*/availability").hasRole("PROVIDER") // Only providers can update availability
                        .requestMatchers(HttpMethod.DELETE, "/api/resources/*/availability").hasRole("PROVIDER") // Only providers can delete availability
                        
                        // Review endpoints
                        .requestMatchers("/api/reviews/provider/**").hasRole("PROVIDER") // Provider review management
                        .requestMatchers("/api/reviews", "/api/reviews/**").hasRole("CUSTOMER")
                        
                        // Other role-based endpoints
                        .requestMatchers("/api/provider").hasRole("PROVIDER")
                        .requestMatchers("/api/customer", "/api/customers/**").hasRole("CUSTOMER")
                        .requestMatchers("/api/payments/create-intent").hasRole("CUSTOMER")
                        .requestMatchers("/api/payments").authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}