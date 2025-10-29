package com.cpalmer.projects.flashcards.security;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * Custom filter that is done once per HTTP request to handle JWT authentication
 * The filter checks the 'Authorization' header for a token, validates it, and sets the
 * authenticated user if the token is valid
 *
 * @author cpalmer
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Used to handle exceptions that may come up in a standard way
    private final HandlerExceptionResolver handlerExceptionResolver;

    // Our defined JwtService for any of the operations
    private final JwtService jwtService;

    // Our custom user details service to load the custom user table data from the database
    private final FlashcardUserDetailsService userDetailsService;

    /**
     * Constructor for dependency injection.
     *
     * @param handlerExceptionResolver Component to resolve exceptions
     * @param jwtService Service to handle JWT token operations
     * @param userDetailsService Service to fetch user data from the database
     */
    public JwtAuthenticationFilter(HandlerExceptionResolver handlerExceptionResolver, JwtService jwtService,
                                   FlashcardUserDetailsService userDetailsService) {
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * The core logic for processing the incoming request.
     *
     * @param request The incoming HTTP request.
     * @param response The HTTP response.
     * @param filterChain The chain of filters to be executed.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);

            String username = jwtService.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, userDetails)) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // Delegate any exception to the defined resolver
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }

}