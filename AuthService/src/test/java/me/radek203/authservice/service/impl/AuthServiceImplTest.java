package me.radek203.authservice.service.impl;

import me.radek203.authservice.entities.Role;
import me.radek203.authservice.entities.User;
import me.radek203.authservice.entities.dto.LoginDTO;
import me.radek203.authservice.entities.dto.UserDTO;
import me.radek203.authservice.entities.dto.UserUpdateDTO;
import me.radek203.authservice.exception.ResourceAlreadyExistsException;
import me.radek203.authservice.exception.ResourceInvalidException;
import me.radek203.authservice.exception.ResourceNotFoundException;
import me.radek203.authservice.repository.UserRepository;
import me.radek203.authservice.security.JWTAuthentication;
import me.radek203.authservice.security.JWTTokenUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JWTTokenUtils jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void createUser_shouldCreateNewUserWhenUsernameIsAvailable() {
        UserDTO userDTO = new UserDTO(null, "user", "pass", "user@mail.com", null, null);
        User user = new User(null, "user", "encoded", "user@mail.com", Role.USER, LocalDateTime.now());

        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = authService.createUser(userDTO);

        assertEquals("user", result.getUsername());
        assertEquals("encoded", result.getPassword());
        assertEquals(Role.USER, result.getRole());
    }

    @Test
    void createUser_shouldThrowWhenUsernameAlreadyExists() {
        UserDTO userDTO = new UserDTO(null, "user", "pass", "user@mail.com", null, null);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(new User()));

        assertThrows(ResourceAlreadyExistsException.class, () -> authService.createUser(userDTO));
    }

    @Test
    void authenticateUserWithLoginDTO_shouldReturnTokenWhenValid() {
        LoginDTO loginDTO = new LoginDTO("user", "pass");
        User user = new User(1, "user", "encoded", "mail", Role.USER, LocalDateTime.now());

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(jwtService.generateToken("user")).thenReturn("token");

        JWTAuthentication result = authService.authenticateUser(loginDTO);

        assertEquals("token", result.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticateUserWithToken_shouldReturnNewTokenWhenValid() {
        JWTAuthentication token = new JWTAuthentication("token");
        User user = new User(1, "user", "encoded", "mail", Role.USER, LocalDateTime.now());

        when(jwtService.getUsername("token")).thenReturn("user");
        when(jwtService.isTokenValid("token", "user")).thenReturn(true);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(jwtService.generateToken("user")).thenReturn("new-token");

        JWTAuthentication result = authService.authenticateUser(token);

        assertEquals("new-token", result.getToken());
    }

    @Test
    void authenticateUserWithToken_shouldThrowWhenTokenInvalid() {
        JWTAuthentication token = new JWTAuthentication("token");

        when(jwtService.getUsername("token")).thenReturn("user");
        when(jwtService.isTokenValid("token", "user")).thenReturn(false);

        assertThrows(ResourceInvalidException.class, () -> authService.authenticateUser(token));
    }

    @Test
    void validateToken_shouldReturnUserWhenValid() {
        JWTAuthentication token = new JWTAuthentication("token");
        User user = new User(1, "user", "encoded", "mail", Role.USER, LocalDateTime.now());

        when(jwtService.getUsername("token")).thenReturn("user");
        when(jwtService.isTokenValid("token", "user")).thenReturn(true);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        User result = authService.validateToken(token);

        assertEquals("user", result.getUsername());
    }

    @Test
    void validateToken_shouldThrowWhenTokenInvalid() {
        JWTAuthentication token = new JWTAuthentication("token");

        when(jwtService.getUsername("token")).thenReturn("user");
        when(jwtService.isTokenValid("token", "user")).thenReturn(false);

        assertThrows(ResourceInvalidException.class, () -> authService.validateToken(token));
    }

    @Test
    void updateUser_shouldUpdateWhenPasswordValid() {
        UserUpdateDTO update = new UserUpdateDTO("new", "old", "newpass", "mail");
        User user = new User(1, "user", "old", "mail", Role.USER, LocalDateTime.now());

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(jwtService.generateToken("user")).thenReturn("token");
        when(passwordEncoder.encode("newpass")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = authService.updateUser(1, update);

        assertEquals("new", result.getUsername());
        assertEquals("mail", result.getEmail());
        assertEquals("encoded", result.getPassword());
    }

    @Test
    void updateUser_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.updateUser(1, new UserUpdateDTO()));
    }

    @Test
    void updateUser_shouldThrowWhenPasswordInvalid() {
        User user = new User(1, "user", "enc", "mail", Role.USER, LocalDateTime.now());
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        doThrow(BadCredentialsException.class).when(authenticationManager).authenticate(any());

        assertThrows(ResourceInvalidException.class, () ->
                authService.updateUser(1, new UserUpdateDTO("user", "wrong", "pass", "mail")));
    }
}
