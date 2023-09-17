package com.mlorenzo.brewery.security.filters;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.RequestMatcher;

// Custom Authentication Filter

// Este filtro personalizado simula un proceso de autenticación, mediante el paso de una clave y una contraseña en las cabeceras de las peticiones http, que se realizaba
// en las aplicaciones o sistemas de tipo API REST antiguos.
// NOTA: Hoy en día, no se lleva a cabo este procedimiento de autenticación.

public class RestHeaderAuthFilter extends AbstractRestAuthFilter {

	public RestHeaderAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
		super(requiresAuthenticationRequestMatcher);
	}

	// Método que obtiene el username de la cabecera "Api-Key" de la petición http
	@Override
	protected String getUsername(HttpServletRequest request) {
		return request.getHeader("Api-Key");
	}

	// Método que obtiene la contraseña de la cabecera "Api-Secret" de la petición http
	@Override
	protected String getPassword(HttpServletRequest request) {
		return request.getHeader("Api-Secret");
	}
	

}
