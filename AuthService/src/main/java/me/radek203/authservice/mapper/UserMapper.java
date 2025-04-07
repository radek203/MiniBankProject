package me.radek203.authservice.mapper;

import me.radek203.authservice.entities.User;
import me.radek203.authservice.entities.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetails;

public class UserMapper {

    public static UserDetails mapUserToUserDetails(final User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();
    }

    public static UserDTO mapUserToUserDTO(final User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getPassword(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }

    public static User mapUserDTOToUser(final UserDTO userDTO) {
        return new User(userDTO.getId(), userDTO.getUsername(), userDTO.getPassword(), userDTO.getEmail(), userDTO.getRole(), userDTO.getCreatedAt());
    }

}
