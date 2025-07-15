package com.lqviet.accountservices.entities;

import com.lqviet.accountservices.enums.ActivityType;
import com.lqviet.baseentity.entities.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Account Activity Log entity for tracking user activities
 */
@Entity
@Table(name = "account_activities",
        indexes = {
                @Index(name = "idx_activity_account", columnList = "account_id"),
                @Index(name = "idx_activity_type", columnList = "activity_type"),
                @Index(name = "idx_activity_date", columnList = "activity_date"),
                @Index(name = "idx_activity_ip", columnList = "ip_address")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
public class AccountActivity extends BaseEntity {
    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false, length = 50)
    private ActivityType activityType;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "activity_date", nullable = false)
    @Builder.Default
    private LocalDateTime activityDate = LocalDateTime.now();

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "device_info", length = 255)
    private String deviceInfo;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "success", nullable = false)
    @Builder.Default
    private Boolean success = true;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "additional_data", columnDefinition = "TEXT")
    private String additionalData;
}
