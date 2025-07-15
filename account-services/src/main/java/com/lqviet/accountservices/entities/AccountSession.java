package com.lqviet.accountservices.entities;

import com.lqviet.accountservices.enums.SessionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Account Session entity for tracking user sessions
 */
@Entity
@Table(name = "account_sessions",
        indexes = {
                @Index(name = "idx_session_account", columnList = "account_id"),
                @Index(name = "idx_session_token", columnList = "session_token"),
                @Index(name = "idx_session_status", columnList = "status"),
                @Index(name = "idx_session_expires", columnList = "expires_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true, exclude = {"sessionToken"})
public class AccountSession extends BaseEntity {
    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @NotBlank(message = "Session token is required")
    @Column(name = "session_token", nullable = false, unique = true, length = 255)
    private String sessionToken;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "device_info", length = 255)
    private String deviceInfo;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "started_at", nullable = false)
    @Builder.Default
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name = "last_activity_at", nullable = false)
    @Builder.Default
    private LocalDateTime lastActivityAt = LocalDateTime.now();

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private SessionStatus status = SessionStatus.ACTIVE;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    public boolean isActive() {
        return status == SessionStatus.ACTIVE &&
                expiresAt.isAfter(LocalDateTime.now());
    }

    public void updateActivity() {
        this.lastActivityAt = LocalDateTime.now();
    }

    public void terminate() {
        this.status = SessionStatus.TERMINATED;
        this.endedAt = LocalDateTime.now();
    }

    public void expire() {
        this.status = SessionStatus.EXPIRED;
        this.endedAt = LocalDateTime.now();
    }
}
