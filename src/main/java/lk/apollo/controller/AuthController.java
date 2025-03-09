package lk.apollo.controller;

import lk.apollo.dto.AuthRequestDTO;
import lk.apollo.dto.AuthResponseDTO;
import lk.apollo.dto.UserDTO;
import lk.apollo.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The type Auth controller.
 */
@RestController
@RequestMapping("/api/auth")
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
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO, @RequestParam String password) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(userDTO, password));
    }

    /**
     * Refresh token response entity.
     *
     * @param refreshToken the refresh token
     * @return the response entity
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@RequestParam String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }
}