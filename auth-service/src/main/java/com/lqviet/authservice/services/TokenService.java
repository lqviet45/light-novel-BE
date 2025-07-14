package com.lqviet.authservice.services;

import com.lqviet.authservice.exceptions.InvalidTokenException;
import com.lqviet.authservice.exceptions.RefreshTokenNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtService jwtService;

    private static final String REFRESH_TOKEN_KEY_PREFIX = "refresh_token:";
    private static final String BLACKLISTED_TOKEN_KEY_PREFIX = "blacklisted_token:";
    private static final String USER_SESSIONS_KEY_PREFIX = "user_sessions:";

    /**
     * Store refresh token in Redis
     */
    public void storeRefreshToken(String refreshToken, String userEmail) {
        String key = REFRESH_TOKEN_KEY_PREFIX + refreshToken;
        long expirationSeconds = jwtService.getRefreshTokenExpirationInSeconds();

        redisTemplate.opsForValue().set(key, userEmail, Duration.ofSeconds(expirationSeconds));

        // Also track user sessions for logout all functionality
        String userSessionKey = USER_SESSIONS_KEY_PREFIX + userEmail;
        redisTemplate.opsForSet().add(userSessionKey, refreshToken);
        redisTemplate.expire(userSessionKey, Duration.ofSeconds(expirationSeconds));

        log.debug("Stored refresh token for user: {}", userEmail);
    }

    /**
     * Validate refresh token
     */
    public boolean isValidRefreshToken(String refreshToken) {
        try {
            // Check if token exists in Redis and is not blacklisted
            String key = REFRESH_TOKEN_KEY_PREFIX + refreshToken;
            String userEmail = redisTemplate.opsForValue().get(key);

            if (userEmail == null) {
                return false;
            }

            // Check if token is blacklisted
            if (isTokenBlacklisted(refreshToken)) {
                return false;
            }

            // Validate JWT token structure and expiration
            return jwtService.validateToken(refreshToken) && jwtService.isRefreshToken(refreshToken);

        } catch (Exception e) {
            log.warn("Error validating refresh token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get user email from refresh token
     */
    public String getUserEmailFromRefreshToken(String refreshToken) {
        if (!isValidRefreshToken(refreshToken)) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        String key = REFRESH_TOKEN_KEY_PREFIX + refreshToken;
        String userEmail = redisTemplate.opsForValue().get(key);

        if (userEmail == null) {
            throw new RefreshTokenNotFoundException("Refresh token not found");
        }

        return userEmail;
    }

    /**
     * Remove refresh token from Redis
     */
    public void removeRefreshToken(String refreshToken) {
        try {
            String userEmail = jwtService.extractUsername(refreshToken);

            // Remove from main storage
            String key = REFRESH_TOKEN_KEY_PREFIX + refreshToken;
            redisTemplate.delete(key);

            // Remove from user sessions
            String userSessionKey = USER_SESSIONS_KEY_PREFIX + userEmail;
            redisTemplate.opsForSet().remove(userSessionKey, refreshToken);

            log.debug("Removed refresh token for user: {}", userEmail);
        } catch (Exception e) {
            log.warn("Error removing refresh token: {}", e.getMessage());
        }
    }

    /**
     * Blacklist access token (for logout)
     */
    public void blacklistAccessToken(String accessToken) {
        try {
            if (!jwtService.isAccessToken(accessToken)) {
                throw new InvalidTokenException("Not an access token");
            }

            String key = BLACKLISTED_TOKEN_KEY_PREFIX + accessToken;

            // Calculate remaining time until token expiry
            long remainingTime = jwtService.extractExpiration(accessToken).getTime() - System.currentTimeMillis();

            if (remainingTime > 0) {
                redisTemplate.opsForValue().set(key, "blacklisted", Duration.ofMillis(remainingTime));
                log.debug("Blacklisted access token");
            }
        } catch (Exception e) {
            log.warn("Error blacklisting access token: {}", e.getMessage());
        }
    }

    /**
     * Check if token is blacklisted
     */
    public boolean isTokenBlacklisted(String token) {
        try {
            String key = BLACKLISTED_TOKEN_KEY_PREFIX + token;
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.warn("Error checking token blacklist status: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Remove all sessions for a user (logout from all devices)
     */
    public void removeAllUserSessions(String userEmail) {
        try {
            String userSessionKey = USER_SESSIONS_KEY_PREFIX + userEmail;
            Set<String> refreshTokens = redisTemplate.opsForSet().members(userSessionKey);

            if (refreshTokens != null) {
                for (String refreshToken : refreshTokens) {
                    // Remove individual refresh tokens
                    String tokenKey = REFRESH_TOKEN_KEY_PREFIX + refreshToken;
                    redisTemplate.delete(tokenKey);
                }
            }

            // Remove user session tracking
            redisTemplate.delete(userSessionKey);

            log.debug("Removed all sessions for user: {}", userEmail);
        } catch (Exception e) {
            log.warn("Error removing all user sessions: {}", e.getMessage());
        }
    }

    /**
     * Get active session count for user
     */
    public long getActiveSessionCount(String userEmail) {
        try {
            String userSessionKey = USER_SESSIONS_KEY_PREFIX + userEmail;
            Long count = redisTemplate.opsForSet().size(userSessionKey);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.warn("Error getting active session count: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Clean up expired tokens (can be called by scheduled task)
     */
    public void cleanupExpiredTokens() {
        try {
            log.debug("Starting cleanup of expired tokens");

            // Redis automatically expires keys, but we can do additional cleanup if needed
            Set<String> refreshTokenKeys = redisTemplate.keys(REFRESH_TOKEN_KEY_PREFIX + "*");
            Set<String> blacklistedKeys = redisTemplate.keys(BLACKLISTED_TOKEN_KEY_PREFIX + "*");

            for (String key : refreshTokenKeys) {
                if (!redisTemplate.hasKey(key)) {
                    log.debug("Found expired refresh token key: {}", key);
                }
            }

            log.debug("Token cleanup completed. Refresh tokens: {}, Blacklisted: {}",
                    refreshTokenKeys.size(),
                    blacklistedKeys.size());

        } catch (Exception e) {
            log.error("Error during token cleanup: {}", e.getMessage());
        }
    }

    /**
     * Extend refresh token expiration (for remember me functionality)
     */
    public void extendRefreshTokenExpiration(String refreshToken, long additionalSeconds) {
        try {
            String key = REFRESH_TOKEN_KEY_PREFIX + refreshToken;
            redisTemplate.expire(key, Duration.ofSeconds(additionalSeconds));

            String userEmail = jwtService.extractUsername(refreshToken);
            String userSessionKey = USER_SESSIONS_KEY_PREFIX + userEmail;
            redisTemplate.expire(userSessionKey, Duration.ofSeconds(additionalSeconds));

            log.debug("Extended refresh token expiration by {} seconds", additionalSeconds);
        } catch (Exception e) {
            log.warn("Error extending refresh token expiration: {}", e.getMessage());
        }
    }
}