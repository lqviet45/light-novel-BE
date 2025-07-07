package com.lqviet.userservice.repositories;

import com.lqviet.userservice.entities.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Optional<Privilege> findByName(String name);
    boolean existsByName(String name);
}