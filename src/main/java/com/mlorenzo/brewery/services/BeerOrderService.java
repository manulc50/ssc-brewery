package com.mlorenzo.brewery.services;

import org.springframework.data.domain.Pageable;

import com.mlorenzo.brewery.web.models.BeerOrderDto;
import com.mlorenzo.brewery.web.models.BeerOrderPagedList;

import java.util.UUID;

public interface BeerOrderService {
    BeerOrderPagedList listOrders(UUID customerId, Pageable pageable);
    BeerOrderPagedList listOrders(Pageable pageable);
    BeerOrderDto placeOrder(UUID customerId, BeerOrderDto beerOrderDto);
    BeerOrderDto getOrderById(UUID customerId, UUID orderId);
    BeerOrderDto getOrderByIdSecure(UUID orderId);
    BeerOrderDto getOrderById(UUID orderId);
    void pickupOrder(UUID customerId, UUID orderId);
}
