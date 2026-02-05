package com.vikas.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Added for cleaner code
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurity {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Keep disabled for Postman testing
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/public/**").permitAll()
                        // 1. ADMIN ZONE: Lock down EVERYTHING under /admin to the ADMIN role
                        // This covers /admin/users, /admin/create-contest, etc.
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 2. CONTEST REGISTRATION: Any authenticated user can register
                        // Specific enough to not conflict with GET requests
                        .requestMatchers(HttpMethod.POST, "/contests/*/register").authenticated()

                        // 3. CONTEST VIEWING: Anyone (even not logged in) can view contests
                        .requestMatchers(HttpMethod.GET, "/contests/**").permitAll()

                        // 4. USER PROFILE: Specific user endpoints
                        .requestMatchers("/user/**").authenticated()

                        // 5. FALLBACK: Secure by default. Anything else requires authentication.
                        // Change 'permitAll()' to 'authenticated()' for better security.
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Still using NoOp since you're currently storing passwords as plain text
        return NoOpPasswordEncoder.getInstance();
    }
}