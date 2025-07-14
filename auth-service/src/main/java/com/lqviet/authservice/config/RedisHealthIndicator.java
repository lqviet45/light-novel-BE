package com.lqviet.authservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisHealthIndicator implements HealthIndicator {

    private final RedisConnectionFactory redisConnectionFactory;

    @Override
    public Health health() {
        try {
            redisConnectionFactory.getConnection().ping();
            return Health.up()
                    .withDetail("redis", "Available")
                    .withDetail("host", redisConnectionFactory.getConnection())
                    .build();
        } catch (Exception e) {
            log.error("Redis health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("redis", "Unavailable")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
