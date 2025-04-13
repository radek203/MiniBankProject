package me.radek203.authservice.service;

import me.radek203.authservice.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {

    UserDetailsService getUserDetailsService();

    User getUserByUsername(String username);
}
