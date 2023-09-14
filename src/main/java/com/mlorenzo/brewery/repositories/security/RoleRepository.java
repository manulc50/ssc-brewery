package com.mlorenzo.brewery.repositories.security;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mlorenzo.brewery.domain.security.Role;

public interface RoleRepository extends JpaRepository<Role,Integer> {
}
