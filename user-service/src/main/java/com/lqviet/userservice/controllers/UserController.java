package com.lqviet.userservice.controllers;

import com.lqviet.userservice.dto.requests.PasswordUpdateRequest;
import com.lqviet.userservice.dto.requests.UserRegistrationRequest;
import com.lqviet.userservice.dto.requests.UserUpdateRequest;
import com.lqviet.userservice.dto.responses.ApiResponse;
import com.lqviet.userservice.dto.responses.PagedResponse;
import com.lqviet.userservice.dto.responses.UserResponse;
import com.lqviet.userservice.enums.UserStatus;
import com.lqviet.userservice.services.IUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final IUserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody UserRegistrationRequest request) {
        log.info("Creating new user with email: {}", request.getEmail());

        UserResponse userResponse = userService.createUser(request);
        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User created successfully")
                .data(userResponse)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable @Min(value = 1, message = "User ID must be positive") Long id) {
        log.debug("Retrieving user by ID: {}", id);

        UserResponse userResponse = userService.getUserById(id);
        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User retrieved successfully")
                .data(userResponse)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(
            @PathVariable @Email(message = "Invalid email format") String email) {
        log.debug("Retrieving user by email: {}", email);

        UserResponse userResponse = userService.getUserByEmail(email);
        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User retrieved successfully")
                .data(userResponse)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(
            @PathVariable String username) {
        log.debug("Retrieving user by username: {}", username);

        UserResponse userResponse = userService.getUserByUsername(username);
        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User retrieved successfully")
                .data(userResponse)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Retrieving all users - page: {}, size: {}, sortBy: {}, sortDir: {}",
                page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PagedResponse<UserResponse> pagedUsers = userService.getAllUsers(pageable);
        ApiResponse<PagedResponse<UserResponse>> response = ApiResponse.<PagedResponse<UserResponse>>builder()
                .success(true)
                .message("Users retrieved successfully")
                .data(pagedUsers)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> getUsersByStatus(
            @PathVariable UserStatus status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Retrieving users by status: {}", status);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PagedResponse<UserResponse> pagedUsers = userService.getUsersByStatus(status, pageable);
        ApiResponse<PagedResponse<UserResponse>> response = ApiResponse.<PagedResponse<UserResponse>>builder()
                .success(true)
                .message("Users retrieved successfully")
                .data(pagedUsers)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable @Min(value = 1, message = "User ID must be positive") Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        log.info("Updating user with ID: {}", id);

        UserResponse userResponse = userService.updateUser(id, request);
        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User updated successfully")
                .data(userResponse)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @PathVariable @Min(value = 1, message = "User ID must be positive") Long id,
            @Valid @RequestBody PasswordUpdateRequest request) {
        log.info("Updating password for user ID: {}", id);

        userService.updatePassword(id, request);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Password updated successfully")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @PathVariable @Min(value = 1, message = "User ID must be positive") Long id,
            @RequestParam UserStatus status) {
        log.info("Updating status for user ID: {} to: {}", id, status);

        userService.updateUserStatus(id, status);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("User status updated successfully")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @PathVariable @Min(value = 1, message = "User ID must be positive") Long id) {
        log.info("Verifying email for user ID: {}", id);

        userService.verifyEmail(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Email verified successfully")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/last-login")
    public ResponseEntity<ApiResponse<Void>> updateLastLoginTime(
            @PathVariable @Min(value = 1, message = "User ID must be positive") Long id) {
        log.debug("Updating last login time for user ID: {}", id);

        userService.updateLastLoginTime(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Last login time updated successfully")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable @Min(value = 1, message = "User ID must be positive") Long id) {
        log.info("Soft deleting user with ID: {}", id);

        userService.deleteUser(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("User deleted successfully")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/hard")
    public ResponseEntity<ApiResponse<Void>> hardDeleteUser(
            @PathVariable @Min(value = 1, message = "User ID must be positive") Long id) {
        log.warn("Hard deleting user with ID: {}", id);

        userService.hardDeleteUser(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("User permanently deleted")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/exists/email/{email}")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailExists(
            @PathVariable @Email(message = "Invalid email format") String email) {
        log.debug("Checking if email exists: {}", email);

        boolean exists = userService.existsByEmail(email);
        ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                .success(true)
                .message("Email existence check completed")
                .data(exists)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/exists/username/{username}")
    public ResponseEntity<ApiResponse<Boolean>> checkUsernameExists(
            @PathVariable String username) {
        log.debug("Checking if username exists: {}", username);

        boolean exists = userService.existsByUsername(username);
        ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                .success(true)
                .message("Username existence check completed")
                .data(exists)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserStats() {
        log.debug("Retrieving user statistics");

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.getTotalUserCount());
        stats.put("activeUsers", userService.getActiveUserCount());

        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("User statistics retrieved successfully")
                .data(stats)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }
}