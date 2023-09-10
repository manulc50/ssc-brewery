package com.mlorenzo.brewery.web.mappers;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

import com.mlorenzo.brewery.domain.Beer;
import com.mlorenzo.brewery.web.model.BeerDto;

@Mapper(uses = DateMapper.class)
@DecoratedWith(BeerMapperDecorator.class)
public interface BeerMapper {
    BeerDto beerToBeerDto(Beer beer);
    Beer beerDtoToBeer(BeerDto beerDto);
}
