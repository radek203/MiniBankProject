package me.radek203.authservice.service.impl;

import me.radek203.authservice.entities.Role;
import me.radek203.authservice.entities.User;
import me.radek203.authservice.exception.ResourceNotFoundException;
import me.radek203.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1, "user123", "password", "email@example.com", Role.USER, null);
    }

    @Test
    void getUserByUsername_shouldReturnUserWhenUserExists() {
        String username = "user123";
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        User result = userService.getUserByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void getUserByUsername_shouldThrowExceptionWhenUserNotFound() {
        String username = "user123";
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.getUserByUsername(username));

        assertEquals("error/user-not-found", exception.getMessage());
        assertEquals(username, exception.getData());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void getUserDetailsService_shouldReturnUserDetailsService() {
        String username = "user123";
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserDetailsService userDetailsService = userService.getUserDetailsService();

        UserDetails result = userDetailsService.loadUserByUsername(username);

        assertEquals(user.getUsername(), result.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }
}