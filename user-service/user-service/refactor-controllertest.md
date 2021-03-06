# Refactor ControllerTest

## Create User Tests

Our test class is now very huge and has a whole lot of repetitive code. Let's look into shrinking it and make it look cleaner.

### Before refactor

```java
private static final String CREATE_USER = "/v1/%s/user";

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
            .andExpect(status().is2xxSuccessful()).andReturn();
}
```

If you look at them, we are creating the request object in every single method. We can have one method which can take requesterId, userId and userDto to create the request for us and use it in all the tests. The tests are bound to be smaller after that.

### After refactor

```java
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
    fMockMvc.perform(createUserRequest(ADMINISTRATOR.getId(), new UserDto("Mike Selby", "8765436548", "selby@mark.com")))
            .andExpect(status().is2xxSuccessful()).andReturn();
}

private static final String CREATE_USER = "/v1/%s/user";

private static RequestBuilder createUserRequest(@Nullable UUID requesterId, @Nullable UserDto userDto) {
    final var requestBuilder = MockMvcRequestBuilders
            .post(String.format(CREATE_USER, requesterId))
            .contentType(APPLICATION_JSON);
    if (userDto != null)
        requestBuilder.content(userDto.toString());
    return requestBuilder;
}
```

 Can you appreciate the refactor here? 

Each of the tests now clearly demonstrate the intent without the intricate details of a http request.

You can also move the endpoint constant and the createUserRequest in TestBase. They might be reusable in other test class as well.

```java
public class TestBase {

    private static final String CREATE_USER = "/v1/%s/user";

    protected static RequestBuilder createUserRequest(@Nullable UUID requesterId, @Nullable UserDto userDto) {
        final var requestBuilder = MockMvcRequestBuilders
                .post(String.format(CREATE_USER, requesterId))
                .contentType(APPLICATION_JSON);
        if (userDto != null)
            requestBuilder.content(userDto.toString());
        return requestBuilder;
    }
}
```

## Get User Tests

### Before refactor

```java
private static final String GET_USER = "/v1/%s/user/%s";

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
```

 We can apply the same refactor here and make the tests look cleaner.

### After refactor

```java
public class TestBase {

    ...

    private static final String GET_USER = "/v1/%s/user/%s";

    protected static RequestBuilder getUserRequest(@Nullable UUID requesterId, @Nullable UUID userId) {
        return MockMvcRequestBuilders
                .get(String.format(GET_USER, requesterId, userId))
                .contentType(APPLICATION_JSON);
    }
}
```

```java
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
```

 Each of the tests now clearly demonstrate the intent without the intricate details of a http request.

## Update User Tests

### Before refactor

```java
@Test
void testUpdateUserWhenRequesterIsNull() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .put(String.format(UPDATE_USER, null, null))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isBadRequest());
}

@Test
void testUpdateUserWhenUserIdIsNull() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .put(String.format(UPDATE_USER, UUID.randomUUID(), null))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isBadRequest());
}

@Test
void testUpdateUserWhenRequesterDoesNotExist() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .put(String.format(UPDATE_USER, UUID.randomUUID(), UUID.randomUUID()))
            .content(new UpdateUserDto("Mike Selby", "8765436548", "selby@mark.com").toString())
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
}

@Test
void testUpdateUserWhenRequestIsDifferentFromUser() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .put(String.format(UPDATE_USER, USER.getId(), UUID.randomUUID()))
            .content(new UpdateUserDto("Mike Selby", "8765436548", "selby@mark.com").toString())
            .contentType(APPLICATION_JSON))
            .andExpect(status().isForbidden());
}

@Test
void testUpdateUserWhenRequesterIsUpdatingOwnDetails() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .put(String.format(UPDATE_USER, USER.getId(), USER.getId()))
            .content(new UpdateUserDto("Mike Selby", "8765436548", "selby@mark.com").toString())
            .contentType(APPLICATION_JSON))
            .andExpect(status().is2xxSuccessful());
}
```

### After refactor

```java
public class TestBase {

    ...

    private static final String UPDATE_USER = "/v1/%s/user/%s";

    protected static RequestBuilder updateUserRequest(@Nullable UUID requesterId, @Nullable UUID userId, @Nullable UpdateUserDto updateUserDto) {
        final var requestBuilder = MockMvcRequestBuilders
                .put(String.format(UPDATE_USER, requesterId, userId))
                .contentType(APPLICATION_JSON);
        if (updateUserDto != null)
            requestBuilder.content(updateUserDto.toString());
        return requestBuilder;
    }
}
```

```java
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
```

 Each of the tests now clearly demonstrate the intent without the intricate details of a http request.

##  Delete User Tests

### Before refactor

```java
@Test
void testDeleteUserWhenRequesterIsNull() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .delete(String.format(DELETE_USER, null, null)))
            .andExpect(status().isBadRequest());
}

@Test
void testDeleteUserWhenUserIdIsNull() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .delete(String.format(DELETE_USER, UUID.randomUUID(), null)))
            .andExpect(status().isBadRequest());
}

@Test
void testDeleteUserWhenRequesterDoesNotExist() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .delete(String.format(DELETE_USER, UUID.randomUUID(), UUID.randomUUID())))
            .andExpect(status().isUnauthorized());
}

@Test
void testDeleteUserWhenRequesterIsDifferentFromUser() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .delete(String.format(DELETE_USER, USER.getId(), UUID.randomUUID())))
            .andExpect(status().isForbidden());
}

@Test
void testDeleteUserWhenRequestIsDeletingOwnProfile() throws Exception {
    final var user = fUserService.createUser(ADMINISTRATOR.getId(), new UserDto("Mike Selby", "8765436548", "selby@mark.com"));
    fMockMvc.perform(MockMvcRequestBuilders
            .delete(String.format(DELETE_USER, user.getId(), user.getId())))
            .andExpect(status().is2xxSuccessful());
}
```

### After refactor

```java
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
    final var user = fUserService.createUser(ADMINISTRATOR.getId(), new UserDto("Mike Selby", "8765436548", "selby@mark.com"));
    fMockMvc.perform(deleteUserRequest(user.getId(), user.getId()))
            .andExpect(status().is2xxSuccessful());
}
```

  Each of the tests now clearly demonstrate the intent without the intricate details of a http request.

All of our tests look brilliant now.



