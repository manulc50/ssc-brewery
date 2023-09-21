package com.mlorenzo.brewery.security;

import java.util.List;

import org.springframework.stereotype.Component;

import com.mlorenzo.brewery.domain.security.User;
import com.mlorenzo.brewery.repositories.security.UserRepository;
import com.warrenstrange.googleauth.ICredentialRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class GoogleCredentialRepository implements ICredentialRepository {
	private final UserRepository userRepository;
	
	@Override
	public String getSecretKey(String userName) {
		return userRepository.findByUsername(userName)
				// Versión simplificada de la expresión "user -> user.getGoogle2faSecret()"
				.map(User::getGoogle2faSecret)
				.orElseThrow();
	}

	@Override
	public void saveUserCredentials(String userName, String secretKey, int validationCode, List<Integer> scratchCodes) {
		User user = userRepository.findByUsername(userName).orElseThrow();
		user.setGoogle2faSecret(secretKey);
		userRepository.save(user);
	}

}
