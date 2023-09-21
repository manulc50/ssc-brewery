package com.mlorenzo.brewery.security.listeners;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import com.mlorenzo.brewery.domain.security.LoginFailure;
import com.mlorenzo.brewery.domain.security.User;
import com.mlorenzo.brewery.repositories.security.LoginFailureRepository;
import com.mlorenzo.brewery.repositories.security.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Componente de Spring que escucha los eventos de tipo "AuthenticationFailureBadCredentialsEvent" de Spring Security que se producen
// cada vez que falla el proceso de login

@AllArgsConstructor
@Slf4j
@Component
public class AuthenticationFailureListener {
	private final LoginFailureRepository loginFailureRepository;
	private final UserRepository userRepository;
	
	// Opcional indicar la clase del evento en esta anotación @EventListener porque utiliza el tipo del argumento de entrada del método
	// para las escuchas de eventos de un determinado tipo
	@EventListener
	public void listen(AuthenticationFailureBadCredentialsEvent event) {
		log.debug("Login Failure");
		if(event.getSource() instanceof UsernamePasswordAuthenticationToken) {
			LoginFailure.LoginFailureBuilder<?, ?> builder = LoginFailure.builder();
			UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken)event.getSource();
			if(token.getPrincipal() instanceof String) {
				String username = (String)token.getPrincipal();
				builder.username(username);
				// Versión simplificada de la expresión "user -> builder.user(user)"
				userRepository.findByUsername(username).ifPresent(builder::user);
				log.debug("Attempted Username: {}", username);
			}
			if(token.getDetails() instanceof WebAuthenticationDetails) {
				WebAuthenticationDetails details = (WebAuthenticationDetails)token.getDetails();
				builder.sourceIp(details.getRemoteAddress());
				log.debug("Source IP: {}", details.getRemoteAddress());
			}
			LoginFailure savedLoginFailure = loginFailureRepository.save(builder.build());
			log.debug("Login Failure saved with Id: {}", savedLoginFailure.getId());
			if(savedLoginFailure.getUser() != null)
				checkLockUserAccount(savedLoginFailure.getUser());
		}
	}
	
	private void checkLockUserAccount(User user) {
		List<LoginFailure> loginFailures = loginFailureRepository
				.findByUserAndCreatedDateIsAfter(user, Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
		if(loginFailures.size() > 3) {
			log.debug("Locking User Account...");
			user.setAccountNonLocked(false);
			userRepository.save(user);
		}
	}

}
