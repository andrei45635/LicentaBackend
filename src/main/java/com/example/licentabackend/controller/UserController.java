package com.example.licentabackend.controller;

import com.example.licentabackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

//    @PostMapping("/register")
//    public void registerUser(@RequestBody UserDTO userDTO) {
//        userService.findOrCreateUser(userDTO);
//    }
}
