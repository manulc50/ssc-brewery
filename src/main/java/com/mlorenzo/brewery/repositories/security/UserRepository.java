package com.mlorenzo.brewery.repositories.security;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mlorenzo.brewery.domain.security.User;

public interface UserRepository extends JpaRepository<User,Integer> {
	Optional<User> findByUsername(String username);
}
