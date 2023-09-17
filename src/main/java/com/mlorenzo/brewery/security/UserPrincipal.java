package com.mlorenzo.brewery.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mlorenzo.brewery.domain.security.Authority;
import com.mlorenzo.brewery.domain.security.Role;
import com.mlorenzo.brewery.domain.security.User;

public class UserPrincipal implements UserDetails {
	private static final long serialVersionUID = -6800675148373200991L;
	
	private User userEntity;
	
	public UserPrincipal(User userEntity) {
		this.userEntity = userEntity;
	}
	
	public User getUserEntity() {
		return userEntity;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<Role> roles = userEntity.getRoles();
		if(roles != null && roles.size() > 0) {
			// Convertimos los roles del usuario en authorities de Spring Security(SimpleGrantedAuthority)
			return roles.stream()
					.flatMap(role -> role.getAuthorities().stream())
					// Forma simplificada de la expresión lambda "auth -> auth.getPermission()"
					.map(Authority::getPermission)
					// Forma simplificada de la expresión lambda "permission -> new SimpleGrantedAuthority(permission)"
					.map(SimpleGrantedAuthority::new)
					.collect(Collectors.toSet());
		}
		else
			return new HashSet<>();
	}

	@Override
	public String getPassword() {
		return userEntity.getPassword();
	}

	@Override
	public String getUsername() {
		return userEntity.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return userEntity.getAccountNonExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		return userEntity.getAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return userEntity.getCredentialsNonExpired();
	}

	@Override
	public boolean isEnabled() {
		return userEntity.getEnabled();
	}

}
