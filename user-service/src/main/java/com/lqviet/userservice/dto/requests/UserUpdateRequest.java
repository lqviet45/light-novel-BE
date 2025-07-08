package com.lqviet.userservice.dto.requests;

import com.lqviet.userservice.enums.UserStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateRequest {

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(max = 50, message = "Username must not exceed 50 characters")
    private String username;

    @Enumerated(EnumType.STRING)
    private UserStatus status;
}