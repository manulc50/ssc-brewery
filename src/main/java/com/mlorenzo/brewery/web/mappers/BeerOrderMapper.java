package com.mlorenzo.brewery.web.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mlorenzo.brewery.domain.BeerOrder;
import com.mlorenzo.brewery.web.models.BeerOrderDto;

@Mapper(uses = {DateMapper.class, BeerOrderLineMapper.class})
public interface BeerOrderMapper {
	
	@Mapping(source = "customer.id", target = "customerId")
    BeerOrderDto beerOrderToDto(BeerOrder beerOrder);
	
    BeerOrder dtoToBeerOrder(BeerOrderDto dto);
}
