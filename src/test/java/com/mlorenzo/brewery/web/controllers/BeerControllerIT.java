package com.mlorenzo.brewery.web.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.mlorenzo.brewery.domain.Beer;
import com.mlorenzo.brewery.repositories.BeerRepository;

// No podemos usar la anotación @WebMvcTest para la ejecución de los tests de esta clase porque los usuarios se obtienen de la base de datos y,
// por lo tanto, es necesario levantar en el contexto de Spring, además del controlador, más partes de la aplicación.
@SpringBootTest
@AutoConfigureMockMvc
public class BeerControllerIT {
	
	@Autowired
	BeerRepository beerRepository;
	
	@Autowired
	MockMvc mockMvc;
	
	@DisplayName("Init New Form")
    @Nested
    class InitNewForm {
		
		@ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("com.mlorenzo.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void initCreationFormWithHttpBasic(String user, String pwd) throws Exception {
            mockMvc.perform(get("/beers/new").with(httpBasic(user, pwd)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("beers/createOrUpdateBeer"))
                    .andExpect(model().attributeExists("beer"));
        }

        @Test
        void initCreationFormWithNoCreds() throws Exception {
            mockMvc.perform(get("/beers/new"))
                    .andExpect(status().isUnauthorized());
        }
	}
	
	@DisplayName("Init Find Beer Form")
    @Nested
    class FindForm{
		
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("com.mlorenzo.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeersFormWithHttpBasic(String user, String pwd) throws Exception{
            mockMvc.perform(get("/beers/find").with(httpBasic(user, pwd)))
            		.andExpect(status().isOk())
                    .andExpect(view().name("beers/findBeers"))
                    .andExpect(model().attributeExists("beer"));
        }

        @Test
        void findBeersWithAnonymous() throws Exception{
            mockMvc.perform(get("/beers/find").with(anonymous()))
                    .andExpect(status().isUnauthorized());
        }
    }
	
	@DisplayName("Process Find Beer Form")
    @Nested
    class ProcessFindForm {
		
        @Test
        void findBeerFormWithNoCreds() throws Exception {
            mockMvc.perform(get("/beers").param("beerName", ""))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("com.mlorenzo.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeerFormWithHttpBasic(String user, String pwd) throws Exception {
            mockMvc.perform(get("/beers").param("beerName", "").with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }
    }

    @DisplayName("Get Beer By Id")
    @Nested
    class GetByID {
    	
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("com.mlorenzo.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void getBeerByIdWithHttpBasic(String user, String pwd) throws Exception {
        	Beer beer = beerRepository.findAll().get(0);
            mockMvc.perform(get("/beers/" + beer.getId()).with(httpBasic(user, pwd)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("beers/beerDetails"))
                    .andExpect(model().attributeExists("beer"));
        }

        @Test
        void getBeerByIdNoCreds() throws Exception {
            mockMvc.perform(get("/beers/493410b3-dd0b-4b78-97bf-289f50f6e74f"))
                    .andExpect(status().isUnauthorized());
        }
    }
    
    @DisplayName("Add Beers")
    @Nested
    class AddCustomers {
		
        @Test
        void processCreationFormWithHttpBasicAndCsrf() throws Exception {
            mockMvc.perform(post("/beers").with(httpBasic("spring", "admin")).with(csrf())
                    .param("beerName", "Foo Beer"))
            	.andExpect(status().is3xxRedirection());
        }
		
		@Test
	    void processCreationFormWithHttpBasicAndNoCsrf() throws Exception {
	        mockMvc.perform(post("/beers").with(httpBasic("spring", "admin"))
	                .param("beerName", "Foo Beer"))
	        	.andExpect(status().isForbidden());
	    }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("com.mlorenzo.brewery.web.controllers.BeerControllerIT#getStreamNotAdmin")
        void processCreationFormWithHttpBasicNoRoleAdminAndCsrf(String user, String pwd) throws Exception {
            mockMvc.perform(post("/beers").with(httpBasic(user, pwd)).with(csrf())
                    .param("beerName", "Foo Beer"))
            	.andExpect(status().isForbidden());
        }

        @Test
        void processCreationFormWithNoCredsAndCsrf() throws Exception {
            mockMvc.perform(post("/beers").with(csrf())
                    .param("beerName", "Foo Beer"))
            	.andExpect(status().isUnauthorized());
        }
	}
    
    static Stream<Arguments> getStreamAdminCustomer() {
        return Stream.of(Arguments.of("spring" , "admin"),
                Arguments.of("scott", "tiger"));
    }
    
    static Stream<Arguments> getStreamAllUsers() {
        return Stream.of(Arguments.of("spring" , "admin"),
                Arguments.of("scott", "tiger"),
                Arguments.of("user", "password"));
    }

    static Stream<Arguments> getStreamNotAdmin() {
        return Stream.of(Arguments.of("scott", "tiger"),
                Arguments.of("user", "password"));
    }

}
