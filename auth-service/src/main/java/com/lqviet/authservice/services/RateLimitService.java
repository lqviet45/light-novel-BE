package com.lqviet.authservice.services;

import com.lqviet.authservice.exceptions.RateLimitExceededException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitService {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${rate-limit.login.requests:5}")
    private int loginMaxRequests;

    @Value("${rate-limit.login.window:900}")
    private long loginWindowSeconds;

    @Value("${rate-limit.refresh.requests:10}")
    private int refreshMaxRequests;

    @Value("${rate-limit.refresh.window:3600}")
    private long refreshWindowSeconds;

    private static final String LOGIN_RATE_LIMIT_KEY_PREFIX = "rate_limit:login:";
    private static final String REFRESH_RATE_LIMIT_KEY_PREFIX = "rate_limit:refresh:";

    /**
     * Check rate limit for login attempts
     */
    public void checkLoginRateLimit(String identifier) {
        String key = LOGIN_RATE_LIMIT_KEY_PREFIX + identifier;
        checkRateLimit(key, loginMaxRequests, loginWindowSeconds, "login");
    }

    /**
     * Check rate limit for token refresh attempts
     */
    public void checkRefreshRateLimit(String identifier) {
        String key = REFRESH_RATE_LIMIT_KEY_PREFIX + identifier;
        checkRateLimit(key, refreshMaxRequests, refreshWindowSeconds, "refresh");
    }

    /**
     * Generic rate limiting implementation using Redis
     */
    private void checkRateLimit(String key, int maxRequests, long windowSeconds, String operation) {
        try {
            // Get current count
            String currentCountStr = redisTemplate.opsForValue().get(key);
            int currentCount = currentCountStr != null ? Integer.parseInt(currentCountStr) : 0;

            if (currentCount >= maxRequests) {
                log.warn("Rate limit exceeded for {} operation. Key: {}, Count: {}",
                        operation, key, currentCount);
                throw new RateLimitExceededException(
                        String.format("Too many %s attempts. Please try again later.", operation));
            }

            // Increment count
            if (currentCount == 0) {
                // First request in the window
                redisTemplate.opsForValue().set(key, "1", Duration.ofSeconds(windowSeconds));
            } else {
                // Increment existing count
                redisTemplate.opsForValue().increment(key);
            }

            log.debug("Rate limit check passed for {} operation. Key: {}, Count: {}/{}",
                    operation, key, currentCount + 1, maxRequests);

        } catch (RateLimitExceededException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error checking rate limit for {} operation: {}", operation, e.getMessage());
            // In case of Redis errors, allow the request to proceed
            // This prevents Redis issues from blocking all authentication
        }
    }

    /**
     * Reset rate limit for an identifier (useful for admin operations)
     */
    public void resetLoginRateLimit(String identifier) {
        String key = LOGIN_RATE_LIMIT_KEY_PREFIX + identifier;
        redisTemplate.delete(key);
        log.info("Reset login rate limit for: {}", identifier);
    }

    /**
     * Reset refresh rate limit for an identifier
     */
    public void resetRefreshRateLimit(String identifier) {
        String key = REFRESH_RATE_LIMIT_KEY_PREFIX + identifier;
        redisTemplate.delete(key);
        log.info("Reset refresh rate limit for: {}", identifier);
    }

    /**
     * Get remaining requests for login
     */
    public int getRemainingLoginRequests(String identifier) {
        String key = LOGIN_RATE_LIMIT_KEY_PREFIX + identifier;
        return getRemainingRequests(key, loginMaxRequests);
    }

    /**
     * Get remaining requests for refresh
     */
    public int getRemainingRefreshRequests(String identifier) {
        String key = REFRESH_RATE_LIMIT_KEY_PREFIX + identifier;
        return getRemainingRequests(key, refreshMaxRequests);
    }

    /**
     * Get remaining requests for a given key
     */
    private int getRemainingRequests(String key, int maxRequests) {
        try {
            String currentCountStr = redisTemplate.opsForValue().get(key);
            int currentCount = currentCountStr != null ? Integer.parseInt(currentCountStr) : 0;
            return Math.max(0, maxRequests - currentCount);
        } catch (Exception e) {
            log.warn("Error getting remaining requests for key {}: {}", key, e.getMessage());
            return maxRequests; // Return max requests if there's an error
        }
    }

    /**
     * Get time until rate limit resets
     */
    public long getTimeUntilReset(String identifier, String operation) {
        String key = operation.equals("login") ?
                LOGIN_RATE_LIMIT_KEY_PREFIX + identifier :
                REFRESH_RATE_LIMIT_KEY_PREFIX + identifier;

        try {
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return ttl != null ? Math.max(0, ttl) : 0;
        } catch (Exception e) {
            log.warn("Error getting TTL for key {}: {}", key, e.getMessage());
            return 0;
        }
    }

    /**
     * Check if identifier is currently rate limited
     */
    public boolean isRateLimited(String identifier, String operation) {
        if ("login".equals(operation)) {
            return getRemainingLoginRequests(identifier) <= 0;
        } else if ("refresh".equals(operation)) {
            return getRemainingRefreshRequests(identifier) <= 0;
        }
        return false;
    }

    /**
     * Get rate limit info for monitoring
     */
    public RateLimitInfo getRateLimitInfo(String identifier, String operation) {
        String key = operation.equals("login") ?
                LOGIN_RATE_LIMIT_KEY_PREFIX + identifier :
                REFRESH_RATE_LIMIT_KEY_PREFIX + identifier;

        int maxRequests = operation.equals("login") ? loginMaxRequests : refreshMaxRequests;
        int remaining = getRemainingRequests(key, maxRequests);
        long resetTime = getTimeUntilReset(identifier, operation);

        return RateLimitInfo.builder()
                .maxRequests(maxRequests)
                .remainingRequests(remaining)
                .resetTimeSeconds(resetTime)
                .isLimited(remaining <= 0)
                .build();
    }

    /**
     * Rate limit information DTO
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class RateLimitInfo {
        private int maxRequests;
        private int remainingRequests;
        private long resetTimeSeconds;
        private boolean isLimited;
    }
}
