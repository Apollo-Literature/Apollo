package lk.apollo.controller;

import lk.apollo.dto.UserDTO;
import lk.apollo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

//    public ResponseEntity<UserDTO> addUser(@RequestBody UserDTO userDTO) {
//
//    }
}
