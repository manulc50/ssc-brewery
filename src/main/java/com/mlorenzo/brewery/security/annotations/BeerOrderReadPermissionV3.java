package com.mlorenzo.brewery.security.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import org.springframework.security.access.prepost.PostAuthorize;

@Retention(RUNTIME)
// Anotación personalizada que contiene la anotación de Spring Security @PreAuthorize
@BeerOrderReadPermissionV2
@PostAuthorize("hasAuthority('admin.order.read') or returnObject.customerId == principal.userEntity.customer.id")
public @interface BeerOrderReadPermissionV3 {
}
