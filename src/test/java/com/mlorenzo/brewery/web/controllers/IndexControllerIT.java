package com.mlorenzo.brewery.web.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = IndexController.class)
public class IndexControllerIT {
	
	@MockBean
	PersistentTokenRepository tokenRepository;
	
	@Autowired
	MockMvc mockMvc;
	
	@Test
    void testGetIndexSlash() throws Exception {
        mockMvc.perform(get("/" ))
                .andExpect(status().isOk());
    }

}
