package com.lqviet.accountservices;

import com.lqviet.baseentity.annotations.EnableBaseEntity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Account Services
 *
 * @EnableBaseEntity - Enables your custom base entity library with:
 * <pre>
 * - BaseEntity with auditing fields
 * - BaseRepository with soft delete support
 * - Automatic JPA auditing configuration
 * - SoftDeleteUtils for bulk operations
 * </pre>
 */
@SpringBootApplication
@EnableBaseEntity
public class AccountServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServicesApplication.class, args);
    }

}
