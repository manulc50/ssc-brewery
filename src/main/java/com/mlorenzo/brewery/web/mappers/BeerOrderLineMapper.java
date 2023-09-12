package com.mlorenzo.brewery.web.mappers;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

import com.mlorenzo.brewery.domain.BeerOrderLine;
import com.mlorenzo.brewery.web.models.BeerOrderLineDto;

@Mapper(uses = {DateMapper.class})
@DecoratedWith(BeerOrderLineMapperDecorator.class)
public interface BeerOrderLineMapper {
    BeerOrderLineDto beerOrderLineToDto(BeerOrderLine line);
    BeerOrderLine dtoToBeerOrderLine(BeerOrderLineDto dto);
}
