package com.mlorenzo.brewery.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "customers")
public class Customer extends BaseEntity {
    private String customerName;

    @Column(length = 36, columnDefinition = "varchar")
    private UUID apiKey;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<BeerOrder> beerOrders;
    
    @Builder
    public Customer(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate, String customerName,
                    UUID apiKey, Set<BeerOrder> beerOrders) {
        super(id, version, createdDate, lastModifiedDate);
        this.customerName = customerName;
        this.apiKey = apiKey;
        this.beerOrders = beerOrders;
    }

}
