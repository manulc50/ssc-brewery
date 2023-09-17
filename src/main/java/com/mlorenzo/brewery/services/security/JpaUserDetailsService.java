package com.mlorenzo.brewery.services.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mlorenzo.brewery.domain.security.User;
import com.mlorenzo.brewery.repositories.security.UserRepository;
import com.mlorenzo.brewery.security.UserPrincipal;

import lombok.extern.slf4j.Slf4j;

// Esta clase implementa la interfaz "UserDetailsService" de Spring Security para indicarle de dónde tiene que obtener la información de los usuarios junto con sus roles

@Slf4j
@Service
public class JpaUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("Getting User info via JPA");
		// Obtenemos el usuario de la base de datos a partir de su Username a través de nuestro repositorio "UserRepository"
		// Si no se localiza el usuario en la base de datos, lanzamos la excepción "UsernameNotFoundException"
		User userEntity = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User name: " + username + " not found"));
		// En vez de devolver directamente un usuario de Spring Security, devolvemos un usuario personalizado, que implementa la interfaz UserDetails de Spring Security,
		// para tener la posibilidad de manejar las autorizaciones de ejecución de los métodos Handler de los controladores mediante el id del usuario autenticado. Si devolvemos
		// directamente un usuario de Spring Security, no es posible pasarle el id del usuario autenticado y, por lo tanto, no podemos realizar autorizaciones medinate ese id.
		return new UserPrincipal(userEntity);
	}

}
