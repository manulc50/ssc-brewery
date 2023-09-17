package com.mlorenzo.brewery.security.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RUNTIME)
@PreAuthorize("hasAnyAuthority('admin.order.read','customer.order.read')")
public @interface BeerOrderReadPermissionV2 {
}
