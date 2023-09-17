package com.mlorenzo.brewery.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mlorenzo.brewery.domain.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    List<Customer> findAllByCustomerNameLike(String customerName);
    Optional<Customer> findByCustomerName(String customerName);
}
