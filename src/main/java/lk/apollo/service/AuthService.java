package lk.apollo.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lk.apollo.dto.AuthRequestDTO;
import lk.apollo.dto.AuthResponseDTO;
import lk.apollo.dto.RoleDTO;
import lk.apollo.dto.UserDTO;
import lk.apollo.exception.ResourceInvalidException;
import lk.apollo.exception.ResourceNotFoundException;
import lk.apollo.mapper.UserMapper;
import lk.apollo.model.Role;
import lk.apollo.model.User;
import lk.apollo.repository.RoleRepository;
import lk.apollo.repository.UserRepository;
import lk.apollo.security.SupabaseTokenValidator;
import lk.apollo.util.RoleEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The type Auth service.
 */
@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final SupabaseTokenValidator tokenValidator;
    private final RestTemplate restTemplate;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.api-key}")
    private String supabaseApiKey;

    /**
     * Instantiates a new Auth service.
     *
     * @param userRepository the user repository
     * @param roleRepository the role repository
     * @param userMapper     the user mapper
     * @param tokenValidator the token validator
     * @param restTemplate   the rest template
     */
    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserMapper userMapper,
            SupabaseTokenValidator tokenValidator,
            RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.tokenValidator = tokenValidator;
        this.restTemplate = restTemplate;
    }

    /**
     * Authenticate user auth response dto.
     *
     * @param authRequest the auth request
     * @return the auth response dto
     */
    @Transactional
    public AuthResponseDTO authenticateUser(AuthRequestDTO authRequest) {
        // Retrieve the user from the database using the provided email
        User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify the password by comparing the provided password with the stored hashed password
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new ResourceInvalidException("Invalid credentials"); // Password doesn't match
        }

        try {
            // Create headers for the Supabase API request
            HttpHeaders headers = new HttpHeaders();
            headers.set("apikey", supabaseApiKey);
            headers.set("Content-Type", "application/json");

            // Create the request body with user credentials
            Map<String, String> requestBody = Map.of(
                    "email", authRequest.getEmail(),
                    "password", authRequest.getPassword()
            );

            // Combine headers and body into a single HTTP entity
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            // Construct the Supabase authentication URL for password-based login
            String authUrl = supabaseUrl + "/auth/v1/token?grant_type=password";

            logger.debug("Sending authentication request to Supabase: {}", authUrl);

            // Send POST request to Supabase authentication endpoint
            ResponseEntity<Map> response = restTemplate.exchange(
                    authUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            // Extract authentication information from the response
            Map<String, Object> responseBody = response.getBody();

            if (responseBody == null) {
                throw new ResourceInvalidException("Authentication failed: Empty response body");
            }

            String accessToken = (String) responseBody.get("access_token");
            String refreshToken = (String) responseBody.get("refresh_token");

            if (accessToken == null || refreshToken == null) {
                logger.error("Missing tokens in response: {}", responseBody);
                throw new ResourceInvalidException("Authentication failed: Missing tokens in response");
            }

            // Validate the access token and extract claims
            try {
                logger.debug("Validating access token");
                Claims claims = tokenValidator.validateToken(accessToken);
                String supabaseUserId = claims.getSubject();
                logger.debug("Token validated successfully for user ID: {}", supabaseUserId);

                // Set up the response DTO
                AuthResponseDTO authResponse = new AuthResponseDTO();
                authResponse.setToken(accessToken);
                authResponse.setRefreshToken(refreshToken);
                authResponse.setUser(userMapper.toDto(user));

                return authResponse;
            } catch (JwtException e) {
                logger.error("JWT validation failed: {}", e.getMessage());
                throw new ResourceInvalidException("Token validation failed: " + e.getMessage());
            }
        } catch (HttpClientErrorException e) {
            logger.error("Supabase authentication failed: {}", e.getResponseBodyAsString());
            throw new ResourceInvalidException("Authentication failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Authentication error", e);
            throw new ResourceInvalidException("Authentication failed: " + e.getMessage());
        }
    }

    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {
        // Hash the password before saving it
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(userDTO.getPassword());

        try {
            // Create user in Supabase
            HttpHeaders headers = new HttpHeaders();
            headers.set("apikey", supabaseApiKey);
            headers.set("Content-Type", "application/json");

            Map<String, Object> requestBody = Map.of(
                    "email", userDTO.getEmail(),
                    "password", userDTO.getPassword()
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String signupUrl = supabaseUrl + "/auth/v1/signup";
            logger.debug("Sending signup request to Supabase: {}", signupUrl);

            ResponseEntity<Map> response = restTemplate.exchange(
                    signupUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) {
                throw new ResourceInvalidException("Registration failed: Empty response body");
            }

            // Extract user object from response
            Map<String, Object> userMap = (Map<String, Object>) responseBody.get("user");
            if (userMap == null) {
                logger.error("Missing user object in response: {}", responseBody);
                throw new ResourceInvalidException("Registration failed: Missing user object in response");
            }

            //Extract Supabase user ID from the user object
            String supabaseUserId = (String) userMap.get("id");
            if (supabaseUserId == null) {
                logger.error("Missing user ID in response: {}", responseBody);
                throw new ResourceInvalidException("Registration failed: Missing user ID in response");
            }

            // Create User entity with additional details
            User user = new User();
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setEmail(userDTO.getEmail());
            user.setPassword(hashedPassword);
            user.setSupabaseUserId(supabaseUserId);
            user.setDateOfBirth(userDTO.getDateOfBirth());
            user.setIsActive(true);

            // Set default role if none provided
            if (userDTO.getRoles() == null || userDTO.getRoles().isEmpty()) {
                Role readerRole = roleRepository.findByName(RoleEnum.READER.name())
                        .orElseThrow(() -> new RuntimeException("Default reader role not found"));

                Set<Role> roles = new HashSet<>();
                roles.add(readerRole);
                user.setRoles(roles);
            } else {
                // If roles are provided in the DTO, map them
                Set<Role> roles = new HashSet<>();
                for (RoleDTO roleDTO : userDTO.getRoles()) {
                    Role role = roleRepository.findByName(roleDTO.getName())
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleDTO.getName()));
                    roles.add(role);
                }
                user.setRoles(roles);
            }

            // Save the user to the database
            user = userRepository.save(user);
            logger.info("User registered successfully: {}", user.getEmail());

            // Return the user as DTO
            return userMapper.toDto(user);
        } catch (HttpClientErrorException e) {
            logger.error("Supabase registration failed: {}", e.getResponseBodyAsString());
            throw new ResourceInvalidException("Registration failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Registration error", e);
            throw new ResourceInvalidException("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Refresh token auth response dto.
     *
     * @param refreshToken the refresh token
     * @return the auth response dto
     */
    public AuthResponseDTO refreshToken(String refreshToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("apikey", supabaseApiKey);
            headers.set("Content-Type", "application/json");

            Map<String, String> requestBody = Map.of(
                    "refresh_token", refreshToken
            );

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            String refreshUrl = supabaseUrl + "/auth/v1/token?grant_type=refresh_token";
            logger.debug("Sending refresh token request to Supabase: {}", refreshUrl);

            ResponseEntity<Map> response = restTemplate.exchange(
                    refreshUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) {
                throw new ResourceInvalidException("Token refresh failed: Empty response body");
            }

            String newAccessToken = (String) responseBody.get("access_token");
            String newRefreshToken = (String) responseBody.get("refresh_token");

            if (newAccessToken == null || newRefreshToken == null) {
                logger.error("Missing tokens in refresh response: {}", responseBody);
                throw new ResourceInvalidException("Token refresh failed: Missing tokens in response");
            }

            // Validate and extract user information from token
            try {
                Claims claims = tokenValidator.validateToken(newAccessToken);
                String supabaseUserId = claims.getSubject();

                User user = userRepository.findBySupabaseUserId(supabaseUserId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with Supabase ID: " + supabaseUserId));

                AuthResponseDTO authResponse = new AuthResponseDTO();
                authResponse.setToken(newAccessToken);
                authResponse.setRefreshToken(newRefreshToken);
                authResponse.setUser(userMapper.toDto(user));

                return authResponse;
            } catch (JwtException e) {
                logger.error("JWT validation failed during token refresh: {}", e.getMessage());
                throw new ResourceInvalidException("Token validation failed: " + e.getMessage());
            }
        } catch (HttpClientErrorException e) {
            logger.error("Supabase token refresh failed: {}", e.getResponseBodyAsString());
            throw new ResourceInvalidException("Token refresh failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Token refresh error", e);
            throw new ResourceInvalidException("Token refresh failed: " + e.getMessage());
        }
    }
}