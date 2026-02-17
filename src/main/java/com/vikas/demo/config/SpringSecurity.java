package com.vikas.demo.config;

import com.vikas.demo.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SpringSecurity {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                // 1. Disable CSRF for stateless APIs
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 2. Public Endpoints (No Token Required)
                        .requestMatchers("/signup/**", "/public/**", "/badge/**").permitAll()

                        // 3. Role-based Endpoints
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 4. Protected Endpoints (JWT Token Required)
                        .requestMatchers(HttpMethod.POST, "/contests/*/register").authenticated()
                        .requestMatchers(HttpMethod.POST, "/profile").authenticated()
                        .requestMatchers(HttpMethod.GET, "/contests/**").permitAll()
                        .requestMatchers("/user/**").authenticated()

                        // 5. Fallback
                        .anyRequest().authenticated())

                // 6. IMPORTANT: Set Session to Stateless (No Sessions/Cookies)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 7. REMOVED: .httpBasic() - This is what stops the Basic Auth popup!

        // 8. Add JWT Filter before the standard UsernamePassword filter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}