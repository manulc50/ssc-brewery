package com.mlorenzo.brewery.security.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('admin.order.create') or (hasAuthority('customer.order.create') and principal.userEntity.customer.id == #customerId)")
public @interface BeerOrderCreatePermission {
}
