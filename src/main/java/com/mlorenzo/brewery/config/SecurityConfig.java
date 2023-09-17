package com.mlorenzo.brewery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.mlorenzo.brewery.security.SfgPasswordEncoderFactories;
import com.mlorenzo.brewery.security.filters.RestHeaderAuthFilter;
import com.mlorenzo.brewery.security.filters.RestUrlAuthFilter;

// Esta anotación ya incluye la anotación @Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Registramos nuestros filtros "RestHeaderAuthFilter" y "RestUrlAuthFilter", pasándoles previamente el manejador de autenticaciones, en la cadena de filtros de Spring Security antes del filtro "UsernamePasswordAuthenticationFilter"
		http.addFilterBefore(restHeaderAuthFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);
		//http.addFilterBefore(restUrlAuthFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);
		http.authorizeRequests(authorize -> authorize
				// No usar en Producción porque se trata de una base de datos apta únicamente para Desarrollo
				.antMatchers("/h2-console/**").permitAll()
				.antMatchers("/", "/webjars/**", "/resources/**").permitAll()
				// Nota: Esta ruta entra en conflicto con las rutas de la expresión de abajo "/beers/*".
				// Por lo tanto, la ponemos aquí encima para que tenga preferencia.
				.antMatchers("/beers/new").authenticated()
				.antMatchers("/beers/find", "/beers**", "/beers/*").permitAll()
				.antMatchers(HttpMethod.GET, "/api/v1/beers/**").permitAll()
				.anyRequest().authenticated())
			.formLogin()
			.and()
			.httpBasic()
			.and()
			// Para no aplicar protección CSRF a las rutas, o endpoints, correspondientes a la API REST y a la consola H2
			.csrf().ignoringAntMatchers("/api/**", "/h2-console/**");
		// Por defecto, Spring Security no permite, o bloquea, la carga, o el renderizado, de elementos tipo iFrame.
		// La consola de la base de datos H2 utiliza este tipo de elementos para su renderizado y visualización. Por lo tanto, con esta configuración, permitimos que Spring Security no los bloquee.
		http.headers().frameOptions().sameOrigin();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		//return NoOpPasswordEncoder.getInstance();
		//return new LdapShaPasswordEncoder();
		//return new StandardPasswordEncoder(); // SHA-256
		//return new BCryptPasswordEncoder();
		// Usando este método estático, tenemos la opción de usar distintos algoritmos de codificación en las contraseñas de los usuarios
		// Para ello, es necesario indicar en las contraseñas codificadas los prefijos correspondientes a cada algoritmo de codificación utilizado
		// Esta es la configuración por defecto que utiliza Spring Security para la codificación de contraseñas
		//return PasswordEncoderFactories.createDelegatingPasswordEncoder();
		// Usamos nuestra propia factoría de codificadores de contraseñas
		return SfgPasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	// Primera forma para configurar usuarios de Spring Security en memoria
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
			.withUser("spring")
			.password("{bcrypt}$2a$10$QpJ6K30L99D9HT10RV7rYexggaJOR.hUVKrA7//OVuBhvXdBjzy1i") // BCrypt
			.roles("ADMIN")
			.and()
			.withUser("user")
			//.password("password") // NoOp
			//.password("{SSHA}BwqPVocp6l37CCS+NP2CkgDh1AOiIKe0XFYpgw==") // LDAP
			.password("{sha256}80b2aaf0f399c74e5552868ce22400edadc8ee1db4071dd1a9b05a7c0594b57cd0108bc33cb6e37a") // SHA-256
			//.password("$2a$10$dgyBK9eqHDQyA4UttnvhKeRxip6niLwRijvZ43rc2ba8OBixJ3Bea") // BCrypt
			.roles("USER");
		auth.inMemoryAuthentication()
			.withUser("scott")
			.password("{bcrypt10}$2a$10$ULhSPDT3ep4CdB/TU46FzOVpfSicDUhjIxTzFP6Rdp49OyuMqPx7e") // BCrypt-10
			.roles("CUSTOMER");
	}
	
	// Segunda forma para configurar usuarios de Spring Security en memoria
	/*@Bean
	@Override
	protected UserDetailsService userDetailsService() {
		UserDetails admin = User
				.withUsername("spring")
				.password("{noop}admin")
				.roles("ADMIN")
				.build();
		UserDetails user = User
				.withUsername("user")
				.password("{noop}password")
				.roles("USER")
				.build();
		return new InMemoryUserDetailsManager(admin, user);
	}*/
	
	// Método para crear una instancia de nuestro filtro "RestHeaderAuthFilter" y así poder registrarlo en la cadena de filtros de Spring Security
	private RestHeaderAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager) {
		// Nuestro filtro "RestHeaderAuthFilter" se aplicará a todos las rutas que coincidan con la expresión "/api/**". Son las rutas de nuestra API REST
		RestHeaderAuthFilter filter = new RestHeaderAuthFilter(new AntPathRequestMatcher("/api/**"));
		// Establecemos el manejador de autenticaciones, que se pasa como parámetro de entrada a este método, en nuestro filtro
		filter.setAuthenticationManager(authenticationManager);
		return filter;
	}
	
	// Método para crear una instancia de nuestro filtro "RestUrlAuthFilter" y así poder registrarlo en la cadena de filtros de Spring Security
	private RestUrlAuthFilter restUrlAuthFilter(AuthenticationManager authenticationManager) {
		// Nuestro filtro "RestUrlAuthFilter" se aplicará a todos las rutas que coincidan con la expresión "/api/**". Son las rutas de nuestra API REST
		RestUrlAuthFilter filter = new RestUrlAuthFilter(new AntPathRequestMatcher("/api/**"));
		// Establecemos el manejador de autenticaciones, que se pasa como parámetro de entrada a este método, en nuestro filtro
		filter.setAuthenticationManager(authenticationManager);
		return filter;
	}
	
}
