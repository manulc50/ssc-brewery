package com.mlorenzo.brewery.repositories.security;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mlorenzo.brewery.domain.security.LoginSuccess;

public interface LoginSuccessRepository extends JpaRepository<LoginSuccess, Integer> {
}
