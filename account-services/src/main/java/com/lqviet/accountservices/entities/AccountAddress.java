package com.lqviet.accountservices.entities;

import com.lqviet.accountservices.enums.AddressType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Account Address entity
 */
@Entity
@Table(name = "account_addresses",
        indexes = {
                @Index(name = "idx_address_account", columnList = "account_id"),
                @Index(name = "idx_address_type", columnList = "address_type"),
                @Index(name = "idx_address_country", columnList = "country")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
public class AccountAddress extends BaseEntity {
    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", nullable = false, length = 20)
    @Builder.Default
    private AddressType addressType = AddressType.HOME;

    @NotBlank(message = "Street address is required")
    @Size(max = 255, message = "Street address must not exceed 255 characters")
    @Column(name = "street_address", nullable = false, length = 255)
    private String streetAddress;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    @Column(name = "country", nullable = false, length = 100)
    private String country;

    public String getFullAddress() {
        return streetAddress +
                ", " + city +
                ", " + country;
    }
}
