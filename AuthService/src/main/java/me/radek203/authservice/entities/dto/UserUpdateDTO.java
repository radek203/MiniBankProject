package me.radek203.authservice.entities.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {

    @NotNull(message = "error/username-required")
    private String username;
    @NotNull(message = "error/old-password-required")
    private String oldPassword;
    @NotNull(message = "error/password-required")
    private String password;
    @NotNull(message = "error/email-required")
    private String email;

}
