package me.radek203.authservice.service;


import me.radek203.authservice.entities.User;
import me.radek203.authservice.entities.dto.LoginDTO;
import me.radek203.authservice.entities.dto.UserDTO;
import me.radek203.authservice.entities.dto.UserUpdateDTO;
import me.radek203.authservice.security.JWTAuthentication;

public interface AuthService {

    /**
     * Creates a new user in the system.
     *
     * @param user the user data to be created
     * @return the created user
     */
    User createUser(UserDTO user);

    /**
     * Authenticates a user based on the provided login credentials.
     *
     * @param user the login credentials
     * @return the JWT authentication token
     */
    JWTAuthentication authenticateUser(LoginDTO user);

    /**
     * Refreshes the JWT token for the user.
     *
     * @param authentication the JWT authentication object
     * @return the refreshed JWT authentication token
     */
    JWTAuthentication authenticateUser(JWTAuthentication authentication);

    /**
     * Validates the provided JWT token and retrieves the associated user.
     *
     * @param authentication the JWT authentication object
     * @return the user associated with the token
     */
    User validateToken(JWTAuthentication authentication);

    /**
     * Updates the user information in the system.
     *
     * @param userId the ID of the user to be updated
     * @param user   the updated user data
     * @return the updated user
     */
    User updateUser(int userId, UserUpdateDTO user);

}
