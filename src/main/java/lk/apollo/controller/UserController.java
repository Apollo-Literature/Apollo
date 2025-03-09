package lk.apollo.controller;

import lk.apollo.dto.UserDTO;
import lk.apollo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get all users
     * @return List of UserDTO
     */
    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Get user by ID
     * @param id - Long id
     * @return UserDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));

    }

    /**
     * Get user by email
     * @param email - String email
     * @return UserDTO
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Add a new user
     * @param userDTO - UserDTO instance
     * @return UserDTO
     */
    @PostMapping("/add-user")
    public ResponseEntity<UserDTO> addUser(@RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.addUser(userDTO);
        return ResponseEntity.ok(createdUser);
    }

    /**
     * Update an existing user
     * @param id - Long user ID
     * @param userDTO - Updated user details
     * @return Updated UserDTO
     */
    @PutMapping("/update-user")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(userDTO));
    }

    /**
     * Delete a user
     * @param id - Long id
     * @return ResponseEntity with status
     */
    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
