package com.lqviet.accountservices.dtos.responses;

import com.lqviet.accountservices.enums.AccountStatus;
import com.lqviet.accountservices.enums.AccountType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.lqviet.accountservices.entities.Account}
 */
@Value
public class AccountResponse implements Serializable {
    Long id;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String lastModifiedBy;
    @Size(message = "Username must be between 3 and 50 characters", min = 3, max = 50)
    @Pattern(message = "Username can only contain letters, numbers, underscores, and hyphens", regexp = "^[a-zA-Z0-9_-]+$")
    @NotBlank(message = "Username is required")
    String username;
    @Size(message = "Email must not exceed 100 characters", max = 100)
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    String email;
    @Size(message = "First name must not exceed 50 characters", max = 50)
    String firstName;
    @Size(message = "Last name must not exceed 50 characters", max = 50)
    String lastName;
    @Pattern(message = "Phone number should be valid", regexp = "^\\+?[1-9]\\d{1,14}$")
    String phoneNumber;
    AccountStatus status;
    AccountType accountType;
    Boolean emailVerified;
    LocalDateTime lockedUntil;
}