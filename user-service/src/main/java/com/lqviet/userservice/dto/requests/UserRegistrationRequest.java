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
    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
    @Pattern(message = "Username can only contain letters, numbers, and underscores", regexp = "^[a-zA-Z0-9_]+$")
    @NotBlank(message = "Username is required")
    String username;

    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Email(message = "Please provide a valid email address")
    @NotBlank(message = "Email is required")
    String email;

    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    @NotBlank(message = "Password is required")
    String password;

    @Size(max = 50, message = "First name must not exceed 50 characters")
    String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    String lastName;
}