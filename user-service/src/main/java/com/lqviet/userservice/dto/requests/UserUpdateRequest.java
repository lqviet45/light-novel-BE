package com.lqviet.userservice.dto.requests;

import com.lqviet.userservice.enums.UserStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserUpdateRequest {

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;

    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Pattern(regexp = "^[+]?[1-9]?\\d{1,14}$", message = "Phone number must be valid")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;

    @Past(message = "Date of birth must be in the past")
    private LocalDateTime dateOfBirth;

    @Enumerated(EnumType.STRING)
    private UserStatus status;
}