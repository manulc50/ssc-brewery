package com.mlorenzo.brewery.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

// Custom Authentication Filter

@Slf4j
public abstract class AbstractRestAuthFilter extends AbstractAuthenticationProcessingFilter {
	
	protected AbstractRestAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
		super(requiresAuthenticationRequestMatcher);
	}

	// Sobrescribimos este método de la clase abstracta AbstractAuthenticationProcessingFilter para implementar nuestro filtro personalizado
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		if (log.isDebugEnabled())
			log.debug("Request is to process authentication");
		try {
			// Realiza la autenticación
			// Si la autenticación falla, lanza una excepción que capturamos
			Authentication authResult = attemptAuthentication(request, response);
			// Si el resultado de la autenticación no es nulo y no se ha lanzado ninguna exceptión por autenticación fallida, significa que la autenticación es correcta e invocamos al método sobrescrito por nosotros "successfulAuthentication"
			if(authResult != null)
				successfulAuthentication(request, response, chain, authResult);
			// En caso contrario, continua con el siguiente filtro de la cadena de filtros
			else
				chain.doFilter(request,response);
		}
		// Capturamos la excepción asociada a la autenticación fallida e invocamos al método sobrescrito por nosotros "unsuccessfulAuthentication"
		catch(AuthenticationException e) {
			log.error("Authentication failed", e);
			unsuccessfulAuthentication(request, response, e);
		}
	}
	
	// Sobrescribimos este método de la clase abstracta "AbstractAuthenticationProcessingFilter" para implementar nuestro filtro personalizado
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		// Obtenemos el username y el password de la petición http
		String username = getUsername(request);
		String password = getPassword(request);
		// Si por algún motivo el username o el passowrd no vienen, los inicializamos a una cadena de texto vacía
		if(username == null)
			username = "";
		if(password == null)
			password = "";
		log.debug("Authenticating User: " + username);
		// Creamos un token de autenticación, generado y proporcionado por Spring Security, a partir del username y el password obtenidos previamente
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
		// Si el username no está vacío, devolvemos el manejador de autenticaciones que realizará el proceso de autenticación teniendo en cuenta el token generado en el punto anterior
	    if(!StringUtils.isEmpty(username))
	    	return this.getAuthenticationManager().authenticate(token);
	    // En caso contrario, devolvemos null para no realizar el proceso de autenticación
	    else
	    	return null;
	}
	
	// Sobrescribimos este método de la clase abstracta AbstractAuthenticationProcessingFilter para indicar nuestra implementación personalizada sobre las autenticaciones con éxito o satisfactorias
	@Override
	protected void successfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain, Authentication authResult)
			throws IOException, ServletException {
		if (log.isDebugEnabled())
			log.debug("Authentication success. Updating SecurityContextHolder to contain: " + authResult);
		// Establecemos el resultado de la autenticación(autenticación con éxtio) dentro del contexto de seguridad
		SecurityContextHolder.getContext().setAuthentication(authResult);
;
	}
	
	// Sobrescribimos este método de la clase abstracta AbstractAuthenticationProcessingFilter para indicar nuestra implementación personalizada sobre las autenticaciones fallidas
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException failed)
			throws IOException, ServletException {
		// Como la autenticación ha fallado, limpiamos el contexto de Spring Security porque ya ha finalizado el proceso de autenticación
		SecurityContextHolder.clearContext();
		if (log.isDebugEnabled()) {
			log.debug("Authentication request failed: " + failed.toString(), failed);
			log.debug("Updated SecurityContextHolder to contain null Authentication");
		}
		// Establecemos la respuesta de la petición http con el código de error UNAUTHORIZED(401) y el texto del error asociado a dicho código para enviársela al cliente
		response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
	}
	
	// Método que obtiene el username de la petición http
	protected abstract String getUsername(HttpServletRequest request);
	
	// Método que obtiene la contraseña de la petición http
	protected abstract String getPassword(HttpServletRequest request);
}
