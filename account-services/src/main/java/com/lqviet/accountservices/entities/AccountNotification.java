package com.lqviet.accountservices.entities;

import com.lqviet.accountservices.enums.NotificationStatus;
import com.lqviet.accountservices.enums.NotificationType;
import com.lqviet.baseentity.entities.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Account Notification entity for user notifications
 */
@Entity
@Table(name = "account_notifications",
        indexes = {
                @Index(name = "idx_notification_account", columnList = "account_id"),
                @Index(name = "idx_notification_type", columnList = "notification_type"),
                @Index(name = "idx_notification_status", columnList = "status"),
                @Index(name = "idx_notification_date", columnList = "created_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
public class AccountNotification extends BaseEntity {

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "message", length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 30)
    @Builder.Default
    private NotificationType notificationType = NotificationType.INFO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.UNREAD;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "action_url", length = 500)
    private String actionUrl;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    public void markAsRead() {
        this.status = NotificationStatus.READ;
        this.readAt = LocalDateTime.now();
    }

    public boolean isRead() {
        return status == NotificationStatus.READ || status == NotificationStatus.ARCHIVED;
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }
}
