package lk.apollo.config;

import lk.apollo.security.SupabaseAuthenticationFilter;
import lk.apollo.security.SupabaseAuthenticationProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final SupabaseAuthenticationProvider authenticationProvider;
    private final SupabaseAuthenticationFilter authenticationFilter;
    private final String[] allowedOrigins;

    public SecurityConfig(SupabaseAuthenticationProvider authenticationProvider,
                          SupabaseAuthenticationFilter authenticationFilter,
                          @Value("${security.allowed-origins}") String allowedOriginsStr) {
        this.authenticationProvider = authenticationProvider;
        this.authenticationFilter = authenticationFilter;
        this.allowedOrigins = allowedOriginsStr.split(",");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Disable CSRF protection
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // Configure CORS
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Stateless session management
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/books/all",
                                "/books/search",
                                "/books/{id}",
                                "/auth/**",
                                "/swagger-ui/**",
                                "/error",
                                "/favicon.ico",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui.html",
                                "/webjars/**",
                                "/v3/api-docs/**",
                                "/v2/api-docs/**",
                                "/api/public/**",
                                "/api/public/authenticate",
                                "/actuator/*"
                                ).permitAll()  // Permit these endpoints
                        .anyRequest().authenticated()  // Require authentication for other endpoints
                )
                .authenticationProvider(authenticationProvider) // Register the custom provider
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);  // Add custom filter before the authentication filter



        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}