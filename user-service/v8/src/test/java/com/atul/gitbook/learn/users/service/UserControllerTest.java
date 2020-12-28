package com.atul.gitbook.learn.users.service;

import com.atul.gitbook.learn.users.TestBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends TestBase {

    @Autowired
    private WebApplicationContext fAppContext;

    private MockMvc fMockMvc;

    @BeforeAll
    void setUp() {
        fMockMvc = MockMvcBuilders.webAppContextSetup(fAppContext).build();
    }

    private static final String GET_USERS = "/v1/%s/users/%s";

    @Test
    void testCreateUserWhenRequesterIdIsNull() throws Exception {
        fMockMvc.perform(MockMvcRequestBuilders
                .get(String.format(GET_USERS, null, UUID.randomUUID()))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUserWhenUserIdIsNull() throws Exception {
        fMockMvc.perform(MockMvcRequestBuilders
                .get(String.format(GET_USERS, UUID.randomUUID(), null))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUserWhenRequesterExists() throws Exception {
        fMockMvc.perform(MockMvcRequestBuilders
                .get(String.format(GET_USERS, UUID.randomUUID(), UUID.randomUUID()))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
