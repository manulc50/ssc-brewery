package com.mlorenzo.brewery.domain.security;

import java.util.Set;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {
	private String name;
	
	@ManyToMany(mappedBy = "roles")
	private Set<User> users;
	
	// Como esta propiedad es una colección, podemos usar la anotaión @Singular de Lombok para añadir al patrón Builder la posibilidad de agregar elementos de uno en uno a la colección.
	// Si no usamos esta anotación, sólo podemos establecer una colección entera mediante el patrón Builder.
	@Singular
	@ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	@JoinTable(name = "roles_authorities",
			joinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id" )},
			inverseJoinColumns = {@JoinColumn(name = "authority_id", referencedColumnName = "id")})
	private Set<Authority> authorities;
}
