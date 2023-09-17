package com.mlorenzo.brewery.bootstrap;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mlorenzo.brewery.domain.Customer;
import com.mlorenzo.brewery.domain.security.Authority;
import com.mlorenzo.brewery.domain.security.Role;
import com.mlorenzo.brewery.domain.security.User;
import com.mlorenzo.brewery.repositories.security.RoleRepository;
import com.mlorenzo.brewery.repositories.security.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
// Esta clase debe ejecutarse antes de la clase "DefaultBreweryLoader" porque necesita de ella la inserción
// de los customers en la base de datos que se realiza aquí
@Order(1)
@Component
public class SecurityDataLoader implements CommandLineRunner {
	private static final String TASTING_ROOM = "Tasting Room";
    public static final String ST_PETE_DISTRIBUTING = "St Pete Distributing";
    public static final String DUNEDIN_DISTRIBUTING = "Dunedin Distributing";
    public static final String KEY_WEST_DISTRIBUTORS = "Key West Distributors";
    public static final String STPETE_USER = "stpete";
    public static final String DUNEDIN_USER = "dunedin";
    public static final String KEYWEST_USER = "keywest";
	
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		loadUserData();
	}
	
	private List<Role> loadRolesData() {
		if(roleRepository.count() == 0) {
			// Beer Authorities
			Authority createBeer = Authority.builder().permission("beer.create").build();
			Authority updateBeer = Authority.builder().permission("beer.update").build();
			Authority readBeer = Authority.builder().permission("beer.read").build();
			Authority deleteBeer = Authority.builder().permission("beer.delete").build();
			// Customer Authorities
			Authority createCustomer = Authority.builder().permission("customer.create").build();
			Authority updateCustomer = Authority.builder().permission("customer.update").build();
			Authority readCustomer = Authority.builder().permission("customer.read").build();
			Authority deleteCustomer = Authority.builder().permission("customer.delete").build();
			// Brewery Authorities
			Authority createBrewery = Authority.builder().permission("brewery.create").build();
			Authority updateBrewery = Authority.builder().permission("brewery.update").build();
			Authority readBrewery = Authority.builder().permission("brewery.read").build();
			Authority deleteBrewery = Authority.builder().permission("brewery.delete").build();
			// Order Authorities(Admin Role)
			Authority createOrderAdmin = Authority.builder().permission("admin.order.create").build();
			Authority updateOrderAdmin = Authority.builder().permission("admin.order.update").build();
			Authority pickupOrderAdmin = Authority.builder().permission("admin.order.pickup").build();
			Authority readOrderAdmin = Authority.builder().permission("admin.order.read").build();
			Authority deleteOrderAdmin = Authority.builder().permission("admin.order.delete").build();
			// Order Authorities(Customer Role)
			Authority createOrderCustomer = Authority.builder().permission("customer.order.create").build();
			Authority updateOrderCustomer = Authority.builder().permission("customer.order.update").build();
			Authority pickupOrderCustomer = Authority.builder().permission("customer.order.pickup").build();
			Authority readOrderCustomer = Authority.builder().permission("customer.order.read").build();
			Authority deleteOrderCustomer = Authority.builder().permission("customer.order.delete").build();
			// Los roles de Spring Security usan el prefijo "ROLE_"
			Role adminRole = Role.builder()
					.name("ROLE_ADMIN")
					.authorities(Set.of(createBeer, updateBeer, readBeer, deleteBeer,
							createCustomer, updateCustomer, readCustomer, deleteCustomer,
							createBrewery, updateBrewery, readBrewery, deleteBrewery,
							createOrderAdmin, updateOrderAdmin, pickupOrderAdmin, readOrderAdmin, deleteOrderAdmin))
					.build();
			Role customerRole = Role.builder()
					.name("ROLE_CUSTOMER")
					.authorities(Set.of(readBeer, readCustomer, readBrewery,
							createOrderCustomer, updateOrderCustomer, pickupOrderCustomer, readOrderCustomer, deleteOrderCustomer))
					.build();
			Role userRole = Role.builder()
					.name("ROLE_USER")
					.authority(readBeer)
					.build();
			return roleRepository.saveAll(Arrays.asList(adminRole,customerRole,userRole));
		}
		return roleRepository.findAll();
	}
	
	private void loadUserData() {
		if(userRepository.count() == 0) {
			List<Role> roles = loadRolesData();
			Role customerRole = findRole(roles, "ROLE_CUSTOMER");
			User admin = User.builder()
						.username("spring")
						.password(passwordEncoder.encode("admin"))
						.role(findRole(roles, "ROLE_ADMIN"))
						.build();
			User user = User.builder()
					.username("user")
					.password(passwordEncoder.encode("password"))
					.role(findRole(roles, "ROLE_USER"))
					.build();
			User tastingRoomUser = User.builder()
            		.username("scott")
					.password(passwordEncoder.encode("tiger"))
					.role(customerRole)
					.customer(Customer.builder()
							.customerName(TASTING_ROOM)
							.apiKey(UUID.randomUUID())
							.build())
            		.build();
			User stPeteUser = User.builder()
            		.username(STPETE_USER)
	                .password(passwordEncoder.encode("password"))
	                .role(customerRole)
	                .customer(Customer.builder()
        	                .customerName(ST_PETE_DISTRIBUTING)
        	                .apiKey(UUID.randomUUID())
        	                .build())
            		.build();
			User dunedinUser = User.builder()
            		.username(DUNEDIN_USER)
	                .password(passwordEncoder.encode("password"))
	                .role(customerRole)
	                .customer(Customer.builder()
	                		.customerName(DUNEDIN_DISTRIBUTING)
	    	                .apiKey(UUID.randomUUID())
	    	                .build())
            		.build();
			User keywestUser = User.builder()
            		.username(KEYWEST_USER)
	                .password(passwordEncoder.encode("password"))
	                .role(customerRole)
	                .customer(Customer.builder()
	                		.customerName(KEY_WEST_DISTRIBUTORS)
	                		.apiKey(UUID.randomUUID())
	                		.build())
            		.build();
			userRepository.saveAll(List.of(admin, user, tastingRoomUser, stPeteUser, dunedinUser, keywestUser));
			log.debug("Users Loaded: " + userRepository.count());
		}
	}
	
	private Role findRole(List<Role> roles, String roleName) {
		return roles.stream()
				.filter(role -> role.getName().equals(roleName))
				.findFirst()
				.orElseThrow();
	}

}
