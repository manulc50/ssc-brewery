package com.mlorenzo.brewery.web.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.mlorenzo.brewery.domain.security.User;
import com.mlorenzo.brewery.repositories.security.UserRepository;
import com.mlorenzo.brewery.security.UserPrincipal;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@SessionAttributes("googleUrl")
@Controller
@RequestMapping("/users")
public class UserController {
	private final GoogleAuthenticator googleAuthenticator;
	private final UserRepository userRepository;
	
	@GetMapping("/register2fa")
	public String register2fa(Model model, @AuthenticationPrincipal UserPrincipal principal) {
		User user = principal.getUserEntity();
		// El método "createCredentials" invocará por debajo al método "saveUserCredentials" de nuestra implementación
		// de la interfaz "ICredentialRepository" que se encuentra en la clase "GoogleCredentialService"
		String googleUrl = GoogleAuthenticatorQRGenerator.getOtpAuthURL("SFG", user.getUsername(),
				googleAuthenticator.createCredentials(user.getUsername()));
		log.debug("Google QR URL: {}", googleUrl);
		model.addAttribute("googleUrl", googleUrl);
		return "users/register2fa";
	}
	
	@PostMapping("/register2fa")
	public String proccessRegister2faForm(Integer verifyCode, SessionStatus status, @AuthenticationPrincipal UserPrincipal principal) {
		User user = principal.getUserEntity();
		log.debug("Enterd Code is: {}", verifyCode);
		if(googleAuthenticator.authorizeUser(user.getUsername(), verifyCode)) {
			User foundUser = userRepository.findById(user.getId()).orElseThrow();
			foundUser.setUseGoogle2fa(true);
			userRepository.save(foundUser);
			status.setComplete();
			return "index";
		}
		return "users/register2fa";
	}
	
	@GetMapping("/verify2fa")
	public String verify2fa() {
		return "users/verify2fa";
	}
	
	@PostMapping("/verify2fa")
	public String proccessVerify2faForm(Integer verifyCode, Authentication authentication) {
		log.debug("Enterd Code is: {}", verifyCode);
		User user = ((UserPrincipal)authentication.getPrincipal()).getUserEntity();
		if(googleAuthenticator.authorizeUser(user.getUsername(), verifyCode)) {
			user.setGoogle2faRequired(false);
			return "index";
		}
		return "users/verify2fa";
	}
	
}
