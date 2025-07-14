package com.lqviet.accountservices.entities;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity<ID> {
    @Id
    private ID id;
}
