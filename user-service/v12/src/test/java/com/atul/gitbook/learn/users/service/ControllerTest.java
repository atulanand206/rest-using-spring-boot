package com.atul.gitbook.learn.users.service;

import com.atul.gitbook.learn.TestBase;
import com.atul.gitbook.learn.users.models.UpdateUserDto;
import com.atul.gitbook.learn.users.models.UserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static com.atul.gitbook.learn.users.service.impl.InMemoryRepository.ADMINISTRATOR;
import static com.atul.gitbook.learn.users.service.impl.InMemoryRepository.USER;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ControllerTest extends TestBase {

    @Autowired
    private WebApplicationContext fAppContext;

    private MockMvc fMockMvc;

    @BeforeAll
    void setUp() {
        fMockMvc = MockMvcBuilders.webAppContextSetup(fAppContext).build();
    }

    @Test
    void testCreateUserWhenRequesterIsNull() throws Exception {
        fMockMvc.perform(createUserRequest(null, null))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUserWhenRequesterDoesNotExist() throws Exception {
        fMockMvc.perform(createUserRequest(UUID.randomUUID(), new UserDto("Mike Selby", "8765436548", "selby@mark.com")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateUserWhenRequesterExistsButIsNotAdministrator() throws Exception {
        fMockMvc.perform(createUserRequest(USER.getId(), new UserDto("Mike Selby", "8765436548", "selby@mark.com")))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateUserWhenRequesterIsAdministrator() throws Exception {
        final var expected = new UserDto("Mike Selby", "8765436548", "selby@mark.com");
        final var contentAsString = fMockMvc.perform(createUserRequest(ADMINISTRATOR.getId(), expected))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        final var actual = USER_SERIALIZER.deserialize(contentAsString);
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getPhone(), actual.getPhone());
        Assertions.assertEquals(expected.getEmail(), actual.getEmail());
    }

    @Test
    void testGetUserWhenRequesterIsNull() throws Exception {
        fMockMvc.perform(getUserRequest(null, null))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserWhenUserIdIsNull() throws Exception {
        fMockMvc.perform(getUserRequest(UUID.randomUUID(), null))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserWhenRequesterDoesNotExist() throws Exception {
        fMockMvc.perform(getUserRequest(UUID.randomUUID(), UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUserWhenRequesterIsDifferentFromUser() throws Exception {
        fMockMvc.perform(getUserRequest(USER.getId(), UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetUserWhenRequesterIsFetchingOwnDetails() throws Exception {
        fMockMvc.perform(getUserRequest(USER.getId(), USER.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testGetUserWhenRequesterIsAdministratorAndUserIsPresent() throws Exception {
        fMockMvc.perform(getUserRequest(ADMINISTRATOR.getId(), USER.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testUpdateUserWhenRequesterIsNull() throws Exception {
        fMockMvc.perform(updateUserRequest(null, null, null))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUserWhenUserIdIsNull() throws Exception {
        fMockMvc.perform(updateUserRequest(UUID.randomUUID(), null, null))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUserWhenRequesterDoesNotExist() throws Exception {
        fMockMvc.perform(updateUserRequest(UUID.randomUUID(), UUID.randomUUID(), new UpdateUserDto("Mike Selby", "8765436548", "selby@mark.com")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateUserWhenRequestIsDifferentFromUser() throws Exception {
        fMockMvc.perform(updateUserRequest(USER.getId(), UUID.randomUUID(), new UpdateUserDto("Mike Selby", "8765436548", "selby@mark.com")))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateUserWhenRequesterIsUpdatingOwnDetails() throws Exception {
        fMockMvc.perform(updateUserRequest(USER.getId(), USER.getId(), new UpdateUserDto("Mike Selby", "8765436548", "selby@mark.com")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testDeleteUserWhenRequesterIsNull() throws Exception {
        fMockMvc.perform(deleteUserRequest(null, null))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteUserWhenUserIdIsNull() throws Exception {
        fMockMvc.perform(deleteUserRequest(UUID.randomUUID(), null))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteUserWhenRequesterDoesNotExist() throws Exception {
        fMockMvc.perform(deleteUserRequest(UUID.randomUUID(), UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteUserWhenRequesterIsDifferentFromUser() throws Exception {
        fMockMvc.perform(deleteUserRequest(USER.getId(), UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteUserWhenRequestIsDeletingOwnProfile() throws Exception {
        final var userDto = new UserDto("Mike Selby", "8765436548", "selby@mark.com");
        final var contentAsString = fMockMvc.perform(createUserRequest(ADMINISTRATOR.getId(), userDto))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        final var user = USER_SERIALIZER.deserialize(contentAsString);
        fMockMvc.perform(getUserRequest(user.getId(), user.getId())).andExpect(status().is2xxSuccessful());
        fMockMvc.perform(deleteUserRequest(user.getId(), user.getId()))
                .andExpect(status().is2xxSuccessful());
        fMockMvc.perform(getUserRequest(user.getId(), user.getId())).andExpect(status().isUnauthorized());
    }
}
