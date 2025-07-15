package com.lqviet.accountservices.entities;

import com.lqviet.accountservices.enums.Gender;
import com.lqviet.baseentity.entities.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * User Profile entity for additional user information
 */
@Entity
@Table(name = "user_profiles",
        indexes = {
                @Index(name = "idx_profile_account", columnList = "account_id"),
                @Index(name = "idx_profile_display_name", columnList = "display_name")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
public class UserProfile extends BaseEntity {
    @Column(name = "account_id", nullable = false, unique = true)
    private Long accountId;

    @NotBlank(message = "Display name is required")
    @Size(max = 100, message = "Display name must not exceed 100 characters")
    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Size(max = 500, message = "Bio must not exceed 500 characters")
    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "website_url", length = 255)
    private String websiteUrl;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "date_of_birth")
    private LocalDateTime dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 20)
    private Gender gender;

    @Column(name = "timezone", length = 50)
    @Builder.Default
    private String timezone = "UTC";

    @Column(name = "language", length = 10)
    @Builder.Default
    private String language = "en";

    @Column(name = "currency", length = 3)
    @Builder.Default
    private String currency = "USD";

    @Column(name = "date_format", length = 20)
    @Builder.Default
    private String dateFormat = "yyyy-MM-dd";

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private Boolean isPublic = true;
}
