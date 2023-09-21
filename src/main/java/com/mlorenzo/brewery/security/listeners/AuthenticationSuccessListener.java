package com.mlorenzo.brewery.security.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import com.mlorenzo.brewery.domain.security.LoginSuccess;
import com.mlorenzo.brewery.domain.security.User;
import com.mlorenzo.brewery.repositories.security.LoginSuccessRepository;
import com.mlorenzo.brewery.security.UserPrincipal;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Componente de Spring que escucha los eventos de tipo "AuthenticationSuccessEvent" de Spring Security que se producen
// cada vez que el proceso de login es satisfactorio

@AllArgsConstructor
@Slf4j
@Component
public class AuthenticationSuccessListener {
	private final LoginSuccessRepository loginSuccessRepository;
	
	// Opcional indicar la clase del evento en esta anotación @EventListener porque utiliza el tipo del argumento de entrada del método
	// para las escuchas de eventos de un determinado tipo
	@EventListener(AuthenticationSuccessEvent.class)
	public void listen(AuthenticationSuccessEvent event) {
		log.debug("User Logged In Ok");
		if(event.getSource() instanceof UsernamePasswordAuthenticationToken) {
			LoginSuccess.LoginSuccessBuilder<?, ?> builder = LoginSuccess.builder();
			UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken)event.getSource();
			if(authentication.getPrincipal() instanceof UserPrincipal) {
				User user = ((UserPrincipal)authentication.getPrincipal()).getUserEntity();
				builder.user(user);
				if(user.getUseGoogle2fa())
					user.setGoogle2faRequired(true);
				log.debug("Username logged in: {}", user.getUsername());
			}
			if(authentication.getDetails() instanceof WebAuthenticationDetails) {
				WebAuthenticationDetails details = (WebAuthenticationDetails)authentication.getDetails();
				builder.sourceIp(details.getRemoteAddress());
				log.debug("Source IP: {}", details.getRemoteAddress());
			}
			LoginSuccess savedLoginSuccess = loginSuccessRepository.save(builder.build());
			log.debug("Login Success saved with Id: {}", savedLoginSuccess.getId());
		}
	}

}
