package com.mlorenzo.brewery.web.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BeerDto extends BaseItem {
    private String beerName;
    private BeerStyleEnum beerStyle;
    private String upc;
    private Integer quantityOnHand;
    private Integer quantityToBrew;

    @JsonFormat(shape= JsonFormat.Shape.STRING)
    private BigDecimal price;
    
    @Builder
    public BeerDto(UUID id, Integer version, OffsetDateTime createdDate, OffsetDateTime lastModifiedDate, String beerName,
                   BeerStyleEnum beerStyle, String upc, Integer quantityOnHand, Integer quantityToBrew, BigDecimal price) {
        super(id, version, createdDate, lastModifiedDate);
        this.beerName = beerName;
        this.beerStyle = beerStyle;
        this.upc = upc;
        this.quantityOnHand = quantityOnHand;
        this.quantityToBrew = quantityToBrew;
        this.price = price;
    }

}
