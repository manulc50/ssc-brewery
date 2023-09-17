package com.mlorenzo.brewery.web.controllers.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.mlorenzo.brewery.bootstrap.SecurityDataLoader;
import com.mlorenzo.brewery.domain.Beer;
import com.mlorenzo.brewery.domain.BeerOrder;
import com.mlorenzo.brewery.domain.Customer;
import com.mlorenzo.brewery.repositories.BeerRepository;
import com.mlorenzo.brewery.repositories.CustomerRepository;

// No podemos usar la anotación @WebMvcTest para la ejecución de los tests de esta clase porque los usuarios se obtienen de la base de datos y,
// por lo tanto, es necesario levantar en el contexto de Spring, además del controlador, más partes de la aplicación.
@SpringBootTest
@AutoConfigureMockMvc
public class BeerOrderRestControllerV2IT {
	
	@Autowired
    CustomerRepository customerRepository;
	
	@Autowired
    BeerRepository beerRepository;
	
	@Autowired
	MockMvc mockMvc;
	
	Customer stPeteCustomer;
	List<Beer> beers;
	
	@BeforeEach
    void setUp() {
        stPeteCustomer = customerRepository.findByCustomerName(SecurityDataLoader.ST_PETE_DISTRIBUTING).orElseThrow();
        beers = beerRepository.findAll();
    }
	
// Cant use nested tests bug - https://github.com/spring-projects/spring-security/issues/8793
// Parece ser que, en esta versión de Spring Boot, la configuración de Spring Security no se propaga a las clases internas y,
// por lo tanto, no podemos hacer uso de anotaciones de Spring Security, como por ejemplo @WithUserDetails, dentro de ellas.
//		    @DisplayName("List Test")
//		    @Nested
//		    class listOrdersTests {	
	
	@Test
    void listOrdersWithNoCreds() throws Exception {
        mockMvc.perform(get(BeerOrderRestControllerV2.BASE_PATH))
        	.andExpect(status().isUnauthorized());
    }

    @WithUserDetails(value = "spring")
    @Test
    void listOrdersWithAdmin() throws Exception {
        mockMvc.perform(get(BeerOrderRestControllerV2.BASE_PATH))
        	.andExpect(status().isOk());
    }

    @WithUserDetails(value = SecurityDataLoader.STPETE_USER)
    @Test
    void listOrdersWithCustomerStPete() throws Exception {
        mockMvc.perform(get(BeerOrderRestControllerV2.BASE_PATH))
        	.andExpect(status().isOk());
    }

    @WithUserDetails(value = SecurityDataLoader.DUNEDIN_USER)
    @Test
    void listOrdersWithCustomerDunedin() throws Exception {
        mockMvc.perform(get(BeerOrderRestControllerV2.BASE_PATH))
        	.andExpect(status().isOk());
    }
    
    @Test
    void getByOrderIdWithNoCreds() throws Exception {
        mockMvc.perform(get(BeerOrderRestControllerV2.BASE_PATH + "/493410b3-dd0b-4b78-97bf-289f50f6e74f"))
        	.andExpect(status().isUnauthorized());
    }
    
    @Test
    void getByOrderIdSecureWithNoCreds() throws Exception {
        mockMvc.perform(get(BeerOrderRestControllerV2.BASE_PATH + "/493410b3-dd0b-4b78-97bf-289f50f6e74f/secure"))
        	.andExpect(status().isUnauthorized());
    }

    // Usamos esta anotación @Transactional en este test porque los pedidos de cervezas se obtienen desde el cliente de forma perezosa
    @Transactional
    @WithUserDetails("spring")
    @Test
    void getByOrderIdWithAdmin() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(BeerOrderRestControllerV2.BASE_PATH + "/{orderId}", beerOrder.getId()))
        	.andExpect(status().is2xxSuccessful());
    }
    
    // Usamos esta anotación @Transactional en este test porque los pedidos de cervezas se obtienen desde el cliente de forma perezosa
    @Transactional
    @WithUserDetails("spring")
    @Test
    void getByOrderIdSecureWithAdmin() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(BeerOrderRestControllerV2.BASE_PATH + "/{orderId}/secure", beerOrder.getId()))
        	.andExpect(status().is2xxSuccessful());
    }

    // Usamos esta anotación @Transactional en este test porque los pedidos de cervezas se obtienen desde el cliente de forma perezosa
    @Transactional
    @WithUserDetails(SecurityDataLoader.STPETE_USER)
    @Test
    void getByOrderIdWithAuthCustomer() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(BeerOrderRestControllerV2.BASE_PATH + "/{orderId}", beerOrder.getId()))
        	.andExpect(status().is2xxSuccessful());
    }
    
    // Usamos esta anotación @Transactional en este test porque los pedidos de cervezas se obtienen desde el cliente de forma perezosa
    @Transactional
    @WithUserDetails(SecurityDataLoader.STPETE_USER)
    @Test
    void getByOrderIdSecureWithAuthCustomer() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(BeerOrderRestControllerV2.BASE_PATH + "/{orderId}/secure", beerOrder.getId()))
        	.andExpect(status().is2xxSuccessful());
    }

    // Usamos esta anotación @Transactional en este test porque los pedidos de cervezas se obtienen desde el cliente de forma perezosa
    @Transactional
    @WithUserDetails(SecurityDataLoader.DUNEDIN_USER)
    @Test
    void getByOrderIdWithNoAuthCustomer() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(BeerOrderRestControllerV2.BASE_PATH + "/" + beerOrder.getId()))
                .andExpect(status().isForbidden());
    }
    
    // Usamos esta anotación @Transactional en este test porque los pedidos de cervezas se obtienen desde el cliente de forma perezosa
    @Transactional
    @WithUserDetails(SecurityDataLoader.DUNEDIN_USER)
    @Test
    void getByOrderIdSecureWithNoAuthCustomer() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(BeerOrderRestControllerV2.BASE_PATH + "/" + beerOrder.getId() + "/secure"))
                .andExpect(status().isNotFound());
    }

}
