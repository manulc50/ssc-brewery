package com.mlorenzo.brewery.web.controllers.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.mlorenzo.brewery.services.BeerService;

@WebMvcTest(controllers = BeerRestController.class)
public class BeerRestControllerIT {
	
	@MockBean
	BeerService beerService;
	
	@Autowired
	MockMvc mockMvc;
	
	@Test
    void findBeers() throws Exception {
        mockMvc.perform(get("/api/v1/beers"))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerById() throws Exception {
        mockMvc.perform(get("/api/v1/beers/97df0c39-90c4-4ae0-b663-453e8e19c311"))
                .andExpect(status().isOk());
    }
    
    @Test
    void findBeerByUpc() throws Exception {
        mockMvc.perform(get("/api/v1/beers/upc/0631234200036"))
                .andExpect(status().isOk());
    }
    
    // Activar cuando se aplique el filtro personalizado "RestHeaderAuthFilter" en Spring Security
    //@Disabled
    @Test
    void deleteBeerWithHeaderCreds() throws Exception {
        mockMvc.perform(delete("/api/v1/beers/97df0c39-90c4-4ae0-b663-453e8e19c311")
        		.header("Api-Key", "spring").header("Api-Secret", "admin"))
        	.andExpect(status().isNoContent());
    }
    
    // Activar cuando se aplique el filtro personalizado "RestHeaderAuthFilter" en Spring Security
    //@Disabled
    @Test
    void deleteBeerWithHeaderBadCreds() throws Exception {
        mockMvc.perform(delete("/api/v1/beers/97df0c39-90c4-4ae0-b663-453e8e19c311")
        		.header("Api-Key", "spring").header("Api-Secret", "adminXXXX"))
        	.andExpect(status().isUnauthorized());
    }
    
    // Activar cuando se aplique el filtro personalizado "RestUrlAuthFilter" en Spring Security
    @Disabled
    @Test
    void deleteBeerWithUrlCreds() throws Exception {
        mockMvc.perform(delete("/api/v1/beers/97df0c39-90c4-4ae0-b663-453e8e19c311")
        		.param("apiKey", "scott").param("apiSecret", "tiger"))
        	.andExpect(status().isNoContent());
    }

    // Activar cuando se aplique el filtro personalizado "RestUrlAuthFilter" en Spring Security
    @Disabled
    @Test
    void deleteBeerWithUrlBadCreds() throws Exception {
        mockMvc.perform(delete("/api/v1/beers/97df0c39-90c4-4ae0-b663-453e8e19c311")
        		.header("apiKey", "scottXXXX").header("apiSecret", "tiger"))
        	.andExpect(status().isUnauthorized());
    }
}
