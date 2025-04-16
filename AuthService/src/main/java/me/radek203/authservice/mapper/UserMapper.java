package me.radek203.authservice.mapper;

import me.radek203.authservice.entities.User;
import me.radek203.authservice.entities.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetails;

public class UserMapper {

    /**
     * Maps a User entity to a UserDetails object for Spring Security.
     *
     * @param user the User entity to map
     * @return a UserDetails object containing the user's information
     */
    public static UserDetails mapUserToUserDetails(final User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();
    }

    /**
     * Maps a User entity to a UserDTO object.
     *
     * @param user the User entity to map
     * @return a UserDTO object containing the user's information
     */
    public static UserDTO mapUserToUserDTO(final User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getPassword(), user.getEmail(), user.getAvatar(), user.getRole(), user.getCreatedAt());
    }

    /**
     * Maps a UserDTO object to a User entity.
     *
     * @param userDTO the UserDTO object to map
     * @return a User entity containing the user's information
     */
    public static User mapUserDTOToUser(final UserDTO userDTO) {
        return new User(userDTO.getId(), userDTO.getUsername(), userDTO.getPassword(), userDTO.getEmail(), userDTO.getAvatar(), userDTO.getRole(), userDTO.getCreatedAt());
    }

}
