package com.mlorenzo.brewery.security.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mlorenzo.brewery.domain.security.User;
import com.mlorenzo.brewery.security.UserPrincipal;

import lombok.extern.slf4j.Slf4j;

// Nota: Como este filtro se registra en el contexto de Spring Security justo antes del filtro "SessionManagementFilter", nos aseguramos que siempre va a existir un objeto "Authentication",
// ya sea de tipo "UsernamePasswordAuthenticationToken" o de tipo "AnonymousAuthenticationToken"(usuarios públicos), cada vez que se ejecute este filtro.

@Slf4j
public class Google2faFilter extends OncePerRequestFilter {
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// Comprobamos que la autenticación es de tipo "UsernamePasswordAuthenticationToken" para descartar al usuario público con autenticación "especial" de tipo "AnonymousAuthenticationToken"
		if(authentication.isAuthenticated() && authentication instanceof UsernamePasswordAuthenticationToken) {
			User user = ((UserPrincipal)authentication.getPrincipal()).getUserEntity();
			if(user.getUseGoogle2fa() && user.getGoogle2faRequired()) {
				log.debug("Forward to 2fa");
				request.getRequestDispatcher("/users/verify2fa").forward(request, response);
				return;
			}
		}
		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String requestURI = request.getRequestURI();
		return requestURI.startsWith("/resources") ||
				requestURI.startsWith("/webjars") ||
				requestURI.equals("/users/verify2fa");
	}

}
