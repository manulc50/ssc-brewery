package com.mlorenzo.brewery.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mlorenzo.brewery.domain.BeerOrder;
import com.mlorenzo.brewery.domain.Customer;
import com.mlorenzo.brewery.domain.OrderStatusEnum;

import java.util.List;
import java.util.UUID;

public interface BeerOrderRepository  extends JpaRepository<BeerOrder, UUID> {
    Page<BeerOrder> findAllByCustomer(Customer customer, Pageable pageable);
    List<BeerOrder> findAllByOrderStatus(OrderStatusEnum orderStatusEnum);
}
