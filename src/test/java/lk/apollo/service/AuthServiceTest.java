package lk.apollo.service;

import io.jsonwebtoken.Claims;
import lk.apollo.dto.AuthRequestDTO;
import lk.apollo.dto.AuthResponseDTO;
import lk.apollo.dto.RoleDTO;
import lk.apollo.dto.UserDTO;
import lk.apollo.mapper.UserMapper;
import lk.apollo.model.Role;
import lk.apollo.model.User;
import lk.apollo.repository.RoleRepository;
import lk.apollo.repository.UserRepository;
import lk.apollo.security.SupabaseTokenValidator;
import lk.apollo.util.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SupabaseTokenValidator tokenValidator;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UserDTO testUserDTO;
    private String supabaseUrl = "https://test-supabase.com";
    private String supabaseApiKey = "test-api-key";

    @BeforeEach
    void setUp() {
        // Set up mock values
        ReflectionTestUtils.setField(authService, "supabaseUrl", supabaseUrl);
        ReflectionTestUtils.setField(authService, "supabaseApiKey", supabaseApiKey);

        // Create test user
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test@example.com");
        testUser.setPassword(new BCryptPasswordEncoder().encode("password123"));
        testUser.setSupabaseUserId("test-supabase-id");
        testUser.setIsActive(true);
        testUser.setDateOfBirth(LocalDate.of(1990, 1, 1));

        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setRoleId(1L);
        role.setName(RoleEnum.READER.name());
        roles.add(role);
        testUser.setRoles(roles);

        // Create test user DTO
        testUserDTO = new UserDTO();
        testUserDTO.setUserId(1L);
        testUserDTO.setFirstName("Test");
        testUserDTO.setLastName("User");
        testUserDTO.setEmail("test@example.com");
        testUserDTO.setPassword("password123");
        testUserDTO.setDateOfBirth(LocalDate.of(1990, 1, 1));

        Set<RoleDTO> roleDTOs = new HashSet<>();
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setRoleId(1L);
        roleDTO.setName(RoleEnum.READER.name());
        roleDTOs.add(roleDTO);
        testUserDTO.setRoles(roleDTOs);
    }

    @Test
    void authenticateUser_Success() {
        // Arrange
        AuthRequestDTO authRequest = new AuthRequestDTO();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password123");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // Mock Supabase API response
        Map<String, Object> supabaseResponse = new HashMap<>();
        supabaseResponse.put("access_token", "mock-access-token");
        supabaseResponse.put("refresh_token", "mock-refresh-token");

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(supabaseResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // Mock token validation
        Claims mockClaims = mock(Claims.class);
        when(mockClaims.getSubject()).thenReturn("test-supabase-id");
        when(tokenValidator.validateToken(anyString())).thenReturn(mockClaims);

        // Mock user mapper
        when(userMapper.toDtoWithoutSensitiveFields(any(User.class))).thenReturn(testUserDTO);

        // Act
        AuthResponseDTO response = authService.authenticateUser(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals("mock-access-token", response.getToken());
        assertEquals("mock-refresh-token", response.getRefreshToken());
        assertEquals(testUserDTO, response.getUser());

        // Verify interactions
        verify(userRepository).findByEmail("test@example.com");
        verify(tokenValidator).validateToken("mock-access-token");
        verify(userMapper).toDtoWithoutSensitiveFields(testUser);
    }


    @Test
    void registerUser_Success() {
        // Arrange
        when(roleRepository.findByName(RoleEnum.READER.name())).thenReturn(Optional.of(new Role()));

        // Mock Supabase response
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", "new-supabase-id");
        Map<String, Object> supabaseResponse = new HashMap<>();
        supabaseResponse.put("user", userMap);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(supabaseResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setUserId(1L);
            return savedUser;
        });

        when(userMapper.toDtoWithoutSensitiveFields(any(User.class))).thenReturn(testUserDTO);

        // Act
        UserDTO result = authService.registerUser(testUserDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testUserDTO, result);

        // Verify
        verify(restTemplate).exchange(eq(supabaseUrl + "/auth/v1/signup"), eq(HttpMethod.POST), any(), eq(Map.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void refreshToken_Success() {
        // Arrange
        String refreshToken = "test-refresh-token";

        // Mock Supabase API response
        Map<String, Object> supabaseResponse = new HashMap<>();
        supabaseResponse.put("access_token", "new-access-token");
        supabaseResponse.put("refresh_token", "new-refresh-token");

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(supabaseResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // Mock token validation
        Claims mockClaims = mock(Claims.class);
        when(mockClaims.getSubject()).thenReturn("test-supabase-id");
        when(tokenValidator.validateToken(anyString())).thenReturn(mockClaims);

        // Mock user repository
        when(userRepository.findBySupabaseUserId(anyString())).thenReturn(Optional.of(testUser));

        // Mock user mapper
        when(userMapper.toDto(any(User.class))).thenReturn(testUserDTO);

        // Act
        AuthResponseDTO response = authService.refreshToken(refreshToken);

        // Assert
        assertNotNull(response);
        assertEquals("new-access-token", response.getToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
        assertEquals(testUserDTO, response.getUser());

        // Verify interactions
        verify(restTemplate).exchange(eq(supabaseUrl + "/auth/v1/token?grant_type=refresh_token"), eq(HttpMethod.POST), any(), eq(Map.class));
        verify(tokenValidator).validateToken("new-access-token");
        verify(userRepository).findBySupabaseUserId("test-supabase-id");
    }

    @Test
    void updateUserInSupabase_Success() {
        // Arrange
        String supabaseUserId = "test-supabase-id";

        ResponseEntity<String> responseEntity = new ResponseEntity<>("Success", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Act
        authService.updateUserInSupabase(supabaseUserId, testUserDTO);

        // Verify
        verify(restTemplate).exchange(contains(supabaseUrl + "/auth/v1/admin/users/" + supabaseUserId),
                eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void deleteUserInSupabase_Success() {
        // Arrange
        String supabaseUserId = "test-supabase-id";

        ResponseEntity<String> responseEntity = new ResponseEntity<>("Success", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Act
        authService.deleteUserInSupabase(supabaseUserId);

        // Verify
        verify(restTemplate).exchange(contains(supabaseUrl + "/auth/v1/admin/users/" + supabaseUserId),
                eq(HttpMethod.DELETE), any(HttpEntity.class), eq(String.class));
    }
}