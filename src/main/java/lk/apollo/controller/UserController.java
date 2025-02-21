package lk.apollo.controller;

import lk.apollo.dto.BookDTO;
import lk.apollo.dto.UserDTO;
import lk.apollo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

//    public ResponseEntity<UserDTO> addPublisher(@RequestBody UserDTO userDTO) {
//        UserDTO publisher = userService.addPublisher(userDTO);
//        return new ResponseEntity<>(publisher, HttpStatus.OK);
//    }
}
