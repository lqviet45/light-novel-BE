package com.lqviet.userservice.mapper;

import com.lqviet.userservice.dto.requests.UserRegistrationRequest;
import com.lqviet.userservice.dto.requests.UserUpdateRequest;
import com.lqviet.userservice.dto.responses.UserResponse;
import com.lqviet.userservice.entities.Role;
import com.lqviet.userservice.entities.User;
import com.lqviet.userservice.enums.UserStatus;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toEntity(UserRegistrationRequest request) {
        if (request == null) {
            return null;
        }

        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .status(UserStatus.ACTIVE)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .roles(new LinkedHashSet<>())
                .build();
    }

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        Set<String> roleNames = user.getRoles() != null
            ? user.getRoles().stream()
                .map(Role::getName)
                .map(Enum::name)
                .collect(Collectors.toSet())
            : new LinkedHashSet<>();

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .status(user.getStatus())
                .roles(roleNames)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public void updateEntity(User existingUser, UserUpdateRequest updateRequest) {
        if (existingUser == null || updateRequest == null) {
            return;
        }

        if (updateRequest.getUsername() != null) {
            existingUser.setUsername(updateRequest.getUsername());
        }
        if (updateRequest.getEmail() != null) {
            existingUser.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getStatus() != null) {
            existingUser.setStatus(updateRequest.getStatus());
        }
    }

    public List<UserResponse> toResponseList(List<User> users) {
        if (users == null) {
            return null;
        }

        return users.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
