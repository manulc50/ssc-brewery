package com.mlorenzo.brewery.config;

import javax.sql.DataSource;

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
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.mlorenzo.brewery.security.SfgPasswordEncoderFactories;
import com.mlorenzo.brewery.security.filters.Google2faFilter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.ICredentialRepository;

import lombok.RequiredArgsConstructor;

// Nota: En Spring Security, los usurios que no requieren autenticación, es decir, aquellos que acceden a rutas públicas de la aplicación, se autentican en el contexto de Spring Security
// con una autenticación "especial" que es de tipo AnonymousAuthenticationToken

//securedEnabled = true -> Habilita el uso de la anotación de seguridad @Secured(más antigua y menos potente que las anotaciones de abajo @PreAuthorize y @PostAuthorize)
//prePostEnabled = true -> Habilita el uso de las anotaciones de seguridad @PreAuthorize y @PostAuthorize
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {
	
	@Bean
	public PersistentTokenRepository persistentTokenRepository(DataSource dataSource) {
		JdbcTokenRepositoryImpl tokenRepositoryImpl = new JdbcTokenRepositoryImpl();
		tokenRepositoryImpl.setDataSource(dataSource);
		return tokenRepositoryImpl;
	}
	
	@Bean
    public GoogleAuthenticator googleAuthenticator(ICredentialRepository credentialRepository){
        GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder configBuilder
                = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder();
        // Si queremos personalizar la configuración por defecto de Google Authenticator
        /*configBuilder.setTimeStepSizeInMillis(TimeUnit.SECONDS.toMillis(60))
        			 .setWindowSize(10)
        			 .setNumberOfScratchCodes(0);*/
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator(configBuilder.build());
        googleAuthenticator.setCredentialRepository(credentialRepository);
        return googleAuthenticator;
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
				.and().csrf().disable()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and().cors();
		}

	}
	
	@Order(2)
	@RequiredArgsConstructor
	// Esta anotación ya incluye la anotación @Configuration
	@EnableWebSecurity
	static class MvcSecurity extends WebSecurityConfigurerAdapter {
		private final UserDetailsService userDetailsService;
		private final PersistentTokenRepository tokenRepository;
		
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.addFilterBefore(new Google2faFilter(), SessionManagementFilter.class);
			http.authorizeRequests(authorize -> authorize
					// No usar en Producción porque se trata de una base de datos apta únicamente para Desarrollo
					.antMatchers("/h2-console/**").permitAll()
					.antMatchers("/", "/webjars/**", "/resources/**").permitAll()
					// Opcional porque el resto de rutas de la aplicación están protegidas por defecto y requiren autenticación
					.anyRequest().authenticated())
				.formLogin(loginConfigurer -> loginConfigurer
						// Opcional porque, en este caso, la url "/login" es la url por defecto si no se indica otra distinta
						.loginProcessingUrl("/login")
						.loginPage("/"))
				.logout(logoutConfigurer -> logoutConfigurer
						.logoutRequestMatcher(new AntPathRequestMatcher("/logout", HttpMethod.GET.name()))
						// Pasamos el parámetro "logout" a la url "/" en caso de ser exitoso el proceso de logout
						.logoutSuccessUrl("/?logout"))
				// Configura Persistent Token Remember Me
				.rememberMe()
					.tokenRepository(tokenRepository)
					.userDetailsService(userDetailsService)
				.and().httpBasic()
				// Para no aplicar protección CSRF a las rutas, o endpoints, correspondientes a la consola H2
				.and().csrf().ignoringAntMatchers("/h2-console/**");
			// Por defecto, Spring Security no permite, o bloquea, la carga, o el renderizado, de elementos tipo iFrame.
			// La consola de la base de datos H2 utiliza este tipo de elementos para su renderizado y visualización. Por lo tanto, con esta configuración, permitimos que Spring Security no los bloquee.
			http.headers().frameOptions().sameOrigin();
		}
		
	}
}
