package com.mlorenzo.brewery.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Esta clase representa nuestra implementación personalizada de nuestra factoría de codificadores de contraseñas(Custom Delegating Password Encoder)

// Nota: El método BCrypt tiene un parámetro numérico entero, llamado en Ingles "strength", que se corresponde con la fuerza de encriptación o codificación.
// Si aumenta el valor de este parámetro, el coste computacional para generar la encriptación, o codificación, se vuelve exponencialmente más alto pudiendo llegar a afectar al rendimiento de la aplicación.
// Como tarda más tiempo en generar la encriptación, aumentar el parámetro "strength" puede utilizarse para disuadir ataques de fuerza bruta al reducir el número de cálculos que se pueden realizar en un período de tiempo determinado.

public class SfgPasswordEncoderFactories {
	
	// Creamos el constructor de esta clase privado para que no se pueda instanciar desde fuera
	private SfgPasswordEncoderFactories() {
		
	}
	
	public static PasswordEncoder createDelegatingPasswordEncoder() {
		// Indicamos a Spring Security que utilice por defecto el algoritmo de encriptación BCrypt-10 
		String encodingId = "bcrypt10";
		Map<String, PasswordEncoder> encoders = new HashMap<>();
		encoders.put(encodingId, new BCryptPasswordEncoder(10));
		encoders.put("bcrypt", new BCryptPasswordEncoder());
		encoders.put("ldap", new org.springframework.security.crypto.password.LdapShaPasswordEncoder());
		encoders.put("noop", org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance());
		encoders.put("sha256", new org.springframework.security.crypto.password.StandardPasswordEncoder());
		return new DelegatingPasswordEncoder(encodingId, encoders);
	}

}
