package com.mlorenzo.brewery.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mlorenzo.brewery.domain.*;
import com.mlorenzo.brewery.repositories.*;
import com.mlorenzo.brewery.web.models.BeerStyleEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
// Esta clase debe ejecutarse después de la clase "SecurityBreweryLoader" porque necesita de ella la inserción
// de los customers en la base de datos
@Order(2)
@Component
public class DefaultBreweryLoader implements CommandLineRunner {
	private static final String BEER_1_UPC = "0631234200036";
    private static final String BEER_2_UPC = "0631234300019";
    private static final String BEER_3_UPC = "0083783375213";
    
    private final BreweryRepository breweryRepository;
    private final BeerRepository beerRepository;
    private final BeerInventoryRepository beerInventoryRepository;
    private final CustomerRepository customerRepository;
    private final BeerOrderRepository beerOrderRepository;

    @Override
	public void run(String... args) throws Exception {
        loadBreweryData();
        loadBeerData();
        loadBeerOrderData();
    }
    
    private void loadBreweryData() {
        if (breweryRepository.count() == 0) {
            breweryRepository.save(Brewery
                    .builder()
                    .breweryName("Cage Brewing")
                    .build());
        }
    }
    
    @Transactional
    private void loadBeerData() {
    	if(beerRepository.count() == 0) {
    		Beer mangoBobs = Beer.builder()
                    .beerName("Mango Bobs")
                    .beerStyle(BeerStyleEnum.IPA)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_1_UPC)
                    .price(BigDecimal.valueOf(9.75))
                    .build();
            Beer savedMangoBobs = beerRepository.save(mangoBobs);
            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(savedMangoBobs)
                    .quantityOnHand(500)
                    .build());
            Beer galaxyCat = Beer.builder()
                    .beerName("Galaxy Cat")
                    .beerStyle(BeerStyleEnum.PALE_ALE)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_2_UPC)
                    .price(BigDecimal.valueOf(10.40))
                    .build();
            Beer savedGalaxyCat = beerRepository.save(galaxyCat);
            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(savedGalaxyCat)
                    .quantityOnHand(500)
                    .build());
            Beer pinball = Beer.builder()
                    .beerName("Pinball Porter")
                    .beerStyle(BeerStyleEnum.PORTER)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_3_UPC)
                    .price(BigDecimal.valueOf(12.99))
                    .build();
            Beer savedPinball = beerRepository.save(pinball);
            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(savedPinball)
                    .quantityOnHand(500)
                    .build());
    	}
    }

    private void loadBeerOrderData() {
    	if(beerOrderRepository.count() == 0) {
    		List<Customer> customers = customerRepository.findAll();
    		List<BeerOrder> beerOrders = customers.stream()
    				// Versión simplificada de la expresión "customer -> createOrder(customer)"
    				.map(this::createOrder)
    				.collect(Collectors.toList());
    		beerOrderRepository.saveAll(beerOrders);
    		log.debug("Orders Loaded: " + beerOrderRepository.count());
    	}
    }
    
    private BeerOrder createOrder(Customer customer) {
    	BeerOrder beerOrder = BeerOrder.builder()
    		.customer(customer)
    		.orderStatus(OrderStatusEnum.NEW)
        	.build();
    	Set<BeerOrderLine> beerOrderLines = Set.of(BeerOrderLine.builder()
    			.beer(beerRepository.findByUpc(BEER_1_UPC).orElseThrow())
                .orderQuantity(2)
                .beerOrder(beerOrder)
                .build());
    	beerOrder.setBeerOrderLines(beerOrderLines);
        return beerOrder;
    }
}
