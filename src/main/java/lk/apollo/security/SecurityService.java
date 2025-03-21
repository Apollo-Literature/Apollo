package lk.apollo.security;

import lk.apollo.model.User;
import lk.apollo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class SecurityService {

    private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);
    private final UserRepository userRepository;

    public SecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isCurrentUser(Long id) {
        logger.debug("Checking if current user has ID: {}", id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            logger.debug("Authentication is null");
            return false;
        }

        String supabaseUserId = null;

        // Handle different authentication types
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
            Jwt jwt = jwtAuth.getToken();
            supabaseUserId = jwt.getClaimAsString("sub");
            logger.debug("Extracted Supabase user ID from JWT: {}", supabaseUserId);
        }
        else if (authentication instanceof UsernamePasswordAuthenticationToken) {
            // Check if the principal is a Map that contains user details
            Object principal = authentication.getPrincipal();
            logger.debug("Principal type: {}", principal != null ? principal.getClass().getName() : "null");

            if (principal instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> userDetails = (Map<String, Object>) principal;
                supabaseUserId = (String) userDetails.get("sub");
                logger.debug("Extracted Supabase user ID from user details map: {}", supabaseUserId);

                // If sub is not available in the map, try to find the user by email
                if (supabaseUserId == null) {
                    String email = (String) userDetails.get("email");
                    if (email != null) {
                        logger.debug("No sub claim found, using email instead: {}", email);
                        Optional<User> userByEmail = userRepository.findByEmail(email);
                        if (userByEmail.isPresent()) {
                            boolean result = userByEmail.get().getUserId().equals(id);
                            logger.debug("Found user by email, ID match: {}", result);
                            return result;
                        }
                    }
                }
            }
            else if (principal instanceof String) {
                // If principal is just a String, try using it directly as an email or username
                String principalStr = (String) principal;
                logger.debug("Principal is string: {}", principalStr);

                // Try to find user by email first
                Optional<User> userByEmail = userRepository.findByEmail(principalStr);
                if (userByEmail.isPresent()) {
                    boolean result = userByEmail.get().getUserId().equals(id);
                    logger.debug("Found user by email, ID match: {}", result);
                    return result;
                }

                // If not found by email, try using it as the Supabase ID
                supabaseUserId = principalStr;
            }
            else {
                logger.error("Unexpected principal type: {}",
                        principal != null ? principal.getClass().getName() : "null");
                return false;
            }
        }
        else {
            logger.error("Unexpected authentication type: {}", authentication.getClass().getName());
            return false;
        }

        if (supabaseUserId == null) {
            logger.error("Could not extract Supabase user ID from authentication");
            return false;
        }

        // Get user from database by Supabase ID
        Optional<User> userOptional = userRepository.findBySupabaseUserId(supabaseUserId);
        if (userOptional.isEmpty()) {
            logger.debug("No user found with Supabase ID: {}", supabaseUserId);
            return false;
        }

        User user = userOptional.get();
        logger.debug("Found user from DB - ID: {}, Supabase ID: {}",
                user.getUserId(), user.getSupabaseUserId());

        // Compare the database ID with the requested ID
        boolean result = user.getUserId().equals(id);
        logger.debug("Comparison result: Database ID {} {} requested ID {}",
                user.getUserId(), result ? "matches" : "does not match", id);

        return result;
    }
}