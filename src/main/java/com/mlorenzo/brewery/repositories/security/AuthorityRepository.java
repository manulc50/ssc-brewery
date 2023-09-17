package com.mlorenzo.brewery.repositories.security;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mlorenzo.brewery.domain.security.Authority;

public interface AuthorityRepository  extends JpaRepository<Authority,Integer> {
}
