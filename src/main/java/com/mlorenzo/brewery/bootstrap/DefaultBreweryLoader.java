package com.mlorenzo.brewery.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mlorenzo.brewery.domain.*;
import com.mlorenzo.brewery.repositories.*;
import com.mlorenzo.brewery.web.models.BeerStyleEnum;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class DefaultBreweryLoader {
    public static final String TASTING_ROOM = "Tasting Room";
    public static final String BEER_1_UPC = "0631234200036";
    public static final String BEER_2_UPC = "0631234300019";
    public static final String BEER_3_UPC = "0083783375213";

    private final BreweryRepository breweryRepository;
    private final BeerRepository beerRepository;
    private final BeerInventoryRepository beerInventoryRepository;
    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        loadBreweryData();
        loadBeerData();
        loadCustomerData();
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

    @Transactional
    private void loadCustomerData() {
    	if(customerRepository.count() == 0) {
	        Customer tastingRoom = Customer.builder()
	                .customerName(TASTING_ROOM)
	                .apiKey(UUID.randomUUID())
	                .build();
	        Customer savedCustomer = customerRepository.save(tastingRoom);
	        beerRepository.findAll().forEach(beer -> {
	        	BeerOrder beerOrder = BeerOrder.builder()
	                    .customer(savedCustomer)
	                    .orderStatus(OrderStatusEnum.NEW)
	                    .build();
	        	Set<BeerOrderLine> beerOrderLines = Set.of(BeerOrderLine.builder()
	        			.beer(beer)
	        			.beerOrder(beerOrder)
	                    .orderQuantity(2)
	                    .quantityAllocated(7)
	                    .build());
	        	beerOrder.setBeerOrderLines(beerOrderLines);
	        	beerOrderRepository.save(beerOrder);
	        });
    	}
    }
}
