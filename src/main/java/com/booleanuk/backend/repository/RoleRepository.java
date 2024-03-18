package com.booleanuk.backend.repository;

import com.booleanuk.backend.model.Role;
import com.booleanuk.backend.model.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
}
