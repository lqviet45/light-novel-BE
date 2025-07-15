package com.lqviet.accountservices.entities;

import com.lqviet.accountservices.enums.ValueType;
import com.lqviet.baseentity.entities.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "account_settings",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"account_id", "setting_key"})
        },
        indexes = {
                @Index(name = "idx_settings_account", columnList = "account_id"),
                @Index(name = "idx_settings_key", columnList = "setting_key"),
                @Index(name = "idx_settings_category", columnList = "category")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
public class AccountSetting extends BaseEntity {
    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @NotBlank(message = "Setting key is required")
    @Size(max = 100, message = "Setting key must not exceed 100 characters")
    @Column(name = "setting_key", nullable = false, length = 100)
    private String settingKey;

    @Column(name = "setting_value", length = 1000)
    private String settingValue;

    @Size(max = 50, message = "Category must not exceed 50 characters")
    @Column(name = "category", length = 50)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "value_type", nullable = false, length = 20)
    @Builder.Default
    private ValueType valueType = ValueType.STRING;

    @Column(name = "is_encrypted", nullable = false)
    @Builder.Default
    private Boolean isEncrypted = false;

    @Column(name = "description", length = 255)
    private String description;

    public Boolean getBooleanValue() {
        if (valueType == ValueType.BOOLEAN && settingValue != null) {
            return Boolean.parseBoolean(settingValue);
        }
        return null;
    }

    public Integer getIntegerValue() {
        if (valueType == ValueType.INTEGER && settingValue != null) {
            try {
                return Integer.parseInt(settingValue);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public void setBooleanValue(Boolean value) {
        this.valueType = ValueType.BOOLEAN;
        this.settingValue = value != null ? value.toString() : null;
    }

    public void setIntegerValue(Integer value) {
        this.valueType = ValueType.INTEGER;
        this.settingValue = value != null ? value.toString() : null;
    }
}
