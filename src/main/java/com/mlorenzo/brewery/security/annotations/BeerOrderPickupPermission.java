package com.mlorenzo.brewery.security.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('admin.order.pickup') or (hasAuthority('customer.order.pickup') and principal.userEntity.customer.id == #customerId)")
public @interface BeerOrderPickupPermission {
}
