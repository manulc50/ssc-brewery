package com.mlorenzo.brewery;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.DigestUtils;

// Deshabilitamos los tests de esta clase porque realmente no realizan ninguna comprobación

// Anotación para que los tests de esta clase sean ignorados y no se ejecuten
@Disabled
public class PasswordEncodingTests {
	private static final String PASSWORD = "password";
	
	// MD5 Hash and Password Salt
	@Test
	void hashingExample() {
		System.out.println("--- MD5 Hash and Password Salt ---");
		System.out.println("Codificación para \"password\": " + DigestUtils.md5DigestAsHex(PASSWORD.getBytes()));
		System.out.println("Segunda codificación para \"password\"(identica a la anterior): " + DigestUtils.md5DigestAsHex(PASSWORD.getBytes()));
		String salted = PASSWORD + "ThisIsMySALTVALUE";
		System.out.println("Tercera codificación para \"password\"(con salt): " + DigestUtils.md5DigestAsHex(salted.getBytes()));
	}
	
	// NoOp Password Encoder
	@Test
	void testNoOp() {
		System.out.println("--- NoOp Password Encoder  ---");
		// Deprecated porque no se recomienda su uso actualmente
		PasswordEncoder noOp = org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
		System.out.println("Codificación para \"password\": " + noOp.encode(PASSWORD));
		System.out.println("Codificación para \"admin\": " + noOp.encode("admin"));
		System.out.println("Codificación para \"tiger\": " + noOp.encode("tiger"));
	}
	
	// LDAP Password Encoder
	@Test
	void testLdap() {
		System.out.println("--- LDAP Password Encoder ---");
		// Deprecated porque no se recomienda su uso actualmente
		PasswordEncoder ldap = new org.springframework.security.crypto.password.LdapShaPasswordEncoder();
		System.out.println("Codificación para \"password\": " + ldap.encode(PASSWORD));
		System.out.println("Segunda codificación para \"password\": " + ldap.encode(PASSWORD));
		System.out.println("Codificación para \"tiger\": " + ldap.encode("tiger"));
		System.out.println("Codificación para \"admin\": " + ldap.encode("admin"));
		String encodedPwd = ldap.encode(PASSWORD);
		assertTrue(ldap.matches(PASSWORD,encodedPwd));
	}
	
	// SHA-256 Password Encoder
	@Test
	void testSha256() {
		System.out.println("--- SHA-256 Password Encoder ---");
		// Deprecated porque no se recomienda su uso actualmente
		PasswordEncoder sha256 = new org.springframework.security.crypto.password.StandardPasswordEncoder();
		System.out.println("Codificación para \"password\": " + sha256.encode(PASSWORD));
		System.out.println("Segunda codificación para \"password\": " + sha256.encode(PASSWORD));
		System.out.println("Codificación para \"tiger\": " + sha256.encode("tiger"));
		System.out.println("Codificación para \"admin\": " + sha256.encode("admin"));
	}
	
	// BCrypt Password Encoder - Recomendado
	@Test
	void testBcrypt() {
		System.out.println("--- BCrypt Password Encoder ---");
		PasswordEncoder bcrypt = new BCryptPasswordEncoder();
		System.out.println("Codificación para \"password\": " + bcrypt.encode(PASSWORD));
		System.out.println("Segunda codificación para \"password\": " + bcrypt.encode(PASSWORD));
		System.out.println("Codificación para \"tiger\": " + bcrypt.encode("tiger"));
		System.out.println("Codificación para \"admin\": " + bcrypt.encode("admin"));
	}
	
	// BCrypt-10 Password Encoder - Recomendado
	@Test
	void testBcrypt10() {
		System.out.println("--- BCrypt-10 Password Encoder ---");
		PasswordEncoder bcrypt10 = new BCryptPasswordEncoder(10);
		System.out.println("Codificación para \"password\": " + bcrypt10.encode(PASSWORD));
		System.out.println("Segunda codificación para \"password\": " + bcrypt10.encode(PASSWORD));
		System.out.println("Codificación para \"tiger\": " + bcrypt10.encode("tiger"));
		System.out.println("Codificación para \"admin\": " + bcrypt10.encode("admin"));
	}

}
