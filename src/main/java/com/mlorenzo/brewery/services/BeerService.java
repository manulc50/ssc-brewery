package com.mlorenzo.brewery.services;

import org.springframework.data.domain.PageRequest;

import com.mlorenzo.brewery.web.models.BeerDto;
import com.mlorenzo.brewery.web.models.BeerPagedList;
import com.mlorenzo.brewery.web.models.BeerStyleEnum;

import java.util.UUID;

public interface BeerService {
    BeerPagedList listBeers(String beerName, BeerStyleEnum beerStyle, PageRequest pageRequest, Boolean showInventoryOnHand);
    BeerDto findBeerById(UUID beerId,  Boolean showInventoryOnHand);
    BeerDto saveBeer(BeerDto beerDto);
    void updateBeer(UUID beerId, BeerDto beerDto);
    void deleteById(UUID beerId);
    BeerDto findBeerByUpc(String upc);
}
