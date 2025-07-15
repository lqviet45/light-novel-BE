package com.lqviet.accountservices.entities;

import com.lqviet.accountservices.enums.AccountStatus;
import com.lqviet.accountservices.enums.AccountType;
import com.lqviet.baseentity.entities.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Account entity representing user accounts in the system
 */
@Entity
@Table(name = "accounts",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        },
        indexes = {
                @Index(name = "idx_account_username", columnList = "username"),
                @Index(name = "idx_account_email", columnList = "email"),
                @Index(name = "idx_account_status", columnList = "status"),
                @Index(name = "idx_account_type", columnList = "account_type")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true, exclude = {"password"})
public class Account extends BaseEntity {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username can only contain letters, numbers, underscores, and hyphens")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Password must be at least 8 characters long")
    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 50, message = "First name must not exceed 50 characters")
    @Column(name = "first_name", length = 50)
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @Column(name = "last_name", length = 50)
    private String lastName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private AccountStatus status = AccountStatus.PENDING_VERIFICATION;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 20)
    @Builder.Default
    private AccountType accountType = AccountType.USER;

    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;

    @Column(name = "phone_verified", nullable = false)
    @Builder.Default
    private Boolean phoneVerified = false;

    @Column(name = "email_verification_token")
    private String emailVerificationToken;

    @Column(name = "email_verification_expires_at")
    private LocalDateTime emailVerificationExpiresAt;

    @Column(name = "password_reset_token")
    private String passwordResetToken;

    @Column(name = "password_reset_expires_at")
    private LocalDateTime passwordResetExpiresAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "failed_login_attempts", nullable = false)
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    // Business methods
    public String getFullName() {
        if (firstName == null && lastName == null) {
            return username;
        }
        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }

    public boolean isAccountNonLocked() {
        return status != AccountStatus.LOCKED &&
                status != AccountStatus.SUSPENDED &&
                (lockedUntil == null || lockedUntil.isBefore(LocalDateTime.now()));
    }

    public boolean isEnabled() {
        return status == AccountStatus.ACTIVE;
    }

    public void activate() {
        this.status = AccountStatus.ACTIVE;
        this.emailVerified = true;
        this.emailVerificationToken = null;
        this.emailVerificationExpiresAt = null;
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
    }

    public void lock() {
        this.status = AccountStatus.LOCKED;
    }

    public void suspend() {
        this.status = AccountStatus.SUSPENDED;
    }

    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) {
            this.status = AccountStatus.LOCKED;
            this.lockedUntil = LocalDateTime.now().plusHours(1);
        }
    }

    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
        if (this.status == AccountStatus.LOCKED) {
            this.status = AccountStatus.ACTIVE;
        }
    }

    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
        resetFailedLoginAttempts();
    }

    public boolean isAdmin() {
        return accountType == AccountType.ADMIN || accountType == AccountType.SUPER_ADMIN;
    }

    public boolean isModerator() {
        return accountType == AccountType.MODERATOR || isAdmin();
    }

}
