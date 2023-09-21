package com.mlorenzo.brewery.repositories.security;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mlorenzo.brewery.domain.security.LoginFailure;
import com.mlorenzo.brewery.domain.security.User;

public interface LoginFailureRepository extends JpaRepository<LoginFailure, Integer> {
	List<LoginFailure> findByUserAndCreatedDateIsAfter(User user, Timestamp time);
}
