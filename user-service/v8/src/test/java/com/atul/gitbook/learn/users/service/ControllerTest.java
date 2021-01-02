package com.atul.gitbook.learn.users.service;

import com.atul.gitbook.learn.TestBase;
import com.atul.gitbook.learn.users.models.UserDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static com.atul.gitbook.learn.users.service.impl.InMemoryRepository.ADMINISTRATOR;
import static com.atul.gitbook.learn.users.service.impl.InMemoryRepository.USER;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ControllerTest extends TestBase {

    private static final String CREATE_USER = "/v1/%s/user";
    private static final String GET_USER = "/v1/%s/user/%s";

    @Autowired
    private WebApplicationContext fAppContext;

    private MockMvc fMockMvc;

    @BeforeAll
    void setUp() {
        fMockMvc = MockMvcBuilders.webAppContextSetup(fAppContext).build();
    }

    @Test
    void testCreateUserWhenRequesterIsNull() throws Exception {
        fMockMvc.perform(MockMvcRequestBuilders
                .post(String.format(CREATE_USER, null))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUserWhenRequesterDoesNotExist() throws Exception {
        fMockMvc.perform(MockMvcRequestBuilders
                .post(String.format(CREATE_USER, UUID.randomUUID()))
                .content(new UserDto("Mike Selby", "8765436548", "selby@mark.com").toString())
                .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateUserWhenRequesterExistsButIsNotAdministrator() throws Exception {
        fMockMvc.perform(MockMvcRequestBuilders
                .post(String.format(CREATE_USER, USER.getId()))
                .content(new UserDto("Mike Selby", "8765436548", "selby@mark.com").toString())
                .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateUserWhenRequesterIsAdministrator() throws Exception {
        final var userDto = new UserDto("Mike Selby", "8765436548", "selby@mark.com");
        fMockMvc.perform(MockMvcRequestBuilders
                .post(String.format(CREATE_USER, ADMINISTRATOR.getId()))
                .content(userDto.toString())
                .contentType(APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testGetUserWhenRequesterIsNull() throws Exception {
        fMockMvc.perform(MockMvcRequestBuilders
                .get(String.format(GET_USER, null, null))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserWhenUserIdIsNull() throws Exception {
        fMockMvc.perform(MockMvcRequestBuilders
                .get(String.format(GET_USER, UUID.randomUUID(), null))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserWhenRequesterDoesNotExist() throws Exception {
        fMockMvc.perform(MockMvcRequestBuilders
                .get(String.format(GET_USER, UUID.randomUUID(), UUID.randomUUID()))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUserWhenRequesterIsDifferentFromUser() throws Exception {
        fMockMvc.perform(MockMvcRequestBuilders
                .get(String.format(GET_USER, USER.getId(), UUID.randomUUID()))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetUserWhenRequesterIsFetchingOwnDetails() throws Exception {
        fMockMvc.perform(MockMvcRequestBuilders
                .get(String.format(GET_USER, USER.getId(), USER.getId()))
                .contentType(APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testGetUserWhenRequesterIsAdministratorAndUserIsPresent() throws Exception {
        fMockMvc.perform(MockMvcRequestBuilders
                .get(String.format(GET_USER, ADMINISTRATOR.getId(), USER.getId()))
                .contentType(APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }
}
