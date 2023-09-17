package com.mlorenzo.brewery.web.controllers.api;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import com.mlorenzo.brewery.security.annotations.BeerOrderCreatePermission;
import com.mlorenzo.brewery.security.annotations.BeerOrderReadPermission;
import com.mlorenzo.brewery.security.annotations.BeerOrderPickupPermission;
import com.mlorenzo.brewery.services.BeerOrderService;
import com.mlorenzo.brewery.web.models.BeerOrderDto;
import com.mlorenzo.brewery.web.models.BeerOrderPagedList;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(BeerOrderRestController.BASE_PATH)
public class BeerOrderRestController {
	public static final String BASE_PATH = "/api/v1/customers/{customerId}/orders";
	private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final BeerOrderService beerOrderService;

    // Anotación personalizada que contiene la anotación de Spring Security @PreAuthorize
    @BeerOrderReadPermission
    @GetMapping
    public BeerOrderPagedList listOrders(@PathVariable("customerId") UUID customerId,
                                         @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0)
            pageNumber = DEFAULT_PAGE_NUMBER;        
        if (pageSize == null || pageSize < 1)
            pageSize = DEFAULT_PAGE_SIZE;
        return beerOrderService.listOrders(customerId, PageRequest.of(pageNumber, pageSize));
    }
    
    // Anotación personalizada que contiene la anotación de Spring Security @PreAuthorize
    @BeerOrderReadPermission
    @GetMapping("/{orderId}")
    public BeerOrderDto getOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId){
        return beerOrderService.getOrderById(customerId, orderId);
    }

    // Anotación personalizada que contiene la anotación de Spring Security @PreAuthorize
    @BeerOrderCreatePermission
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeerOrderDto placeOrder(@PathVariable("customerId") UUID customerId, @RequestBody BeerOrderDto beerOrderDto) {
        return beerOrderService.placeOrder(customerId, beerOrderDto);
    }

    // Anotación personalizada que contiene la anotación de Spring Security @PreAuthorize
    @BeerOrderPickupPermission
    @PatchMapping("/{orderId}/pickup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pickupOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId){
        beerOrderService.pickupOrder(customerId, orderId);
    }
}
