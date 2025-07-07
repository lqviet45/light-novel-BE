package com.lqviet.userservice.dto.requests;

import com.lqviet.userservice.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
@Value
public class UserRegistrationRequest implements Serializable {
    @Size(min = 2, max = 50)
    @Pattern(message = "Username can only contain letters, numbers, and underscores", regexp = "^[a-zA-Z0-9_]+$")
    @NotBlank
    String username;
    @Size(max = 100)
    @Email
    @NotBlank
    String email;
    @Size(min = 8, max = 50)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d)(?=.*[@$!%*?&])[A-Za-z\\\\d@$!%*?&]")
    @NotBlank
    String password;
}