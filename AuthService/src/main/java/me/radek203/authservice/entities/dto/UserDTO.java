package me.radek203.authservice.entities.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.radek203.authservice.entities.Role;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Integer id;
    @NotNull(message = "error/username-required")
    private String username;
    @NotNull(message = "error/password-required")
    private String password;
    @NotNull(message = "error/email-required")
    private String email;
    @NotNull(message = "error/avatar-required")
    private String avatar;
    private Role role;
    private LocalDateTime createdAt;

}
