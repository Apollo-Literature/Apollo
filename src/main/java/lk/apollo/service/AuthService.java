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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    public AuthService(UserRepository userRepository,
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
     * Authenticate user by verifying credentials locally and via Supabase.
     */
    @Transactional
    public AuthResponseDTO authenticateUser(AuthRequestDTO authRequest) {
        // Retrieve the user from the database using the provided email
        User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify the password using BCrypt
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new ResourceInvalidException("Invalid credentials");
        }

        try {
            // Create headers for the Supabase API request
            HttpHeaders headers = new HttpHeaders();
            headers.set("apikey", supabaseApiKey);
            headers.set("Content-Type", "application/json");

            // Prepare request body with user credentials
            Map<String, String> requestBody = Map.of(
                    "email", authRequest.getEmail(),
                    "password", authRequest.getPassword()
            );

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

                AuthResponseDTO authResponse = new AuthResponseDTO();
                authResponse.setToken(accessToken);
                authResponse.setRefreshToken(refreshToken);
                UserDTO userDTO = userMapper.toDto(user);
                userDTO.setPassword(null); // Ensure password is not returned
                authResponse.setUser(userDTO);
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

    /**
     * Register a new user by signing up with Supabase and saving locally.
     */
    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {
        // Hash the provided password before saving
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

            // Extract the Supabase user ID from the response
            Map<String, Object> userMap = (Map<String, Object>) responseBody.get("user");
            if (userMap == null) {
                logger.error("Missing user object in response: {}", responseBody);
                throw new ResourceInvalidException("Registration failed: Missing user object in response");
            }
            String supabaseUserId = (String) userMap.get("id");
            if (supabaseUserId == null) {
                logger.error("Missing user ID in response: {}", responseBody);
                throw new ResourceInvalidException("Registration failed: Missing user ID in response");
            }

            // Create local User entity
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
                Set<Role> roles = new HashSet<>();
                for (RoleDTO roleDTO : userDTO.getRoles()) {
                    Role role = roleRepository.findByName(roleDTO.getName())
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleDTO.getName()));
                    roles.add(role);
                }
                user.setRoles(roles);
            }

            // Save the user locally
            user = userRepository.save(user);
            logger.info("User registered successfully: {}", user.getEmail());

            UserDTO responseDTO = userMapper.toDto(user);
            responseDTO.setPassword(null);
            return responseDTO;
        } catch (HttpClientErrorException e) {
            logger.error("Supabase registration failed: {}", e.getResponseBodyAsString());
            throw new ResourceInvalidException("Registration failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Registration error", e);
            throw new ResourceInvalidException("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Refresh the authentication token using Supabase.
     */
    public AuthResponseDTO refreshToken(String refreshToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("apikey", supabaseApiKey);
            headers.set("Content-Type", "application/json");

            Map<String, String> requestBody = Map.of("refresh_token", refreshToken);
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

    // -------------------------------------------------------------------------
    // Private Helper Methods (Supabase operations merged from SupabaseAuthService)
    // -------------------------------------------------------------------------

    /**
     * Updates a user in Supabase Auth.
     *
     * @param supabaseUserId The Supabase user ID.
     * @param userDTO        The updated user details.
     */
    private void updateUserInSupabase(String supabaseUserId, UserDTO userDTO) {
        String url = UriComponentsBuilder.fromHttpUrl(supabaseUrl)
                .path("/auth/v1/admin/users/")
                .path(supabaseUserId)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseApiKey);
        headers.set("Authorization", "Bearer " + supabaseApiKey); // CHANGED: Added authorization header
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Prepare payload (adjust as necessary)
        String payload = String.format("{\"email\": \"%s\", \"user_metadata\": {\"full_name\": \"%s\"}}",
                userDTO.getEmail(), userDTO.getFullName() != null ? userDTO.getFullName() : "");
        HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Supabase user {} updated successfully.", supabaseUserId);
            } else {
                logger.error("Failed to update Supabase user {}. Response code: {}", supabaseUserId, response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Exception while updating Supabase user " + supabaseUserId, e);
        }
    }

    /**
     * Deletes a user from Supabase Auth.
     *
     * @param supabaseUserId The Supabase user ID.
     */
    private void deleteUserInSupabase(String supabaseUserId) {
        String url = UriComponentsBuilder.fromHttpUrl(supabaseUrl)
                .path("/auth/v1/admin/users/")
                .path(supabaseUserId)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseApiKey);
        headers.set("Authorization", "Bearer " + supabaseApiKey); // CHANGED: Added authorization header

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Supabase user {} deleted successfully.", supabaseUserId);
            } else {
                logger.error("Failed to delete Supabase user {}. Response code: {}", supabaseUserId, response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Exception while deleting Supabase user " + supabaseUserId, e);
        }
    }

    // -------------------------------------------------------------------------
    // Public Methods for Combined Local and Supabase Operations
    // -------------------------------------------------------------------------

    /**
     * Updates a user's profile locally and in Supabase Auth.
     */
    @Transactional
    public UserDTO updateAuthUser(UserDTO userDTO) {
        User existingUser = userRepository.findById(userDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userDTO.getUserId()));

        userMapper.updateUserFromDtoIgnoreEmail(userDTO, existingUser);
        User savedUser = userRepository.save(existingUser);

        // CHANGED: Propagate update to Supabase Auth
        updateUserInSupabase(savedUser.getSupabaseUserId(), userMapper.toDto(savedUser));
        return userMapper.toDto(savedUser);
    }

    /**
     * Deletes a user locally and in Supabase Auth.
     */
    @Transactional
    public void deleteAuthUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Delete locally
        userRepository.delete(user);
        // CHANGED: Propagate deletion to Supabase Auth
        deleteUserInSupabase(user.getSupabaseUserId());
    }
}
