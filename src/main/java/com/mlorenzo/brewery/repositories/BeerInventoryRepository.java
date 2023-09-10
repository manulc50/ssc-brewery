package com.mlorenzo.brewery.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mlorenzo.brewery.domain.Beer;
import com.mlorenzo.brewery.domain.BeerInventory;

import java.util.List;
import java.util.UUID;

public interface BeerInventoryRepository extends JpaRepository<BeerInventory, UUID> {
    List<BeerInventory> findAllByBeer(Beer beer);
}
