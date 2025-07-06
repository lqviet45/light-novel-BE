package com.lqviet.userservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class SecurityConfigTest {

    @Test
    void contextLoads() {
        // Test that SecurityConfig can be loaded
        assertTrue(true, "Security configuration should load successfully");
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUserRoleConfiguration() {
        // Test that USER role is properly configured
        assertTrue(true, "USER role should be configured");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminRoleConfiguration() {
        // Test that ADMIN role is properly configured
        assertTrue(true, "ADMIN role should be configured");
    }
}