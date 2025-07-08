package com.lqviet.userservice.repositories;

import com.lqviet.userservice.entities.Role;
import com.lqviet.userservice.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {


    @Query("SELECT r FROM Role r JOIN FETCH r.privileges WHERE r.name = :name")
    Optional<Role> findByNameWithPrivileges(@Param("name") String name);

    boolean existsByName(RoleName name);

    Optional<Role> findByName(RoleName name);

    Set<Role> findByNameIn(Collection<RoleName> names);
}