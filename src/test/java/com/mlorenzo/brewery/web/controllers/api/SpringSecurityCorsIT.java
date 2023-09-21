package com.mlorenzo.brewery.web.controllers.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SpringSecurityCorsIT {

	@Autowired
	MockMvc mockMvc;
	
	@WithUserDetails("spring")
    @Test
    void testFindBeersAUTH() throws Exception {
        mockMvc.perform(get("/api/v1/beers")
                .header("Origin", "https://test.com"))
        	.andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void testFindBeersPREFLIGHT() throws Exception {
        mockMvc.perform(options("/api/v1/beers")
                .header("Origin", "https://test.com")
                .header("Access-Control-Request-Method", "GET"))
        	.andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void testPostBeersPREFLIGHT() throws Exception {
        mockMvc.perform(options("/api/v1/beers")
                .header("Origin", "https://test.com")
                .header("Access-Control-Request-Method", "POST"))
        	.andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void testPutBeersPREFLIGHT() throws Exception {
        mockMvc.perform(options("/api/v1/beers/1234")
                .header("Origin", "https://test.com")
                .header("Access-Control-Request-Method", "PUT"))
        	.andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void testDeleteBeersPREFLIGHT() throws Exception {
        mockMvc.perform(options("/api/v1/beers/1234")
                .header("Origin", "https://test.com")
                .header("Access-Control-Request-Method", "DELETE"))
        	.andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }
}
