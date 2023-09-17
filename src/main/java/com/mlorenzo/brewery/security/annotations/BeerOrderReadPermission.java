package com.mlorenzo.brewery.security.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('admin.order.read') or (hasAuthority('customer.order.read') and principal.userEntity.customer.id == #customerId)")
public @interface BeerOrderReadPermission {
}
