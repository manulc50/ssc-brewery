package com.mlorenzo.brewery.web.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.mlorenzo.brewery.domain.Customer;
import com.mlorenzo.brewery.repositories.CustomerRepository;

@WebMvcTest(controllers = CustomerController.class)
public class CustomerControllerIT {
	
	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	CustomerRepository customerRepository;
	
	@Test
    void processCreationFormWithHttpBasicAndCsrf() throws Exception {
		when(customerRepository.save(any(Customer.class))).thenReturn(Customer.builder().build());
        mockMvc.perform(post("/customers").with(httpBasic("spring", "admin")).with(csrf())
        		.param("customerName", "Foo Customer"))
        	.andExpect(status().is3xxRedirection());
    }
	
	@Test
    void processCreationFormWithNoCredsAndCsrf() throws Exception {
        mockMvc.perform(post("/customers/new").with(csrf())
                .param("customerName", "Foo Customer"))
        	.andExpect(status().isUnauthorized());
    }
	
	@Test
    void processCreationFormWithHttpBasicAndNoCsrf() throws Exception {
        mockMvc.perform(post("/customers/new").with(httpBasic("user", "password"))
                .param("customerName", "Foo Customer"))
        .andExpect(status().isForbidden());
    }

}
