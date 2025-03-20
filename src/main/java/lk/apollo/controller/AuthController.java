package lk.apollo.controller;

import lk.apollo.dto.AuthRequestDTO;
import lk.apollo.dto.AuthResponseDTO;
import lk.apollo.dto.UserDTO;
import lk.apollo.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * The type Auth controller.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    /**
     * Instantiates a new Auth controller.
     *
     * @param authService the auth service
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    /**
     * Login response entity.
     *
     * @param authRequest the auth request
     * @return the response entity
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO authRequest) {
        return ResponseEntity.ok(authService.authenticateUser(authRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(userDTO));
    }

    /**
     * Refresh token response entity.
     *
     * @param request the refresh token
     * @return the response entity
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }
}