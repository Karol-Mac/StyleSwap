package com.restapi.styleswap.repository;

import com.restapi.styleswap.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNameContainingIgnoreCase(String name);
}
