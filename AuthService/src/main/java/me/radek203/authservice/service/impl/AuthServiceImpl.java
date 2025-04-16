package me.radek203.authservice.service.impl;

import lombok.AllArgsConstructor;
import me.radek203.authservice.entities.Role;
import me.radek203.authservice.entities.User;
import me.radek203.authservice.entities.dto.LoginDTO;
import me.radek203.authservice.entities.dto.UserDTO;
import me.radek203.authservice.entities.dto.UserUpdateDTO;
import me.radek203.authservice.exception.ResourceAlreadyExistsException;
import me.radek203.authservice.exception.ResourceInvalidException;
import me.radek203.authservice.exception.ResourceNotFoundException;
import me.radek203.authservice.mapper.UserMapper;
import me.radek203.authservice.repository.UserRepository;
import me.radek203.authservice.security.JWTAuthentication;
import me.radek203.authservice.security.JWTTokenUtils;
import me.radek203.authservice.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;
    private final JWTTokenUtils jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public final User createUser(final UserDTO user) {
        userRepository.findByUsername(user.getUsername()).ifPresent(u -> {
            throw new ResourceAlreadyExistsException("error/user-already-existed", user.getUsername());
        });

        final User finalUser = UserMapper.mapUserDTOToUser(user);
        finalUser.setId(null);
        finalUser.setRole(Role.USER);
        finalUser.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(finalUser);
    }

    @Override
    public final JWTAuthentication authenticateUser(final LoginDTO user) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        final User finalUser = userRepository.findByUsername(user.getUsername()).orElseThrow(() -> new ResourceNotFoundException("error/user-not-found", user.getUsername()));
        final String token = jwtService.generateToken(finalUser.getUsername());

        return new JWTAuthentication(token);
    }

    @Override
    public final JWTAuthentication authenticateUser(final JWTAuthentication authentication) {
        final String username = jwtService.getUsername(authentication.getToken());
        if (!jwtService.isTokenValid(authentication.getToken(), username)) {
            throw new ResourceInvalidException("error/token-invalid", authentication.getToken());
        }

        final User finalUser = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("error/user-not-found", username));
        final String token = jwtService.generateToken(finalUser.getUsername());

        return new JWTAuthentication(token);
    }

    @Override
    public User validateToken(final JWTAuthentication authentication) {
        final String username = jwtService.getUsername(authentication.getToken());
        if (!jwtService.isTokenValid(authentication.getToken(), username)) {
            throw new ResourceInvalidException("error/token-invalid", authentication.getToken());
        }

        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("error/user-not-found", username));
    }

    @Override
    public User updateUser(int userId, UserUpdateDTO user) {
        final User finalUser = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("error/user-not-found", userId));

        try {
            authenticateUser(new LoginDTO(finalUser.getUsername(), user.getOldPassword()));
        } catch (Exception e) {
            throw new ResourceInvalidException("error/invalid-password", user.getOldPassword());
        }

        finalUser.setUsername(user.getUsername());
        finalUser.setEmail(user.getEmail());
        finalUser.setAvatar(user.getAvatar());
        finalUser.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(finalUser);
    }

}
