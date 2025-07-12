package com.lqviet.userservice.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lqviet.userservice.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private Long id;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phoneNumber;
    private String bio;
    private LocalDateTime dateOfBirth;
    private String profilePictureUrl;
    private UserStatus status;
    private boolean emailVerified;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private LocalDateTime lastLoginAt;
    private LocalDateTime passwordChangedAt;
    private Set<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;

    // Helper methods for display
    public String getDisplayName() {
        if (fullName != null && !fullName.trim().isEmpty()) {
            return fullName;
        }
        return username;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE && enabled;
    }

    public String getInitials() {
        StringBuilder initials = new StringBuilder();
        if (firstName != null && !firstName.isEmpty()) {
            initials.append(firstName.charAt(0));
        }
        if (lastName != null && !lastName.isEmpty()) {
            initials.append(lastName.charAt(0));
        }
        if (initials.isEmpty() && username != null && !username.isEmpty()) {
            initials.append(username.charAt(0));
        }
        return initials.toString().toUpperCase();
    }
}