package com.lqviet.accountservices.entities;

import com.lqviet.accountservices.enums.AuditAction;
import com.lqviet.baseentity.entities.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Account Audit Log entity for tracking changes
 */
@Entity
@Table(name = "account_audit_logs",
        indexes = {
                @Index(name = "idx_audit_account", columnList = "account_id"),
                @Index(name = "idx_audit_action", columnList = "action"),
                @Index(name = "idx_audit_table", columnList = "table_name"),
                @Index(name = "idx_audit_date", columnList = "action_date")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
public class AccountAuditLog extends BaseEntity {
    @Column(name = "account_id")
    private Long accountId;

    @NotBlank(message = "Table name is required")
    @Size(max = 100, message = "Table name must not exceed 100 characters")
    @Column(name = "table_name", nullable = false, length = 100)
    private String tableName;

    @Column(name = "record_id")
    private Long recordId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 20)
    private AuditAction action;

    @Column(name = "old_values", columnDefinition = "TEXT")
    private String oldValues;

    @Column(name = "new_values", columnDefinition = "TEXT")
    private String newValues;

    @Column(name = "changed_fields", length = 500)
    private String changedFields;

    @Column(name = "action_date", nullable = false)
    @Builder.Default
    private LocalDateTime actionDate = LocalDateTime.now();

    @Column(name = "performed_by")
    private Long performedBy;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    public static AccountAuditLog forInsert(String tableName, Long recordId, String newValues, Long performedBy) {
        return AccountAuditLog.builder()
                .tableName(tableName)
                .recordId(recordId)
                .action(AuditAction.INSERT)
                .newValues(newValues)
                .performedBy(performedBy)
                .build();
    }

    public static AccountAuditLog forUpdate(String tableName, Long recordId, String oldValues,
                                            String newValues, String changedFields, Long performedBy) {
        return AccountAuditLog.builder()
                .tableName(tableName)
                .recordId(recordId)
                .action(AuditAction.UPDATE)
                .oldValues(oldValues)
                .newValues(newValues)
                .changedFields(changedFields)
                .performedBy(performedBy)
                .build();
    }

    public static AccountAuditLog forDelete(String tableName, Long recordId, String oldValues, Long performedBy) {
        return AccountAuditLog.builder()
                .tableName(tableName)
                .recordId(recordId)
                .action(AuditAction.DELETE)
                .oldValues(oldValues)
                .performedBy(performedBy)
                .build();
    }
}
