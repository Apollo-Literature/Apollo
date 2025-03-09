package lk.apollo.service;

import io.jsonwebtoken.Claims;
import lk.apollo.dto.AuthRequestDTO;
import lk.apollo.dto.AuthResponseDTO;
import lk.apollo.dto.UserDTO;
import lk.apollo.mapper.UserMapper;
import lk.apollo.model.Role;
import lk.apollo.model.User;
import lk.apollo.repository.RoleRepository;
import lk.apollo.repository.UserRepository;
import lk.apollo.security.SupabaseTokenValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The type Auth service.
 */
@Service
public class AuthService {
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
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseApiKey);
        headers.set("Content-Type", "application/json");

        Map<String, String> requestBody = Map.of(
                "email", authRequest.getEmail(),
                "password", authRequest.getPassword()
        );

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        String authUrl = supabaseUrl + "/auth/v1/token?grant_type=password";
        ResponseEntity<Map> response = restTemplate.exchange(
                authUrl,
                HttpMethod.POST,
                entity,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        String accessToken = (String) responseBody.get("access_token");
        String refreshToken = (String) responseBody.get("refresh_token");

        // Validate and extract user information from token
        Claims claims = tokenValidator.validateToken(accessToken);
        String supabaseUserId = claims.getSubject();

        // Check if user exists in our system
        User user = userRepository.findBySupabaseUserId(supabaseUserId)
                .orElseGet(() -> {
                    // Create a new user if not exists
                    User newUser = new User();
                    newUser.setEmail(authRequest.getEmail());
                    newUser.setSupabaseUserId(supabaseUserId);
                    newUser.setActive(true);

                    // Assign default READER role
                    Role readerRole = roleRepository.findByName(RoleEnum.READER.name())
                            .orElseThrow(() -> new RuntimeException("Default reader role not found"));
                    newUser.setRoles(new HashSet<>(Collections.singletonList(readerRole)));

                    return userRepository.save(newUser);
                });

        AuthResponseDTO authResponse = new AuthResponseDTO();
        authResponse.setToken(accessToken);
        authResponse.setRefreshToken(refreshToken);
        authResponse.setUser(userMapper.toDto(user));

        return authResponse;
    }

    @Transactional
    public UserDTO registerUser(UserDTO userDTO, String password) {
        // Create user in Supabase
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseApiKey);
        headers.set("Content-Type", "application/json");

        Map<String, Object> requestBody = Map.of(
                "email", userDTO.getEmail(),
                "password", password
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String signupUrl = supabaseUrl + "/auth/v1/signup";
        ResponseEntity<Map> response = restTemplate.exchange(
                signupUrl,
                HttpMethod.POST,
                entity,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        String supabaseUserId = (String) ((Map<String, Object>) responseBody.get("user")).get("id");

        // Create user in our database
        User user = userMapper.toEntity(userDTO);
        user.setSupabaseUserId(supabaseUserId);
        user.setIsActive(true);

        // Set default role if none provided
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role readerRole = roleRepository.findByName(RoleEnum.READER.name())
                    .orElseThrow(() -> new RuntimeException("Default reader role not found"));

            Set<Role> roles = new HashSet<>();
            roles.add(readerRole);
            user.setRoles(roles);
        }

        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    /**
     * Refresh token auth response dto.
     *
     * @param refreshToken the refresh token
     * @return the auth response dto
     */
    public AuthResponseDTO refreshToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseApiKey);
        headers.set("Content-Type", "application/json");

        Map<String, String> requestBody = Map.of(
                "refresh_token", refreshToken
        );

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        String refreshUrl = supabaseUrl + "/auth/v1/token?grant_type=refresh_token";
        ResponseEntity<Map> response = restTemplate.exchange(
                refreshUrl,
                HttpMethod.POST,
                entity,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        String newAccessToken = (String) responseBody.get("access_token");
        String newRefreshToken = (String) responseBody.get("refresh_token");

        // Validate and extract user information from token
        Claims claims = tokenValidator.validateToken(newAccessToken);
        String supabaseUserId = claims.getSubject();

        User user = userRepository.findBySupabaseUserId(supabaseUserId)
                .orElseThrow(() -> new RuntimeException("User not found with Supabase ID: " + supabaseUserId));

        AuthResponseDTO authResponse = new AuthResponseDTO();
        authResponse.setToken(newAccessToken);
        authResponse.setRefreshToken(newRefreshToken);
        authResponse.setUser(userMapper.toDto(user));

        return authResponse;
    }
}