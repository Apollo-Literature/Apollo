package lk.apollo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * The type Supabase authentication filter.
 */
@Component
public class SupabaseAuthenticationFilter extends OncePerRequestFilter {
    private final SupabaseAuthenticationProvider authenticationProvider;

    /**
     * Instantiates a new Supabase authentication filter.
     *
     * @param authenticationProvider the authentication provider
     */
    public SupabaseAuthenticationFilter(SupabaseAuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            try {
                filterChain.doFilter(request, response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        String token = authHeader.substring(7);

        try {
            var auth = authenticationProvider.validateToken(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            // Log error, but continue the filter chain
            logger.error("Authentication error: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}