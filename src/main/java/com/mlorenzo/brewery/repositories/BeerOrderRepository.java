package com.mlorenzo.brewery.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mlorenzo.brewery.domain.BeerOrder;
import com.mlorenzo.brewery.domain.Customer;
import com.mlorenzo.brewery.domain.OrderStatusEnum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeerOrderRepository  extends JpaRepository<BeerOrder, UUID> {
    Page<BeerOrder> findAllByCustomer(Customer customer, Pageable pageable);
    List<BeerOrder> findAllByOrderStatus(OrderStatusEnum orderStatusEnum);
    
    // Consulta JPQL usando Spring Security mediante expresiones SpEL 
    @Query("Select o from BeerOrder o where o.id=?1 and " +
           "(:#{hasAuthority('admin.order.read')}=true or o.customer.id=?#{principal.userEntity.customer?.id})")
    Optional<BeerOrder> findByIdSecure(UUID id);
}
