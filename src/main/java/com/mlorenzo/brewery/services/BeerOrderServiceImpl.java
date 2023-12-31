package com.mlorenzo.brewery.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.mlorenzo.brewery.domain.BeerOrder;
import com.mlorenzo.brewery.domain.Customer;
import com.mlorenzo.brewery.domain.OrderStatusEnum;
import com.mlorenzo.brewery.repositories.BeerOrderRepository;
import com.mlorenzo.brewery.repositories.CustomerRepository;
import com.mlorenzo.brewery.web.mappers.BeerOrderMapper;
import com.mlorenzo.brewery.web.models.BeerOrderDto;
import com.mlorenzo.brewery.web.models.BeerOrderPagedList;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class BeerOrderServiceImpl implements BeerOrderService {
    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;
    private final BeerOrderMapper beerOrderMapper;

    @Override
    public BeerOrderPagedList listOrders(UUID customerId, Pageable pageable) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isPresent()) {
        	Page<BeerOrder> beerOrderPage = beerOrderRepository
            		.findAllByCustomer(customerOptional.get(), pageable);
        	return new BeerOrderPagedList(beerOrderPage.stream()
                    .map(beerOrderMapper::beerOrderToDto)
                    .collect(Collectors.toList()), PageRequest.of(beerOrderPage.getPageable().getPageNumber(),
                    		beerOrderPage.getPageable().getPageSize()), beerOrderPage.getTotalElements());
        }
        else 
            return null;
    }
    
	@Override
	public BeerOrderPagedList listOrders(Pageable pageable) {
		Page<BeerOrder> beerOrderPage = beerOrderRepository.findAll(pageable);
    	return new BeerOrderPagedList(beerOrderPage.stream()
                .map(beerOrderMapper::beerOrderToDto)
                .collect(Collectors.toList()), PageRequest.of(beerOrderPage.getPageable().getPageNumber(),
                		beerOrderPage.getPageable().getPageSize()), beerOrderPage.getTotalElements());
	}

    @Transactional
    @Override
    public BeerOrderDto placeOrder(UUID customerId, BeerOrderDto beerOrderDto) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isPresent()) {
            BeerOrder beerOrder = beerOrderMapper.dtoToBeerOrder(beerOrderDto);
            beerOrder.setId(null); //should not be set by outside client
            beerOrder.setCustomer(customerOptional.get());
            beerOrder.setOrderStatus(OrderStatusEnum.NEW);
            beerOrder.getBeerOrderLines().forEach(line -> line.setBeerOrder(beerOrder));
            BeerOrder savedBeerOrder = beerOrderRepository.saveAndFlush(beerOrder);
            log.debug("Saved Beer Order: " + beerOrder.getId());
            return beerOrderMapper.beerOrderToDto(savedBeerOrder);
        }
        //todo add exception type
        throw new RuntimeException("Customer Not Found");
    }

    @Override
    public BeerOrderDto getOrderById(UUID customerId, UUID orderId) {
        return beerOrderMapper.beerOrderToDto(getOrder(customerId, orderId));
    }
    
	@Override
	public BeerOrderDto getOrderByIdSecure(UUID orderId) {
		return beerOrderRepository.findByIdSecure(orderId)
				// Versión simplificada de la expresión "beerOrder -> beerOrderMapper.beerOrderToDto(beerOrder)"
				.map(beerOrderMapper::beerOrderToDto)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}
	
	@Override
	public BeerOrderDto getOrderById(UUID orderId) {
		return beerOrderRepository.findById(orderId)
				// Versión simplificada de la expresión "beerOrder -> beerOrderMapper.beerOrderToDto(beerOrder)"
				.map(beerOrderMapper::beerOrderToDto)
				.orElseThrow();
	}

    @Override
    public void pickupOrder(UUID customerId, UUID orderId) {
        BeerOrder beerOrder = getOrder(customerId, orderId);
        beerOrder.setOrderStatus(OrderStatusEnum.PICKED_UP);
        beerOrderRepository.save(beerOrder);
    }

    private BeerOrder getOrder(UUID customerId, UUID orderId){
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if(customerOptional.isPresent()){
            Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(orderId);
            if(beerOrderOptional.isPresent()){
                BeerOrder beerOrder = beerOrderOptional.get();
                // fall to exception if customer id's do not match - order not for customer
                if(beerOrder.getCustomer().getId().equals(customerId))
                    return beerOrder;
            }
            throw new RuntimeException("Beer Order Not Found");
        }
        throw new RuntimeException("Customer Not Found");
    }

}
