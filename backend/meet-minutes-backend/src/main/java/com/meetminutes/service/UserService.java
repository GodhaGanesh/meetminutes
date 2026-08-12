package com.meetminutes.service;

import com.meetminutes.dto.LoginRequest;
import com.meetminutes.dto.LoginResponse;
import com.meetminutes.dto.UserRequest;
import com.meetminutes.dto.UserResponse;
import com.meetminutes.entity.User;
import com.meetminutes.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserResponse createUser(UserRequest request) {

        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );
        user.setProfileImageUrl(request.getProfileImageUrl());
        user.setRole("USER");
        user.setActive(true);

        User savedUser = userRepository.save(user);

        return convertToResponse(savedUser);
    }

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private UserResponse convertToResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getProfileImageUrl(),
                user.getRole(),
                user.isActive()
        );
    }

    public boolean authenticate(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return false;
        }

        return passwordEncoder.matches(
                password,
                user.getPasswordHash()
        );
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            throw new RuntimeException("Invalid email or password");
        }

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPasswordHash()
                );

        if (!passwordMatches) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );

        return new LoginResponse(
                "Login successful",
                token
        );
    }
}