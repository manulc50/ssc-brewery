package com.mlorenzo.brewery.domain.security;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

// Usamos las anotaciones @Getter y @Setter en lugar de la anotación @Data porque estamos usando la anotación @ManyToMany y Lombok entra en un loop o bucle infinito y falla
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "authorities")
public class Authority extends BaseEntity {
	private String permission;
	
	@ManyToMany(mappedBy = "authorities")
	private Set<Role> roles;
}
