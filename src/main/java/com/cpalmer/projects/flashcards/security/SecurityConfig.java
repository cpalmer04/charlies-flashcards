package com.cpalmer.projects.flashcards.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * The security configuration for the flashcards application
 * I created this class from a mixture of YouTube tutorials and online articles that allowed me to
 * customize it to this application's needs. I am not quite sure the industry standard, but I used
 * BCryptPasswordEncoder to hash the passwords to securely store them in the DB, DaoAuthenticationProvider
 * to use the JWT authentication filter and custom user details service. Additionally, I set up the
 * authentication filter to allow the endpoints that don't need authentication like login and signup
 * and also set up CORS to allow the frontend to make requests to the backend.
 *
 * @author cpalmer
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // The custom user details service
    private final FlashcardUserDetailsService userDetailsService;

    // Our authentication to validate tokens
    private final JwtAuthenticationFilter jwtAuthFilter;

    /**
     * Construct an instance
     *
     * @param userDetailsService not null
     * @param jwtAuthFilter not null
     */
    public SecurityConfig(FlashcardUserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * The security filter chain which defines how we handle our incoming HTTP requests
     * This is used to allow our endpoints that don't need to be secured like login and signup,
     * require authentication from the rest and also use our JWT authentication filter to validate tokens
     *
     * @param http the HTTP security to configure
     * @return the security filter chain
     * @throws Exception if something goes wrong
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        // We never want to create or use any HTTP sessions - instead we want to use JWT token
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                // All the URL links that are safe go here
                                // These don't require authentication to access
                                "/api/signup",
                                "/api/login",
                                "/signup.html",
                                "/login.html",
                                "decks.html", // Needed to access decks after login, if we go here otherwise we get redirected
                                "flashcards.html", // Same as above - we will just get redirected if not logged in already
                                "/css/**",
                                "/js/**",
                                "/actuator/prometheus" // For prometheus metrics collection. Go here to take a look at custom counters.
                        ).permitAll()
                        .anyRequest().authenticated() // Any other request needs to be authenticated
                )
                .authenticationProvider(authenticationProvider()) // Use DaoAuthenticationProvider - to my knowledge this is the gold standard
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:29001")); // The frontend to make requests
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allowed HTTP methods from our frontend
        configuration.setAllowedHeaders(List.of("*")); // Any header is fine here
        configuration.setAllowCredentials(true); // Could be useful in the future if introducing cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply this configuration to everything
        return source;
    }

    /**
     * This class specifies the password encoder to be used by the application.
     * The password encoder is responsible for hashing passwords so that they aren't visible to any person
     * who may gain access to the database.
     *
     * @return A {@link BCryptPasswordEncoder} instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // I believe to be the current gold standard for hashing the password
    }

    /**
     * Delegates the authentication method used by our login method to the authentication provider.
     * In this case, using the DaoAuthenticationProvider.
     * See {@link #authenticationProvider()}
     *
     * @param config the authentication configuration
     * @return the authentication manager
     * @throws Exception if it cannot be gotten
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Specifies the DaoAuthenticationProvider that uses our custom user details service for the flashcards
     * application and also sets the password encoder to be our defined BCrypt one.
     *
     * @return the authentication provider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService); // DaoAuthenticationProvider seems to be standard
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}