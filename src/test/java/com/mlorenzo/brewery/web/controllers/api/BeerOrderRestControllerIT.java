package com.mlorenzo.brewery.web.controllers.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlorenzo.brewery.bootstrap.SecurityDataLoader;
import com.mlorenzo.brewery.domain.Beer;
import com.mlorenzo.brewery.domain.BeerOrder;
import com.mlorenzo.brewery.domain.Customer;
import com.mlorenzo.brewery.repositories.BeerRepository;
import com.mlorenzo.brewery.repositories.CustomerRepository;
import com.mlorenzo.brewery.web.models.BeerOrderDto;
import com.mlorenzo.brewery.web.models.BeerOrderLineDto;

// No podemos usar la anotación @WebMvcTest para la ejecución de los tests de esta clase porque los usuarios se obtienen de la base de datos y,
// por lo tanto, es necesario levantar en el contexto de Spring, además del controlador, más partes de la aplicación.
@SpringBootTest
@AutoConfigureMockMvc
public class BeerOrderRestControllerIT {
	
	@Autowired
    CustomerRepository customerRepository;
	
	@Autowired
    BeerRepository beerRepository;
	
	@Autowired
	ObjectMapper objectMapper;
	
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
//    @DisplayName("Create Test")
//    @Nested
//    class createOrderTests {


    @Test
    void createOrderWithNoCreds() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, beers.get(0).getId());
        mockMvc.perform(post(BeerOrderRestController.BASE_PATH, stPeteCustomer.getId())
        		.characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerOrderDto)))
        	.andExpect(status().isUnauthorized());
    }

    @WithUserDetails("spring")
    @Test
    void createOrderUserWithAdmin() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, beers.get(0).getId());
        mockMvc.perform(post(BeerOrderRestController.BASE_PATH, stPeteCustomer.getId())
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerOrderDto)))
        	.andExpect(status().isCreated());
    }

    @WithUserDetails(SecurityDataLoader.STPETE_USER)
    @Test
    void createOrderUserWithAuthCustomer() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, beers.get(0).getId());
        mockMvc.perform(post(BeerOrderRestController.BASE_PATH, stPeteCustomer.getId())
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerOrderDto)))
        	.andExpect(status().isCreated());
    }

    @WithUserDetails(SecurityDataLoader.KEYWEST_USER)
    @Test
    void createOrderUserWithNoAuthCustomer() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, beers.get(0).getId());
        mockMvc.perform(post(BeerOrderRestController.BASE_PATH, stPeteCustomer.getId())
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerOrderDto)))
        	.andExpect(status().isForbidden());
    }

    @Test
    void listOrdersWithNoCreds() throws Exception {
        mockMvc.perform(get(BeerOrderRestController.BASE_PATH, stPeteCustomer.getId()))
        	.andExpect(status().isUnauthorized());
    }

    @WithUserDetails("spring")
    @Test
    void listOrdersWithAdmin() throws Exception {
        mockMvc.perform(get(BeerOrderRestController.BASE_PATH, stPeteCustomer.getId()))
        	.andExpect(status().isOk());
    }

    @WithUserDetails(SecurityDataLoader.STPETE_USER)
    @Test
    void listOrdersWithAuthCustomer() throws Exception {
        mockMvc.perform(get(BeerOrderRestController.BASE_PATH, stPeteCustomer.getId()))
        	.andExpect(status().isOk());
    }

    @WithUserDetails(SecurityDataLoader.DUNEDIN_USER)
    @Test
    void listOrdersWithNoAuthCustomer() throws Exception {
        mockMvc.perform(get(BeerOrderRestController.BASE_PATH, stPeteCustomer.getId()))
        	.andExpect(status().isForbidden());
    }
    
    @Test
    void getByOrderIdWithNoCreds() throws Exception {
        mockMvc.perform(get(BeerOrderRestController.BASE_PATH + "/493410b3-dd0b-4b78-97bf-289f50f6e74f", stPeteCustomer.getId()))
        	.andExpect(status().isUnauthorized());
    }

    // Usamos esta anotación @Transactional en este test porque los pedidos de cervezas se obtienen desde el cliente de forma perezosa
    @Transactional(readOnly = true)
    @WithUserDetails("spring")
    @Test
    void getByOrderIdWithAdmin() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(BeerOrderRestController.BASE_PATH + "/{orderId}", stPeteCustomer.getId(), beerOrder.getId()))
        	.andExpect(status().is2xxSuccessful());
    }

    // Usamos esta anotación @Transactional en este test porque los pedidos de cervezas se obtienen desde el cliente de forma perezosa
    @Transactional(readOnly = true)
    @WithUserDetails(SecurityDataLoader.STPETE_USER)
    @Test
    void getByOrderIdWithCustomerAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(BeerOrderRestController.BASE_PATH + "/{orderId}", stPeteCustomer.getId(), beerOrder.getId()))
        	.andExpect(status().is2xxSuccessful());
    }

    // Usamos esta anotación @Transactional en este test porque los pedidos de cervezas se obtienen desde el cliente de forma perezosa
    @Transactional(readOnly = true)
    @WithUserDetails(SecurityDataLoader.DUNEDIN_USER)
    @Test
    void getByOrderIdWithCustomerNoAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(BeerOrderRestController.BASE_PATH, stPeteCustomer.getId(), beerOrder.getId()))
        	.andExpect(status().isForbidden());
    }

    @Test
    void pickUpOrderWithNoCreds() throws Exception {
        mockMvc.perform(patch(BeerOrderRestController.BASE_PATH + "/493410b3-dd0b-4b78-97bf-289f50f6e74f/pickup", stPeteCustomer.getId()))
        	.andExpect(status().isUnauthorized());
    }

    // Usamos esta anotación @Transactional en este test porque los pedidos de cervezas se obtienen desde el cliente de forma perezosa
    @Transactional(readOnly = true)
    @WithUserDetails("spring")
    @Test
    void pickUpOrderWithAdmin() throws Exception {
    	BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(patch(BeerOrderRestController.BASE_PATH + "/{orderId}/pickup", stPeteCustomer.getId(), beerOrder.getId()))
        	.andExpect(status().isNoContent());
    }

    // Usamos esta anotación @Transactional en este test porque los pedidos de cervezas se obtienen desde el cliente de forma perezosa
    @Transactional(readOnly = true)
    @WithUserDetails(SecurityDataLoader.STPETE_USER)
    @Test
    void pickUpOrderWithAuthCustomer() throws Exception {
    	BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(patch(BeerOrderRestController.BASE_PATH + "/{orderId}/pickup", stPeteCustomer.getId(), beerOrder.getId()))
        	.andExpect(status().isNoContent());
    }

    // Usamos esta anotación @Transactional en este test porque los pedidos de cervezas se obtienen desde el cliente de forma perezosa
    @Transactional(readOnly = true)
    @WithUserDetails(SecurityDataLoader.KEYWEST_USER)
    @Test
    void pickUpOrderWithNoAuthCustomer() throws Exception {
    	BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(patch(BeerOrderRestController.BASE_PATH + "/{orderId}/pickup", stPeteCustomer.getId(), beerOrder.getId()))
        	.andExpect(status().isForbidden());
    }

    BeerOrderDto buildOrderDto(Customer customer, UUID beerId) {
        List<BeerOrderLineDto> orderLines = Arrays.asList(BeerOrderLineDto.builder()
                .id(UUID.randomUUID())
                .beerId(beerId)
                .orderQuantity(5)
                .build());
        return BeerOrderDto.builder()
                .customerId(customer.getId())
                .customerRef("123")
                .orderStatusCallbackUrl("http://example.com")
                .beerOrderLines(orderLines)
                .build();
    }

}
