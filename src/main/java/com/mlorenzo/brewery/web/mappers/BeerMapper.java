package com.mlorenzo.brewery.web.mappers;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mlorenzo.brewery.domain.Beer;
import com.mlorenzo.brewery.web.models.BeerDto;

@Mapper(uses = DateMapper.class)
@DecoratedWith(BeerMapperDecorator.class)
public interface BeerMapper {
	
	@Mapping(source = "minOnHand" , target = "quantityOnHand")
    BeerDto beerToBeerDto(Beer beer);
	
	@Mapping(source = "quantityOnHand" , target = "minOnHand")
    Beer beerDtoToBeer(BeerDto beerDto);
}
