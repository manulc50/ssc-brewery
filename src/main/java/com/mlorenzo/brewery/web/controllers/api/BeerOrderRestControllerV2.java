package com.mlorenzo.brewery.web.controllers.api;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mlorenzo.brewery.security.UserPrincipal;
import com.mlorenzo.brewery.security.annotations.BeerOrderReadPermissionV2;
import com.mlorenzo.brewery.security.annotations.BeerOrderReadPermissionV3;
import com.mlorenzo.brewery.services.BeerOrderService;
import com.mlorenzo.brewery.web.models.BeerOrderDto;
import com.mlorenzo.brewery.web.models.BeerOrderPagedList;

import lombok.RequiredArgsConstructor;

// Nota: Suponemos que nos piden otra versión del controlador sin que se conozca el id del customer

@RequiredArgsConstructor
@RestController
@RequestMapping(BeerOrderRestControllerV2.BASE_PATH)
public class BeerOrderRestControllerV2 {
	public static final String BASE_PATH = "/api/v2/orders";
	private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final BeerOrderService beerOrderService;
    
    // Anotación personalizada que contiene la anotación de Spring Security @PreAuthorize
    @BeerOrderReadPermissionV2
    @GetMapping
    public BeerOrderPagedList listOrders(@AuthenticationPrincipal UserPrincipal userPrincipal,
    									 @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0)
            pageNumber = DEFAULT_PAGE_NUMBER;        
        if (pageSize == null || pageSize < 1)
            pageSize = DEFAULT_PAGE_SIZE;
        return userPrincipal.getUserEntity().getCustomer() != null
        		? beerOrderService.listOrders(userPrincipal.getUserEntity().getCustomer().getId(),
        				PageRequest.of(pageNumber, pageSize))
        		: beerOrderService.listOrders(PageRequest.of(pageNumber, pageSize));
    }
    
    // Primera forma usando sólo anotaciones de seguridad @PreAuthorize y @PostAuthorize
    // Anotación personalizada que contiene la anotación personalizada @BeerOrderReadPermissionV2 y la anotación de Spring Security @PostAuthorize
    @BeerOrderReadPermissionV3
    @GetMapping("{orderId}")
    public BeerOrderDto getOrder(@PathVariable UUID orderId) {
    	return beerOrderService.getOrderById(orderId);
    }
    
    // Segunda forma usando Spring Security, mediante expresiones SpEL, en una consulta JPQL del repositorio "BeerOrderRepository"
    // Anotación personalizada que contiene la anotación de Spring Security @PreAuthorize
    @BeerOrderReadPermissionV2
    @GetMapping("{orderId}/secure")
    public BeerOrderDto getOrderSecure(@PathVariable UUID orderId) {
    	return beerOrderService.getOrderByIdSecure(orderId);
    }
}
