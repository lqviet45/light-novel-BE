package com.lqviet.authservice.controllers;

import com.lqviet.authservice.dtos.requests.LoginRequest;
import com.lqviet.authservice.dtos.requests.LogoutRequest;
import com.lqviet.authservice.dtos.requests.RefreshTokenRequest;
import com.lqviet.authservice.dtos.reseponses.ApiResponse;
import com.lqviet.authservice.dtos.reseponses.AuthResponse;
import com.lqviet.authservice.dtos.reseponses.TokenResponse;
import com.lqviet.authservice.services.AuthService;
import com.lqviet.authservice.services.RateLimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

    private final AuthService authService;
    private final RateLimitService rateLimitService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for email: {}", request.getEmail());

        AuthResponse authResponse = authService.login(request);

        ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Login successful")
                .data(authResponse)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Generate new access token using refresh token")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.debug("Token refresh request");

        TokenResponse tokenResponse = authService.refreshToken(request);

        ApiResponse<TokenResponse> response = ApiResponse.<TokenResponse>builder()
                .success(true)
                .message("Token refreshed successfully")
                .data(tokenResponse)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Invalidate tokens and logout user")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody(required = false) LogoutRequest request,
                                                    HttpServletRequest httpRequest) {
        log.debug("Logout request");

        String accessToken = null;
        String refreshToken = null;

        // Get tokens from request body or Authorization header
        if (request != null) {
            accessToken = request.getAccessToken();
            refreshToken = request.getRefreshToken();
        }

        // If no access token in body, try to get from Authorization header
        if (!StringUtils.hasText(accessToken)) {
            accessToken = extractTokenFromHeader(httpRequest);
        }

        authService.logout(accessToken, refreshToken);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Logout successful")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout-all")
    @Operation(summary = "Logout from all devices", description = "Invalidate all user sessions")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<ApiResponse<Void>> logoutAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        log.debug("Logout all request for user: {}", userEmail);

        authService.logoutAll(userEmail);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Logged out from all devices successfully")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate access token", description = "Validate the provided access token")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateToken(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);

        boolean isValid = authService.validateAccessToken(token);

        Map<String, Object> validationResult = new HashMap<>();
        validationResult.put("valid", isValid);
        validationResult.put("token", token != null ? "provided" : "missing");

        if (isValid) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            validationResult.put("username", authentication.getName());
            validationResult.put("authorities", authentication.getAuthorities());
        }

        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message(isValid ? "Token is valid" : "Token is invalid")
                .data(validationResult)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user-info")
    @Operation(summary = "Get user information", description = "Get current user information from token")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<ApiResponse<AuthResponse.UserInfo>> getUserInfo(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);

        AuthResponse.UserInfo userInfo = authService.getUserInfoFromToken(token);

        ApiResponse<AuthResponse.UserInfo> response = ApiResponse.<AuthResponse.UserInfo>builder()
                .success(true)
                .message("User information retrieved successfully")
                .data(userInfo)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/sessions")
    @Operation(summary = "Get active sessions", description = "Get count of active sessions for current user")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getActiveSessions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        long activeSessionCount = authService.getActiveSessionCount(userEmail);

        Map<String, Object> sessionInfo = new HashMap<>();
        sessionInfo.put("activeSessionCount", activeSessionCount);
        sessionInfo.put("userEmail", userEmail);

        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Session information retrieved successfully")
                .data(sessionInfo)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/rate-limit/{identifier}")
    @Operation(summary = "Get rate limit info", description = "Get rate limiting information for identifier")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRateLimitInfo(
            @PathVariable String identifier,
            @RequestParam(defaultValue = "login") String operation) {

        RateLimitService.RateLimitInfo rateLimitInfo = rateLimitService.getRateLimitInfo(identifier, operation);

        Map<String, Object> info = new HashMap<>();
        info.put("maxRequests", rateLimitInfo.getMaxRequests());
        info.put("remainingRequests", rateLimitInfo.getRemainingRequests());
        info.put("resetTimeSeconds", rateLimitInfo.getResetTimeSeconds());
        info.put("isLimited", rateLimitInfo.isLimited());
        info.put("operation", operation);

        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Rate limit information retrieved successfully")
                .data(info)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    // Admin endpoints for managing authentication

    @PostMapping("/admin/reset-rate-limit")
    @Operation(summary = "Reset rate limit", description = "Reset rate limit for identifier (Admin only)")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<ApiResponse<Void>> resetRateLimit(
            @RequestParam String identifier,
            @RequestParam(defaultValue = "login") String operation) {

        if ("login".equals(operation)) {
            rateLimitService.resetLoginRateLimit(identifier);
        } else if ("refresh".equals(operation)) {
            rateLimitService.resetRefreshRateLimit(identifier);
        }

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Rate limit reset successfully")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/logout-user")
    @Operation(summary = "Logout user", description = "Force logout user from all devices (Admin only)")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<ApiResponse<Void>> logoutUser(@RequestParam String userEmail) {
        log.info("Admin forcing logout for user: {}", userEmail);

        authService.logoutAll(userEmail);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("User logged out from all devices successfully")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    // Health check endpoint

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check authentication service health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "auth-service");
        health.put("timestamp", LocalDateTime.now().toString());

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(true)
                .message("Auth service is healthy")
                .data(health)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    // Private helper methods

    private String extractTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}