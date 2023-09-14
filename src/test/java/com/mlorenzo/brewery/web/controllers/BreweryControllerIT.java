package com.mlorenzo.brewery.web.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

// No podemos usar la anotación @WebMvcTest para la ejecución de los tests de esta clase porque los usuarios se obtienen de la base de datos y,
// por lo tanto, es necesario levantar en el contexto de Spring, además del controlador, más partes de la aplicación.
@SpringBootTest
@AutoConfigureMockMvc
public class BreweryControllerIT {
	
	@Autowired
	MockMvc mockMvc;

	@Test
    void listBreweriesWithHttpBasicCustomerRole() throws Exception {
        mockMvc.perform(get("/breweries")
        		.with(httpBasic("scott", "tiger")))
        	.andExpect(status().is2xxSuccessful());
    }

    @Test
    void listBreweriesWithHttpBasicAdminRole() throws Exception {
        mockMvc.perform(get("/breweries")
                .with(httpBasic("spring", "admin")))
        	.andExpect(status().isOk());
    }

    @Test
    void listBreweriesWithHttpBasicUserRole() throws Exception {
        mockMvc.perform(get("/breweries")
                .with(httpBasic("user", "password")))
        	.andExpect(status().isForbidden());
    }

    @Test
    void listBreweriesWithNoAuth() throws Exception {
        mockMvc.perform(get("/breweries"))
        	.andExpect(status().isUnauthorized());
    }
}
