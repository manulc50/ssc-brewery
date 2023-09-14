package com.mlorenzo.brewery.services.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mlorenzo.brewery.domain.security.Role;
import com.mlorenzo.brewery.domain.security.User;
import com.mlorenzo.brewery.repositories.security.UserRepository;

import lombok.extern.slf4j.Slf4j;

// Esta clase implementa la interfaz "UserDetailsService" de Spring Security para indicarle de dónde tiene que obtener la información de los usuarios junto con sus roles

@Slf4j
@Service
public class JpaUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;

	// Implementamos este método de la interfaz "UserDetailsService" de Spring Security para indicar la implementación sobre dónde y cómo tiene que recuperar el usuario a través de su Username
	// Nota: La anotación @Transactional es necesaria porque los roles de los usuarios se cargan desde la base de datos de forma perezosa y se utilizan fuera del contexto por defecto de persistencia, es decir, fuera
	// del método "findByUsername" del repositorio. Otra opción sería establecer una carga ansiosa, o EAGER, en los roles de los usuarios
	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("Getting User info via JPA");
		// Obtenemos el usuario de la base de datos a partir de su Username a través de nuestro repositorio "UserRepository"
		// Si no se localiza el usuario en la base de datos, lanzamos la excepción "UsernameNotFoundException"
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User name: " + username + " not found"));
		// Creamos y devolvemos un usuario de Spring Security a partir de la información y los datos del usuario recuperado de la base de datoa a través de nuestro repositorio "UserRepository"
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.getEnabled(),
				user.getAccountNonExpired(), user.getCredentialsNonExpired(), user.getAccountNonLocked(),
				convertToSpringAuthorities(user.getRoles()));
	}
	
	// Método que convierte los roles del usuario en authorities de Spring Security(SimpleGrantedAuthority)
	private Collection<? extends GrantedAuthority> convertToSpringAuthorities(Set<Role> roles){
		if(roles != null && roles.size() > 0) {
			return roles.stream()
					// Forma simplificada de la expresión lambda "role -> roles.getName()"
					.map(Role::getName)
					// Forma simplificada de la expresión lambda "role -> new SimpleGrantedAuthority(role)"
					.map(SimpleGrantedAuthority::new)
					.collect(Collectors.toList());
		}
		else
			return new HashSet<>();
	}

}
