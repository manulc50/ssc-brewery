package com.mlorenzo.brewery.web.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

//No podemos usar la anotación @WebMvcTest para la ejecución de los tests de esta clase porque los usuarios se obtienen de la base de datos y,
//por lo tanto, es necesario levantar en el contexto de Spring, además del controlador, más partes de la aplicación.
@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerIT {
	
	@Autowired
	MockMvc mockMvc;
	

	@DisplayName("List Customers")
    @Nested
    class ListCustomers {
		
		@Test
	    void testListCustomersNoCreds() throws Exception {
	        mockMvc.perform(get("/customers"))
	        	.andExpect(status().isUnauthorized());
	    }
		
		@ParameterizedTest(name = "#{index} with [{arguments}]")
	    @MethodSource("com.mlorenzo.brewery.web.controllers.BeerControllerIT#getStreamAdminCustomer")
	    void testListCustomersWithHttpBasic(String user, String pwd) throws Exception {
	        mockMvc.perform(get("/customers").with(httpBasic(user, pwd)))
	        	.andExpect(status().isOk());

	    }

	    @Test
	    void testListCustomersWithHttpBasicUserRole() throws Exception {
	        mockMvc.perform(get("/customers").with(httpBasic("user", "password")))
	        	.andExpect(status().isForbidden());
	    }
	}
	
	@DisplayName("Add Customers")
    @Nested
    class AddCustomers {
		
        @Test
        void processCreationFormWithHttpBasicAndCsrf() throws Exception {
            mockMvc.perform(post("/customers").with(httpBasic("spring", "admin")).with(csrf())
                    .param("customerName", "Foo Customer"))
            	.andExpect(status().is3xxRedirection());
        }
		
		@Test
	    void processCreationFormWithHttpBasicAndNoCsrf() throws Exception {
	        mockMvc.perform(post("/customers").with(httpBasic("spring", "admin"))
	                .param("customerName", "Foo Customer"))
	        	.andExpect(status().isForbidden());
	    }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("com.mlorenzo.brewery.web.controllers.BeerControllerIT#getStreamNotAdmin")
        void processCreationFormWithHttpBasicNoRoleAdminAndCsrf(String user, String pwd) throws Exception {
            mockMvc.perform(post("/customers").with(httpBasic(user, pwd)).with(csrf())
                    .param("customerName", "Foo Customer2"))
            	.andExpect(status().isForbidden());
        }

        @Test
        void processCreationFormWithNoCredsAndCsrf() throws Exception {
            mockMvc.perform(post("/customers").with(csrf())
                    .param("customerName", "Foo Customer"))
            	.andExpect(status().isUnauthorized());
        }
	}

}
