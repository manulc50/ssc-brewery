package com.mlorenzo.brewery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.mlorenzo.brewery.security.SfgPasswordEncoderFactories;

import lombok.RequiredArgsConstructor;

//securedEnabled = true -> Habilita el uso de la anotación de seguridad @Secured(más antigua y menos potente que las anotaciones de abajo @PreAuthorize y @PostAuthorize)
//prePostEnabled = true -> Habilita el uso de las anotaciones de seguridad @PreAuthorize y @PostAuthorize
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

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
	
	// Se comenta porque en nuestro caso es opcional, es decir, si sólo tenemos una única implementación de la interfaz "UserDetailsService" de Spring Security, no es necesario
	// indicarla en el método de configuración "configure". Sin embargo, si tenemos más de una implementación, entonces sí es necesario indicar la implementación
	// que queremos utilizar
	/*
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(jpaUserDetailsService).passwordEncoder(passwordEncoder());
	}*/
	
	// Nota: Como en esta aplicación tenemos una parte que es una API REST y otra parte que es MVC, creamos 2 filtros de seguridad para configurar cada parte por separado
	
	@Order(1)
	// Esta anotación ya incluye la anotación @Configuration
	@EnableWebSecurity
	static class ApiSecurity extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.antMatcher("/api/**")
				// Opcional porque, por defecto, todas las rutas de la aplicación están protegidas y requiren autenticación
				.authorizeRequests(authorize -> authorize.anyRequest().authenticated())
				.httpBasic()
				.and()
				.csrf().disable()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		}

	}
	
	@Order(2)
	@RequiredArgsConstructor
	// Esta anotación ya incluye la anotación @Configuration
	@EnableWebSecurity
	static class MvcSecurity extends WebSecurityConfigurerAdapter {
		private final UserDetailsService userDetailsService;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests()
					// No usar en Producción porque se trata de una base de datos apta únicamente para Desarrollo
					.antMatchers("/h2-console/**").permitAll()
					.antMatchers("/", "/webjars/**", "/resources/**").permitAll()
					// Opcional porque el resto de rutas de la aplicación están protegidas por defecto y requiren autenticación
					.anyRequest().authenticated()
				.and().formLogin(loginConfigurer -> loginConfigurer
						.loginProcessingUrl("/login")
						.loginPage("/")
						.defaultSuccessUrl("/")
						// Pasamos el parámetro "error" a la url "/" en caso de fallar el proceso de login
						.failureUrl("/?error"))
				.logout(logoutConfigurer -> logoutConfigurer
						.logoutRequestMatcher(new AntPathRequestMatcher("/logout", HttpMethod.GET.name()))
						// Pasamos el parámetro "logout" a la url "/" en caso de ser exitoso el proceso de logout
						.logoutSuccessUrl("/?logout"))
				// Configura Simple Hash-Based Token Remember Me
				.rememberMe().userDetailsService(userDetailsService)
				.and().httpBasic()
				// Para no aplicar protección CSRF a las rutas, o endpoints, correspondientes a la consola H2
				.and().csrf().ignoringAntMatchers("/h2-console/**");
			// Por defecto, Spring Security no permite, o bloquea, la carga, o el renderizado, de elementos tipo iFrame.
			// La consola de la base de datos H2 utiliza este tipo de elementos para su renderizado y visualización. Por lo tanto, con esta configuración, permitimos que Spring Security no los bloquee.
			http.headers().frameOptions().sameOrigin();
		}
		
	}
}
