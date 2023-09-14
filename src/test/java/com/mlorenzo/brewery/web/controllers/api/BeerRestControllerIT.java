package com.mlorenzo.brewery.web.controllers.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Random;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.mlorenzo.brewery.domain.Beer;
import com.mlorenzo.brewery.repositories.BeerRepository;
import com.mlorenzo.brewery.web.models.BeerStyleEnum;

// No podemos usar la anotación @WebMvcTest para la ejecución de los tests de esta clase porque los usuarios se obtienen de la base de datos y,
// por lo tanto, es necesario levantar en el contexto de Spring, además del controlador, más partes de la aplicación.
@SpringBootTest
@AutoConfigureMockMvc
public class BeerRestControllerIT {
	
	@Autowired
	BeerRepository beerRepository;
	
	@Autowired
	MockMvc mockMvc;
	
	@DisplayName("List Beers")
    @Nested
    class ListBeers {
		
		@Test
	    void findBeersWithNoCreds() throws Exception {
	        mockMvc.perform(get("/api/v1/beers"))
	                .andExpect(status().isUnauthorized());
	    }
		
		@ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("com.mlorenzo.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeersWithHttpBasic(String user, String pwd) throws Exception {
            mockMvc.perform(get("/api/v1/beers").with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }
	}
	
	@DisplayName("Get Beer By ID")
	@Nested
	class GetBeerById {
		
		@Test
	    void findBeerByIdWithNoCreds() throws Exception {
	        mockMvc.perform(get("/api/v1/beers/97df0c39-90c4-4ae0-b663-453e8e19c311"))
	                .andExpect(status().isUnauthorized());
	    }
		
		@ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("com.mlorenzo.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeerByIdWithHttpBasic(String user, String pwd) throws Exception {
            Beer beer = beerRepository.findAll().get(0);
            mockMvc.perform(get("/api/v1/beers/" + beer.getId()).with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }
	}

	@Nested
    @DisplayName("Find By UPC")
    class FindByUPC {
		
		@Test
	    void findBeerByUpcWithNoCreds() throws Exception {
	        mockMvc.perform(get("/api/v1/beers/upc/11111111"))
	                .andExpect(status().isUnauthorized());
	    }
		
		@ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("com.mlorenzo.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeerByUpcWithHttpBasic(String user, String pwd) throws Exception {
			Beer beer = beerRepository.findAll().get(0);
            mockMvc.perform(get("/api/v1/beers/upc/" + beer.getUpc())
                    .with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }
	}
    
    @DisplayName("Delete Tests")
    @Nested
    class DeleteTests {
    	Random rand = new Random();
    	
    	@Test
        void deleteBeerNoCreds() throws Exception {
            mockMvc.perform(delete("/api/v1/beers/97df0c39-90c4-4ae0-b663-453e8e19c311"))
            	.andExpect(status().isUnauthorized());
        }
    	
    	 @ParameterizedTest(name = "#{index} with [{arguments}]")
         @MethodSource("com.mlorenzo.brewery.web.controllers.BeerControllerIT#getStreamNotAdmin")
         void deleteBeerHttpBasicNoAdminRole(String user, String pwd) throws Exception {
             mockMvc.perform(delete("/api/v1/beers/97df0c39-90c4-4ae0-b663-453e8e19c311")
                     .with(httpBasic(user, pwd)))
                     .andExpect(status().isForbidden());
         }
        
        @Test
        void deleteBeerWithHttpBasicAdminRole() throws Exception {
            mockMvc.perform(delete("/api/v1/beers/" + beerToDelete().getId())
            		.with(httpBasic("spring", "admin")))
            	.andExpect(status().isNoContent());
        }
        
        // Activar cuando se aplique el filtro personalizado "RestHeaderAuthFilter" en Spring Security
        @Disabled
        @Test
        void deleteBeerWithHeaderCreds() throws Exception {
            mockMvc.perform(delete("/api/v1/beers/" + beerToDelete().getId())
            		.header("Api-Key", "spring").header("Api-Secret", "admin"))
            	.andExpect(status().isNoContent());
        }
        
        // Activar cuando se aplique el filtro personalizado "RestHeaderAuthFilter" en Spring Security
        @Disabled
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
            mockMvc.perform(delete("/api/v1/beers/" + beerToDelete().getId())
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
        
        Beer beerToDelete() {
            return beerRepository.saveAndFlush(Beer.builder()
                    .beerName("Delete Me Beer")
                    .beerStyle(BeerStyleEnum.IPA)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(String.valueOf(rand.nextInt(99999999)))
                    .build());
        }
    }
  
}
