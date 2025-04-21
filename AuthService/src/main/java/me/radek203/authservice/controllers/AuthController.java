package me.radek203.authservice.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import me.radek203.authservice.entities.dto.LoginDTO;
import me.radek203.authservice.entities.dto.UserDTO;
import me.radek203.authservice.entities.dto.UserUpdateDTO;
import me.radek203.authservice.mapper.UserMapper;
import me.radek203.authservice.security.JWTAuthentication;
import me.radek203.authservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController is a REST controller that handles authentication-related requests.
 * It provides endpoints for user registration, login, token refresh, and user updates.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody final UserDTO user) {
        final UserDTO savedUser = UserMapper.mapUserToUserDTO(authService.createUser(user));
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JWTAuthentication> loginUser(@Valid @RequestBody final LoginDTO user) {
        return ResponseEntity.ok(authService.authenticateUser(user));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JWTAuthentication> refreshToken(@Valid @RequestBody final JWTAuthentication authentication) {
        return ResponseEntity.ok(authService.authenticateUser(authentication));
    }

    @PostMapping("/validate")
    public ResponseEntity<UserDTO> validateToken(@Valid @RequestBody final JWTAuthentication authentication) {
        final UserDTO user = UserMapper.mapUserToUserDTO(authService.validateToken(authentication));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody final UserUpdateDTO user, @PathVariable final int userId, @RequestHeader("X-UserId") int userIdHeader) {
        final UserDTO savedUser = UserMapper.mapUserToUserDTO(authService.updateUser(userId, user, userIdHeader));
        return ResponseEntity.ok(savedUser);
    }

}
