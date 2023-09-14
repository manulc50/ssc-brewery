package com.mlorenzo.brewery.web.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mlorenzo.brewery.domain.BeerOrderLine;
import com.mlorenzo.brewery.repositories.BeerRepository;
import com.mlorenzo.brewery.web.models.BeerOrderLineDto;

public abstract class BeerOrderLineMapperDecorator implements BeerOrderLineMapper {
    private BeerRepository beerRepository;
    private BeerOrderLineMapper beerOrderLineMapper;

    @Autowired
    public void setBeerRepository(BeerRepository beerRepository) {
        this.beerRepository = beerRepository;
    }

    @Autowired
    @Qualifier("delegate")
    public void setBeerOrderLineMapper(BeerOrderLineMapper beerOrderLineMapper) {
        this.beerOrderLineMapper = beerOrderLineMapper;
    }

    @Override
    public BeerOrderLineDto beerOrderLineToDto(BeerOrderLine line) {
        BeerOrderLineDto orderLineDto = beerOrderLineMapper.beerOrderLineToDto(line);
        orderLineDto.setBeerId(line.getBeer().getId());
        return orderLineDto;
    }

    @Override
    public BeerOrderLine dtoToBeerOrderLine(BeerOrderLineDto dto) {
        BeerOrderLine beerOrderLine = beerOrderLineMapper.dtoToBeerOrderLine(dto);
        beerOrderLine.setBeer(beerRepository.getOne(dto.getBeerId()));
        beerOrderLine.setQuantityAllocated(0);
        return beerOrderLine;
    }
}
