package lk.apollo.security;

import io.jsonwebtoken.Claims;
import lk.apollo.exception.ResourceNotFoundException;
import lk.apollo.exception.user.AccessDeniedException;
import lk.apollo.model.User;
import lk.apollo.model.Role;
import lk.apollo.model.Permission;
import lk.apollo.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * The type Supabase authentication provider.
 */
@Component
public class SupabaseAuthenticationProvider implements AuthenticationProvider {
    private final SupabaseTokenValidator tokenValidator;
    private final UserRepository userRepository;


    /**
     * Instantiates a new Supabase authentication provider.
     *
     * @param tokenValidator the token validator
     * @param userRepository the user repository
     */
    public SupabaseAuthenticationProvider(SupabaseTokenValidator tokenValidator, UserRepository userRepository) {
        this.tokenValidator = tokenValidator;
        this.userRepository = userRepository;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();
        return validateToken(token);
    }

    /**
     * Validate token authentication.
     *
     * @param token the token
     * @return the authentication
     */
    public Authentication validateToken(String token) {
        Claims claims = tokenValidator.validateToken(token);
        String supabaseUserId = claims.getSubject();

        Optional<User> userOptional = userRepository.findBySupabaseUserId(supabaseUserId);

        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found in the system");
        }

        User user = userOptional.get();

        if (!user.getIsActive()) {
            throw new AccessDeniedException("User account is deactivated");
        }

        Collection<GrantedAuthority> authorities = new ArrayList<>();

        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));

            for (Permission permission : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }

        return new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                token,
                authorities
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
