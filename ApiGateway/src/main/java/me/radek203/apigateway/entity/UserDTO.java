package me.radek203.apigateway.entity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Role role;
    private LocalDateTime createdAt;

}
