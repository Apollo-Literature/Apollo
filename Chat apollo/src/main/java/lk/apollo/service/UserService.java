package lk.apollo.service;

import lk.apollo.dto.UserDTO;
import lk.apollo.mapper.UserMapper;
import lk.apollo.model.User;
import lk.apollo.repository.UserRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

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
        return userRepository.findAll().stream()
                .map(userMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get user by ID
     * @param id - Long id
     * @return - UserDTO instance
     */
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id).map(userMapper::mapToDTO);
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
        User user = userMapper.mapToEntity(userDTO);
        User savedUser = userRepository.save(user);
        return userMapper.mapToDTO(savedUser);
    }

    /**
     * Update user information
     * @param userDTO - Updated UserDTO instance
     * @return - Updated UserDTO instance
     */
    @Transactional
    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        if (userDTO.getUserId() == null) {
            return Optional.empty();
        }

        return userRepository.findById(userDTO.getUserId())
                .map(existingUser -> {
                    updateUserFromDTO(existingUser, userDTO);
                    return userMapper.mapToDTO(userRepository.save(existingUser));
                });
    }

    /**
     * Delete a user by ID
     * @param id - Long id
     * @return - Boolean (true if deleted, false if not found)
     */
    public boolean deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException ex) {
            return false;
        }
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
}
