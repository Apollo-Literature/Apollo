package lk.apollo.service;

import lk.apollo.dto.UserDTO;
import lk.apollo.exception.user.AccessDeniedException;
import lk.apollo.exception.ResourceAlreadyExistsException;
import lk.apollo.exception.ResourceNotFoundException;
import lk.apollo.mapper.UserMapper;
import lk.apollo.model.Role;
import lk.apollo.model.User;
import lk.apollo.repository.RoleRepository;
import lk.apollo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The type User service.
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    /**
     * Instantiates a new User service.
     *
     * @param userRepository the user repository
     * @param roleRepository the role repository
     * @param userMapper     the user mapper
     */
    public UserService(UserRepository userRepository, RoleRepository roleRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }

    /**
     * Get all users
     *
     * @return List of UserDTOs
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found");
        }
        return userMapper.toDtoList(users);
    }


    /**
     * Gets user by id.
     *
     * @param id the id
     * @return the user by id
     */
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return userMapper.toDto(user);
    }

    /**
     * Gets user by supabase id.
     *
     * @param supabaseUserId the supabase user id
     * @return the user by supabase id
     */
    @Transactional(readOnly = true)
    public UserDTO getUserBySupabaseId(String supabaseUserId) {
        User user = userRepository.findBySupabaseUserId(supabaseUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Supabase ID: " + supabaseUserId));
        return userMapper.toDto(user);
    }

    /**
     * Create user user dto.
     *
     * @param userDTO the user dto
     * @return the user dto
     */
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new ResourceAlreadyExistsException("User already exists with email: " + userDTO.getEmail());
        }

        User user = userMapper.toEntity(userDTO);

        // Set default role if none provided
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role readerRole = roleRepository.findByName("READER")
                    .orElseThrow(() -> new ResourceNotFoundException("Default reader role not found"));

            Set<Role> roles = new HashSet<>();
            roles.add(readerRole);
            user.setRoles(roles);
        }

        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    /**
     * Update user user dto.
     *
     * @param userDTO the user dto
     * @return the user dto
     */
    @Transactional
    public UserDTO updateUser(UserDTO userDTO) {
        User existingUser = userRepository.findById(userDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userDTO.getUserId()));

        userMapper.updateUserFromDto(userDTO, existingUser);
        User savedUser = userRepository.save(existingUser);
        return userMapper.toDto(savedUser);
    }

    /**
     * Delete user.
     *
     * @param id the id
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Deactivate user user dto.
     *
     * @param id the id
     * @return the user dto
     */
    @Transactional
    public UserDTO deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setIsActive(false);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    /**
     * Activate user user dto.
     *
     * @param id the id
     * @return the user dto
     */
    @Transactional
    public UserDTO activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setIsActive(true);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    /**
     * Assign role to user user dto.
     *
     * @param userId the user id
     * @param roleId the role id
     * @return the user dto
     */
    @Transactional
    public UserDTO assignRoleToUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        user.getRoles().add(role);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    /**
     * Remove role from user user dto.
     *
     * @param userId the user id
     * @param roleId the role id
     * @return the user dto
     */
    @Transactional
    public UserDTO removeRoleFromUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        // Prevent removing last role
        if (user.getRoles().size() <= 1) {
            throw new AccessDeniedException("Cannot remove the last role from a user");
        }

        user.getRoles().remove(role);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}