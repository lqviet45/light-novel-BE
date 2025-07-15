package com.lqviet.accountservices.entities;

import com.lqviet.baseentity.entities.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Account Role entity for role-based access control
 */
@Entity
@Table(name = "account_roles",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"account_id", "role_name"})
        },
        indexes = {
                @Index(name = "idx_account_role_account", columnList = "account_id"),
                @Index(name = "idx_account_role_name", columnList = "role_name")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
public class AccountRole extends BaseEntity {

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @NotBlank(message = "Role name is required")
    @Size(max = 50, message = "Role name must not exceed 50 characters")
    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

    @Column(name = "granted_by")
    private Long grantedBy;

    @Column(name = "granted_at", nullable = false)
    @Builder.Default
    private LocalDateTime grantedAt = LocalDateTime.now();

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public boolean isEffective() {
        return isActive && !isExpired() && !isDeleted();
    }
}
