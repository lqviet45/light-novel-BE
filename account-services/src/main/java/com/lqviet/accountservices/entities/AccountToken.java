package com.lqviet.accountservices.entities;

import com.lqviet.accountservices.enums.TokenType;
import com.lqviet.baseentity.entities.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Account Token entity for managing various tokens (reset, verification, etc.)
 */
@Entity
@Table(name = "account_tokens",
        indexes = {
                @Index(name = "idx_token_account", columnList = "account_id"),
                @Index(name = "idx_token_type", columnList = "token_type"),
                @Index(name = "idx_token_value", columnList = "token_value"),
                @Index(name = "idx_token_expires", columnList = "expires_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true, exclude = {"tokenValue"})
public class AccountToken extends BaseEntity {
    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false, length = 30)
    private TokenType tokenType;

    @NotBlank(message = "Token value is required")
    @Column(name = "token_value", nullable = false, unique = true, length = 255)
    private String tokenValue;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "is_used", nullable = false)
    @Builder.Default
    private Boolean isUsed = false;

    @Column(name = "additional_data", length = 500)
    private String additionalData;

    public boolean isValid() {
        return !isUsed && expiresAt.isAfter(LocalDateTime.now()) && !isDeleted();
    }

    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    public void markAsUsed() {
        this.isUsed = true;
        this.usedAt = LocalDateTime.now();
    }
}
