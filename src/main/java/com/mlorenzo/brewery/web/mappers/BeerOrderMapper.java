package com.mlorenzo.brewery.web.mappers;

import org.mapstruct.Mapper;

import com.mlorenzo.brewery.domain.BeerOrder;
import com.mlorenzo.brewery.web.models.BeerOrderDto;

@Mapper(uses = {DateMapper.class, BeerOrderLineMapper.class})
public interface BeerOrderMapper {
    BeerOrderDto beerOrderToDto(BeerOrder beerOrder);
    BeerOrder dtoToBeerOrder(BeerOrderDto dto);
}
