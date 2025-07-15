package com.lqviet.accountservices.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Account Permission entity for fine-grained permissions
 */
@Entity
@Table(name = "account_permissions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"account_id", "permission_name", "resource_type", "resource_id"})
        },
        indexes = {
                @Index(name = "idx_account_permission_account", columnList = "account_id"),
                @Index(name = "idx_account_permission_name", columnList = "permission_name"),
                @Index(name = "idx_account_permission_resource", columnList = "resource_type,resource_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
public class AccountPermission extends BaseEntity {
    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @NotBlank(message = "Permission name is required")
    @Size(max = 100, message = "Permission name must not exceed 100 characters")
    @Column(name = "permission_name", nullable = false, length = 100)
    private String permissionName;

    @Size(max = 50, message = "Resource type must not exceed 50 characters")
    @Column(name = "resource_type", length = 50)
    private String resourceType;

    @Column(name = "resource_id")
    private Long resourceId;

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

    public String getResourceIdentifier() {
        if (resourceType == null) {
            return "global";
        }
        return resourceId != null ? resourceType + ":" + resourceId : resourceType + ":*";
    }
}
