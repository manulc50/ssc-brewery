package com.mlorenzo.brewery.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.mlorenzo.brewery.domain.BeerOrderLine;

import java.util.UUID;

public interface BeerOrderLineRepository extends PagingAndSortingRepository<BeerOrderLine, UUID> {
}
