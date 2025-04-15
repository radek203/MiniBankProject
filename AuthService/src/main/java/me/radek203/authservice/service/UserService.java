package me.radek203.authservice.service;

import me.radek203.authservice.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {

    /**
     * Gets the UserDetailsService instance.
     *
     * @return the UserDetailsService instance
     */
    UserDetailsService getUserDetailsService();

    /**
     * Gets a User by its username.
     *
     * @param username the username of the User
     * @return the User with the specified username
     */
    User getUserByUsername(String username);
}
