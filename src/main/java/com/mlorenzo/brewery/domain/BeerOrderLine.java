package com.mlorenzo.brewery.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "beer_order_lines")
public class BeerOrderLine extends BaseEntity {
	
    @ManyToOne
    private BeerOrder beerOrder;
    
    @ManyToOne
    private Beer beer;

    private Integer orderQuantity = 0;
    private Integer quantityAllocated = 0;
    
    @Builder
    public BeerOrderLine(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate,
                         BeerOrder beerOrder, Beer beer, Integer orderQuantity,
                         Integer quantityAllocated) {
        super(id, version, createdDate, lastModifiedDate);
        this.beerOrder = beerOrder;
        this.beer = beer;
        this.orderQuantity = orderQuantity;
        this.quantityAllocated = quantityAllocated;
    }
}
