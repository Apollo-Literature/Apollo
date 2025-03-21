package lk.apollo.controller;

import lk.apollo.dto.UserDTO;
import lk.apollo.service.UserService;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The type User controller.
 */
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(UserController.class);

    /**
     * Instantiates a new User controller.
     *
     * @param userService the user service
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * Gets all users.
     *
     * @return the all users
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("hasAuthority('ADMIN') or @securityService.isCurrentUser(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Create user response entity.
     *
     * @param userDTO the user dto
     * @return the response entity
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDTO));
    }

    /**
     * Update user response entity.
     *
     * @param userDTO the user dto
     * @return the response entity
     */
    @PreAuthorize("hasAuthority('ADMIN') or @securityService.isCurrentUser(#userDTO.userId)")
    @PutMapping("/update-user")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO) {
        log.info("Updating user with ID: {}", userDTO.getUserId());
        return ResponseEntity.ok(userService.updateUser(userDTO));
    }

    /**
     * Delete user response entity.
     *
     * @param id the id
     * @return the response entity
     */
    @PreAuthorize("hasAuthority('ADMIN') or @securityService.isCurrentUser(#id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deactivate user response entity.
     *
     * @param id the id
     * @return the response entity
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<UserDTO> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }

    /**
     * Activate user response entity.
     *
     * @param id the id
     * @return the response entity
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}/activate")
    public ResponseEntity<UserDTO> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    /**
     * Assign role to user response entity.
     *
     * @param userId the user id
     * @param roleId the role id
     * @return the response entity
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<UserDTO> assignRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        return ResponseEntity.ok(userService.assignRoleToUser(userId, roleId));
    }

    /**
     * Remove role from user response entity.
     *
     * @param userId the user id
     * @param roleId the role id
     * @return the response entity
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<UserDTO> removeRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId) {
        return ResponseEntity.ok(userService.removeRoleFromUser(userId, roleId));
    }
}