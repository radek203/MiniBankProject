package me.radek203.authservice.service.impl;

import lombok.AllArgsConstructor;
import me.radek203.authservice.entities.User;
import me.radek203.authservice.exception.ResourceNotFoundException;
import me.radek203.authservice.mapper.UserMapper;
import me.radek203.authservice.repository.UserRepository;
import me.radek203.authservice.service.UserService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public final User getUserByUsername(final String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("user/not-found", username));
    }

    @Override
    public final UserDetailsService getUserDetailsService() {
        return username -> UserMapper.mapUserToUserDetails(getUserByUsername(username));
    }

}
