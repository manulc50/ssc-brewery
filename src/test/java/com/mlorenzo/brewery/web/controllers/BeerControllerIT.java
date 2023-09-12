package com.mlorenzo.brewery.web.controllers;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.mlorenzo.brewery.repositories.BeerRepository;

@WebMvcTest(controllers = BeerController.class)
public class BeerControllerIT {
	
	@MockBean
	BeerRepository beerRepository;

	@Autowired
	MockMvc mockMvc;
	
	// Se comenta porque, ahora, el endpoint de este test es público
	// Da igual el nombre de usuario que pongamos en esta anotación porque siempre simula un usuario autenticado correctamente en la aplicación
	//@WithMockUser("spring")
	@Test
    void findBeers() throws Exception {
        mockMvc.perform(get("/beers/find"))
        	.andExpect(status().isOk())
        	.andExpect(view().name("beers/findBeers"))
        	.andExpect(model().attributeExists("beer"));
        verifyNoInteractions(beerRepository);
    }
	
	@Test
    void findBeersWithAnonymous() throws Exception {
        mockMvc.perform(get("/beers/find")
        		// Se comenta porque, ahora, el endpoint de este test es público. Para ello, podemos usar el método estático "anonymous" o no poner nada
        		//.with(httpBasic("spring", "admin"))
        		.with(anonymous()))
        	.andExpect(status().isOk())
            .andExpect(view().name("beers/findBeers"))
            .andExpect(model().attributeExists("beer"));
        verifyNoInteractions(beerRepository);
	}
	
	@Test
    void initCreationForm() throws Exception {
        mockMvc.perform(get("/beers/new").with(httpBasic("user", "password")))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/createOrUpdateBeer"))
                .andExpect(model().attributeExists("beer"));
    }
	
	@Test
    void initCreationFormWithScott() throws Exception {
        mockMvc.perform(get("/beers/new").with(httpBasic("scott", "tiger")))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/createOrUpdateBeer"))
                .andExpect(model().attributeExists("beer"));
    }
	
	@Test
    void initCreationFormWithSpring() throws Exception {
        mockMvc.perform(get("/beers/new").with(httpBasic("spring", "admin")))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/createOrUpdateBeer"))
                .andExpect(model().attributeExists("beer"));
    }
}
