package com.mlorenzo.brewery.security.filters;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.RequestMatcher;

// Custom Authentication Filter

// Este filtro personalizado simula un proceso de autenticación mediante el paso de una clave y una contraseña en la url de las peticiones http

public class RestUrlAuthFilter extends AbstractRestAuthFilter{
	
	public RestUrlAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
		super(requiresAuthenticationRequestMatcher);
	}

	// Método que obtiene el username del parámetro "apiKey" de la url de la petición http
	@Override
	protected String getUsername(HttpServletRequest request) {
		return request.getParameter("apiKey");
	}

	// Método que obtiene la contraseña del parámetro "apiSecret" de la url de la petición http
	@Override
	protected String getPassword(HttpServletRequest request) {
		return request.getParameter("apiSecret");
	}

}
