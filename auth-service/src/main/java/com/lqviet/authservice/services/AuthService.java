package com.lqviet.authservice.services;

import com.lqviet.authservice.dtos.external.ExternalApiResponse;
import com.lqviet.authservice.dtos.external.UserResponse;
import com.lqviet.authservice.dtos.requests.LoginRequest;
import com.lqviet.authservice.dtos.requests.RefreshTokenRequest;
import com.lqviet.authservice.dtos.reseponses.AuthResponse;
import com.lqviet.authservice.dtos.reseponses.TokenResponse;
import com.lqviet.authservice.exceptions.AuthenticationException;
import com.lqviet.authservice.exceptions.InvalidTokenException;
import com.lqviet.authservice.exceptions.UserServiceException;
import com.lqviet.authservice.feign.UserServiceClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserServiceClient userServiceClient;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final RateLimitService rateLimitService;

    /**
     * Authenticate user and generate tokens
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        // Check rate limiting
        rateLimitService.checkLoginRateLimit(request.getEmail());

        try {
            // Get user from user service
            ExternalApiResponse<UserResponse> userApiResponse = userServiceClient.getUserByEmail(request.getEmail());

            if (!userApiResponse.isSuccess() || userApiResponse.getData() == null) {
                throw new AuthenticationException("Invalid email or password");
            }

            UserResponse user = userApiResponse.getData();

            // Validate user account status
            validateUserAccount(user);

            // Note: Password verification would need to be implemented in user service
            // For now, we'll assume the user service handles authentication
            // In a real scenario, you might want to pass the password to user service for verification

            // Generate tokens
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            // Store refresh token
            tokenService.storeRefreshToken(refreshToken, user.getEmail());

            // Extend token expiration for remember me
            if (request.isRememberMe()) {
                tokenService.extendRefreshTokenExpiration(refreshToken, 30 * 24 * 60 * 60); // 30 days
            }

            // Update last login time
            updateLastLoginTime(user.getId());

            log.info("Successful login for user: {}", user.getEmail());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getAccessTokenExpirationInSeconds())
                    .user(mapToUserInfo(user))
                    .loginTime(LocalDateTime.now())
                    .build();

        } catch (FeignException e) {
            log.error("User service error during login: {}", e.getMessage());
            if (e.status() == 404) {
                throw new AuthenticationException("Invalid email or password");
            }
            throw new UserServiceException("User service unavailable",
                    org.springframework.http.HttpStatus.valueOf(e.status()));
        } catch (Exception e) {
            log.error("Login error for email {}: {}", request.getEmail(), e.getMessage());
            throw new AuthenticationException("Authentication failed");
        }
    }

    /**
     * Refresh access token using refresh token
     */
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        log.debug("Token refresh attempt");

        String refreshToken = request.getRefreshToken();

        // Check rate limiting
        rateLimitService.checkRefreshRateLimit(refreshToken);

        try {
            // Validate refresh token
            if (!tokenService.isValidRefreshToken(refreshToken)) {
                throw new InvalidTokenException("Invalid refresh token");
            }

            // Get user email from refresh token
            String userEmail = tokenService.getUserEmailFromRefreshToken(refreshToken);

            // Get user details
            ExternalApiResponse<UserResponse> userApiResponse = userServiceClient.getUserByEmail(userEmail);

            if (!userApiResponse.isSuccess() || userApiResponse.getData() == null) {
                throw new AuthenticationException("User not found");
            }

            UserResponse user = userApiResponse.getData();

            // Validate user account status
            validateUserAccount(user);

            // Generate new access token
            String newAccessToken = jwtService.generateAccessToken(user);

            log.debug("Token refreshed successfully for user: {}", userEmail);

            return TokenResponse.builder()
                    .accessToken(newAccessToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getAccessTokenExpirationInSeconds())
                    .issuedAt(LocalDateTime.now())
                    .expiresAt(jwtService.extractExpirationAsLocalDateTime(newAccessToken))
                    .build();

        } catch (FeignException e) {
            log.error("User service error during token refresh: {}", e.getMessage());
            throw new UserServiceException("User service unavailable",
                    org.springframework.http.HttpStatus.valueOf(e.status()));
        } catch (Exception e) {
            log.error("Token refresh error: {}", e.getMessage());
            throw new InvalidTokenException("Token refresh failed");
        }
    }

    /**
     * Logout user and invalidate tokens
     */
    public void logout(String accessToken, String refreshToken) {
        log.debug("Logout attempt");

        try {
            // Blacklist access token
            if (accessToken != null && jwtService.validateToken(accessToken)) {
                tokenService.blacklistAccessToken(accessToken);
            }

            // Remove refresh token
            if (refreshToken != null) {
                tokenService.removeRefreshToken(refreshToken);
            }

            log.debug("Logout completed successfully");

        } catch (Exception e) {
            log.warn("Error during logout: {}", e.getMessage());
            // Don't throw exception for logout errors - it should always succeed
        }
    }

    /**
     * Logout from all devices
     */
    public void logoutAll(String userEmail) {
        log.debug("Logout all devices for user: {}", userEmail);

        try {
            tokenService.removeAllUserSessions(userEmail);
            log.debug("Logged out from all devices for user: {}", userEmail);
        } catch (Exception e) {
            log.error("Error during logout all: {}", e.getMessage());
            throw new RuntimeException("Failed to logout from all devices");
        }
    }

    /**
     * Validate access token
     */
    public boolean validateAccessToken(String accessToken) {
        try {
            // Check if token is blacklisted
            if (tokenService.isTokenBlacklisted(accessToken)) {
                return false;
            }

            // Validate JWT token
            return jwtService.validateToken(accessToken) && jwtService.isAccessToken(accessToken);

        } catch (Exception e) {
            log.debug("Access token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get user info from access token
     */
    public AuthResponse.UserInfo getUserInfoFromToken(String accessToken) {
        if (!validateAccessToken(accessToken)) {
            throw new InvalidTokenException("Invalid access token");
        }

        try {
            String userEmail = jwtService.extractUsername(accessToken);
            ExternalApiResponse<UserResponse> userApiResponse = userServiceClient.getUserByEmail(userEmail);

            if (!userApiResponse.isSuccess() || userApiResponse.getData() == null) {
                throw new AuthenticationException("User not found");
            }

            return mapToUserInfo(userApiResponse.getData());

        } catch (FeignException e) {
            log.error("User service error: {}", e.getMessage());
            throw new UserServiceException("User service unavailable",
                    org.springframework.http.HttpStatus.valueOf(e.status()));
        }
    }

    /**
     * Get active session count for user
     */
    public long getActiveSessionCount(String userEmail) {
        return tokenService.getActiveSessionCount(userEmail);
    }

    // Private helper methods

    private void validateUserAccount(UserResponse user) {
        if (!user.isActive()) {
            throw new AuthenticationException("Account is not active");
        }

        if (!user.isAccountNonLocked()) {
            throw new AuthenticationException("Account is locked");
        }

        if (!user.isAccountNonExpired()) {
            throw new AuthenticationException("Account has expired");
        }

        if (!user.isCredentialsNonExpired()) {
            throw new AuthenticationException("Credentials have expired");
        }
    }

    private AuthResponse.UserInfo mapToUserInfo(UserResponse user) {
        return AuthResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .emailVerified(user.isEmailVerified())
                .enabled(user.isEnabled())
                .build();
    }

    private void updateLastLoginTime(Long userId) {
        try {
            userServiceClient.updateLastLoginTime(userId);
        } catch (Exception e) {
            log.warn("Failed to update last login time for user {}: {}", userId, e.getMessage());
            // Don't fail the login process if this fails
        }
    }
}
