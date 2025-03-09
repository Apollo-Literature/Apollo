package lk.apollo.service;

import io.micrometer.common.util.StringUtils;
import lk.apollo.exception.user.UserIdMissingException;
import lk.apollo.exception.user.UserNotFoundException;
import lk.apollo.exception.user.UserNotValidException;
import lk.apollo.dto.UserDTO;
import lk.apollo.mapper.UserMapper;
import lk.apollo.model.User;
import lk.apollo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /**
     * Get all users
     *
     * @return List of UserDTO instances
     */
    public List<UserDTO> getAllUsers() {
        List<UserDTO> users = userRepository.findAll().stream()
                .map(userMapper::mapToDTO)
                .collect(Collectors.toList());
        log.info("Completed getAllUsers(). Fetched {} users.", users.size());
        return users;
    }

    /**
     * Get user by ID
     * @param id - Long id
     * @return - UserDTO instance
     */
    public UserDTO getUserById(Long id) {
        UserDTO user = userRepository.findById(id)
                .map(userMapper::mapToDTO)
                .orElseThrow(() -> new UserNotFoundException());
        log.info("Completed getUserById(). Fetched user with ID: {}", id);
        return user;
    }

    /**
     * Get user by email
     * @param email - String email
     * @return - UserDTO instance
     */
    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(userMapper::mapToDTO);
    }

    /**
     * Add a user
     *
     * @param userDTO - UserDTO instance
     * @return UserDTO instance
     */
    @Transactional
    public UserDTO addUser(UserDTO userDTO) {
        validateUser(userDTO);
        User user = userMapper.mapToEntity(userDTO);
        User savedUser = userRepository.save(user);
        log.info("Completed addUser(). Added user with ID: {}", savedUser.getUserId());
        return userMapper.mapToDTO(savedUser);
    }

    /**
     * Update user information
     * @param userDTO - Updated UserDTO instance
     * @return - Updated UserDTO instance
     */
    @Transactional
    public UserDTO updateUser(UserDTO userDTO) {
        if (userDTO.getUserId() == null) {
            throw new UserIdMissingException("User ID is required to update.");
        }

        User existingUser = userRepository.findById(userDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException());

        validateUser(userDTO);
        updateUserFromDTO(existingUser, userDTO);

        UserDTO updatedUser = userMapper.mapToDTO(userRepository.save(existingUser));
        log.info("Completed updateUser(). Updated user with ID: {}", existingUser.getUserId());
        return updatedUser;
    }

    /**
     * Delete a user by ID
     * @param id - Long id
     * @return - Boolean (true if deleted, false if not found)
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException();
        }
        userRepository.deleteById(id);
        log.info("Completed deleteUser(). Deleted user with ID: {}", id);
    }

    //! Helper Methods

    /**
     * Update user details from DTO
     * @param user - User entity to be updated
     * @param dto - UserDTO containing updated information
     */
    private void updateUserFromDTO(User user, UserDTO dto) {
        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPassword() != null) user.setPassword(dto.getPassword());
        if (dto.getDateOfBirth() != null) user.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getRole() != null) user.setRole(dto.getRole());
        if (dto.getIsActive() != null) user.setIsActive(dto.getIsActive());
    }

    private void validateUser(UserDTO userDTO) {
        if (StringUtils.isBlank(userDTO.getFirstName())) {
            throw new UserNotValidException("User first name is required.");
        }
        if (StringUtils.isBlank(userDTO.getLastName())) {
            throw new UserNotValidException("User last name is required.");
        }
        if (StringUtils.isBlank(userDTO.getEmail())) {
            throw new UserNotValidException("User email is required.");
        }
        if (StringUtils.isBlank(userDTO.getPassword())) {
            throw new UserNotValidException("User password is required.");
        }
        if (userDTO.getDateOfBirth() == null) {
            throw new UserNotValidException("User date of birth is required.");
        }
        if (StringUtils.isBlank(userDTO.getRole())) {
            throw new UserNotValidException("User role is required.");
        }
    }
}
