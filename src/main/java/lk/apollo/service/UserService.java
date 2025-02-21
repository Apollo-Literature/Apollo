package lk.apollo.service;

import lk.apollo.dto.UserDTO;
import lk.apollo.mapper.UserMapper;
import lk.apollo.model.User;
import lk.apollo.repository.UserRepository;

public class UserService {

    private UserRepository userRepository;
    private UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

//    public UserDTO addPublisher(UserDTO userDTO) {
//        User user = userMapper.mapToEntity(userDTO);
//
//    }
}
