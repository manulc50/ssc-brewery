package com.mlorenzo.brewery.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mlorenzo.brewery.domain.Brewery;

import java.util.UUID;

public interface BreweryRepository extends JpaRepository<Brewery, UUID> {
}
