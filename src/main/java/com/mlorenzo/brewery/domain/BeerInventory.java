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
@Table(name = "beer_inventories")
public class BeerInventory extends BaseEntity {
	
    @ManyToOne
    private Beer beer;

    private Integer quantityOnHand = 0;
    
    @Builder
    public BeerInventory(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate, Beer beer,
                         Integer quantityOnHand) {
        super(id, version, createdDate, lastModifiedDate);
        this.beer = beer;
        this.quantityOnHand = quantityOnHand;
    }
}
