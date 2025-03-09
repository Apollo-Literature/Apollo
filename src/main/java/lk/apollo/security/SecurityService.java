package lk.apollo.security;

import lk.apollo.model.User;
import lk.apollo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

        if (!authentication.isAuthenticated()) {
            logger.debug("Authentication is not authenticated");
            return false;
        }

        logger.debug("Principal: {}", authentication.getPrincipal());
        logger.debug("Authorities: {}", authentication.getAuthorities());

        String supabaseUserId = (String) authentication.getPrincipal();
        if (supabaseUserId == null) {
            logger.debug("Supabase user ID is null");
            return false;
        }

        Optional<User> userOptional = userRepository.findBySupabaseUserId(supabaseUserId);
        if (userOptional.isEmpty()) {
            logger.debug("No user found with Supabase ID: {}", supabaseUserId);
            return false;
        }

        User user = userOptional.get();
        logger.debug("Found user with ID: {}", user.getUserId());

        // Compare internal Long ID with the path Long ID
        boolean result = user.getUserId().equals(id);
        logger.debug("Is current user: {}", result);
        return result;
    }

}