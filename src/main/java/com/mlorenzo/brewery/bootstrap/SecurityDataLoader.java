package com.mlorenzo.brewery.bootstrap;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mlorenzo.brewery.domain.security.Role;
import com.mlorenzo.brewery.domain.security.User;
import com.mlorenzo.brewery.repositories.security.RoleRepository;
import com.mlorenzo.brewery.repositories.security.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class SecurityDataLoader implements CommandLineRunner{
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		loadRolesData();	
		loadUserData();
	}
	
	private void loadRolesData() {
		if(roleRepository.count() == 0) {
			// Los roles de Spring Security usan el prefijo "ROLE_"
			Role adminRole = roleRepository.save(Role.builder().name("ROLE_ADMIN").build());
			Role customerRole = roleRepository.save(Role.builder().name("ROLE_CUSTOMER").build());
			Role userRole = roleRepository.save(Role.builder().name("ROLE_USER").build());
			roleRepository.saveAll(Arrays.asList(adminRole,customerRole,userRole));
		}
	}
	
	private void loadUserData() {
		if(userRepository.count() == 0) {
			List<Role> roles = roleRepository.findAll();
			User admin = User.builder()
						.username("spring")
						.password(passwordEncoder.encode("admin"))
						.role(getRole(roles, "ROLE_ADMIN"))
						.build();
			User user = User.builder()
					.username("user")
					.password(passwordEncoder.encode("password"))
					.role(getRole(roles, "ROLE_USER"))
					.build();
			User customer = User.builder()
					.username("scott")
					.password(passwordEncoder.encode("tiger"))
					.role(getRole(roles, "ROLE_CUSTOMER"))
					.build();
			userRepository.saveAll(List.of(admin, user, customer));
			log.debug("Users Loaded: " + userRepository.count());
		}
	}
	
	private Role getRole(List<Role> roles, String roleName) {
		return roles.stream()
				.filter(role -> role.getName().equals(roleName))
				.findFirst()
				.orElse(null);
	}

}
