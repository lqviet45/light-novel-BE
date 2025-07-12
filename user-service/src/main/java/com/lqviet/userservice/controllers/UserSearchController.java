package com.lqviet.userservice.controllers;

import com.lqviet.userservice.dto.responses.ApiResponse;
import com.lqviet.userservice.dto.responses.PagedResponse;
import com.lqviet.userservice.dto.responses.UserResponse;
import com.lqviet.userservice.enums.UserStatus;
import com.lqviet.userservice.services.IUserSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/users/search")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserSearchController {

    private final IUserSearchService userSearchService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) Boolean emailVerified,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAfter,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdBefore,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastLoginAfter,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.debug("Searching users with filters - keyword: {}, email: {}, status: {}",
                keyword, email, status);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PagedResponse<UserResponse> searchResults = userSearchService.searchUsers(
                keyword, email, username, firstName, lastName, status,
                emailVerified, roleName, createdAfter, createdBefore,
                lastLoginAfter, pageable);

        ApiResponse<PagedResponse<UserResponse>> response = ApiResponse.<PagedResponse<UserResponse>>builder()
                .success(true)
                .message("Users search completed successfully")
                .data(searchResults)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/quick")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> quickSearch(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {

        log.debug("Quick search for: {}", q);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        PagedResponse<UserResponse> searchResults = userSearchService.quickSearch(q, pageable);

        ApiResponse<PagedResponse<UserResponse>> response = ApiResponse.<PagedResponse<UserResponse>>builder()
                .success(true)
                .message("Quick search completed successfully")
                .data(searchResults)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }
}