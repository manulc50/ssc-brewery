package com.mlorenzo.brewery.domain.security;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Usamos las anotaciones @Getter y @Setter en lugar de la anotación @Data porque estamos usando la anotación @ManyToMany y Lombok entra en un loop o bucle infinito y falla
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "authorities")
public class Authority {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	private String permission;
	
	@ManyToMany(mappedBy = "authorities")
	private Set<Role> roles;

}
