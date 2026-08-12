package com.meetminutes.controller;

import com.meetminutes.dto.LoginRequest;
import com.meetminutes.dto.LoginResponse;
import com.meetminutes.dto.UserRequest;
import com.meetminutes.dto.UserResponse;
import com.meetminutes.entity.User;
import com.meetminutes.repository.UserRepository;
import com.meetminutes.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserResponse register(
            @Valid @RequestBody UserRequest request) {

        return userService.createUser(request);
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {

        return userService.login(request);
    }
}