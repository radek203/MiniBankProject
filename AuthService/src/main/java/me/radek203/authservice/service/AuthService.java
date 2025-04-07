package me.radek203.authservice.service;


import me.radek203.authservice.entities.User;
import me.radek203.authservice.entities.dto.LoginDTO;
import me.radek203.authservice.entities.dto.UserDTO;
import me.radek203.authservice.security.JWTAuthentication;

public interface AuthService {

    User createUser(UserDTO user);
    JWTAuthentication authenticateUser(LoginDTO user);
    JWTAuthentication authenticateUser(JWTAuthentication authentication);
    void validateToken(JWTAuthentication authentication);

}
