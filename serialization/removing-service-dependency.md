# Removing Service Dependency

We have the `Serializers` ready to be consumed.

When we look at our Controller test, the delete user test uses a direct call to the `IUserService`. We need to replace that call with a http request.

```java
@Test
void testDeleteUserWhenRequestIsDeletingOwnProfile() throws Exception {
    final var user = fUserService.createUser(ADMINISTRATOR.getId(), new UserDto("Mike Selby", "8765436548", "selby@mark.com"));
    fMockMvc.perform(deleteUserRequest(user.getId(), user.getId()))
            .andExpect(status().is2xxSuccessful());
}
```

But before that, let's see if the create request is getting correctly parsed with our newly introduced serializers.

```java
@Test
void testCreateUserWhenRequesterIsAdministrator() throws Exception {
    fMockMvc.perform(createUserRequest(ADMINISTRATOR.getId(), new UserDto("Mike Selby", "8765436548", "selby@mark.com")))
            .andExpect(status().is2xxSuccessful()).andReturn();
}
```

## Modify the test

 We can include a `Serializer` for a model using this one-liner now.

```java
protected static final Serializer<User> USER_SERIALIZER = Serializers.newJsonSerializer(User.class);
```

The create test with assertions should look to be something like this.

```java
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
```

### Try to run the test

If you run the test, the tests should be passing.

## Modify the test

Coming to the delete test, let's make a create request for getting the user which will be deleted in the next step.

```java
@Test
void testDeleteUserWhenRequestIsDeletingOwnProfile() throws Exception {
    final var userDto = new UserDto("Mike Selby", "8765436548", "selby@mark.com");
    final var contentAsString = fMockMvc.perform(createUserRequest(ADMINISTRATOR.getId(), userDto))
            .andExpect(status().is2xxSuccessful())
            .andReturn().getResponse().getContentAsString();
    final var user = USER_SERIALIZER.deserialize(contentAsString);
    fMockMvc.perform(deleteUserRequest(user.getId(), user.getId()))
            .andExpect(status().is2xxSuccessful());
}
```

The test does not depend on the `IUserService` directly. We can remove that Auto-wiring. 

### Try to run the test

The test should be passing now.

## Modify the test

We can also make an additional assertion by add get requests before and after the delete request to ensure if the user actually got deleted.

```java
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
```

### Try to run the test

The test should be passing this time as well.

## Project Status

At this point, you can refactor this ugly looking test into something of your taste or you can keep it as is. You can raise a pull request on the repository with your suggestions and we can discuss on the many approaches.

We've decoupled a lot of things and the test now have direct dependencies on the controller layer and the 2 user objects defined in the `InMemoryRepository`. We must look for decoupling the repository and tests as well.

Except for the `IUserService`, all of our classes are well-refactored. Let's refactor that first and then we can move on to replacing the `InMemoryRepository` with a persistent database like `Postgres`. We'll figure out what to do with the 2 user objects during that change.

