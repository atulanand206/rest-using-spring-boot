# Delete User Endpoint

{% api-method method="delete" host="https://api.content.com" path="/v1/{requesterId}/user/{userId}" %}
{% api-method-summary %}
Get user details
{% endapi-method-summary %}

{% api-method-description %}
This endpoint allows you to delete a user. A user can delete his/her own profile and an administrator can delete any user.
{% endapi-method-description %}

{% api-method-spec %}
{% api-method-request %}
{% api-method-path-parameters %}
{% api-method-parameter name="requesterId" type="string" required=false %}
UUID of the requester. Used to check for the permissions required for the endpoint.
{% endapi-method-parameter %}

{% api-method-parameter name="userId" type="string" %}
UUID of the user whose profile is being queried.
{% endapi-method-parameter %}
{% endapi-method-path-parameters %}
{% endapi-method-request %}

{% api-method-response %}
{% api-method-response-example httpCode=200 %}
{% api-method-response-example-description %}

{% endapi-method-response-example-description %}

```

```
{% endapi-method-response-example %}
{% endapi-method-response %}
{% endapi-method-spec %}
{% endapi-method %}

## Write a new test

```java
@Test
void testDeleteUserWhenRequesterIsNull() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .delete(String.format(DELETE_USER, null, null)))
            .andExpect(status().isBadRequest());
}
```

### Try to run the test

```java
[ERROR] testDeleteUserWhenRequesterIsNull  Time elapsed: 0.014 s  <<< FAILURE!
java.lang.AssertionError: Status expected:<400> but was:<405>
        at com.atul.gitbook.learn.users.service.ControllerTest.testDeleteUserWhenRequesterIsNull(ControllerTest.java:169)
```

### Let's try to resolve the failure

Let's add a `deleteUser()` method in the `UserController`.

```java
@DeleteMapping("/v1/{requesterId}/user/{userId}")
public void deleteUser(@PathVariable("requesterId") UUID requesterId,
    @PathVariable("userId") UUID userId) {
}
```

### Try to run the test

The tests should be passing now.

### Consume the endpoint

```java
curl -X DELETE \
  http://localhost:8080/v1/null/user/null \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: eb201a17-9d01-e8da-809d-add6ec6354f2'
```

```java
{
    "status": 400,
    "error": "Invalid UUID string: null",
    "timestamp": "2021-01-02T14:11:04Z",
    "path": "/v1/null/user/null",
    "message": "Bad Request"
}
```

The response returns a status and error message correctly.

## Write a new test

```java
@Test
void testDeleteUserWhenUserIdIsNull() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .delete(String.format(DELETE_USER, UUID.randomUUID(), null)))
            .andExpect(status().isBadRequest());
}
```

### Try to run the test

The tests should all be passing now.

### Consume the endpoint

```java
curl -X DELETE \
  http://localhost:8080/v1/6bfd12c0-49a6-11eb-b378-0242ac130002/user/null \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 8b738d6c-4fcf-9e83-2b62-ec0cf08121c2'
```

```java
{
    "status": 400,
    "error": "Invalid UUID string: null",
    "timestamp": "2021-01-02T14:14:21Z",
    "path": "/v1/6bfd12c0-49a6-11eb-b378-0242ac130002/user/null",
    "message": "Bad Request"
}
```

The response returns a status and error message correctly.

## Write a new test

```java
@Test
void testDeleteUserWhenRequesterDoesNotExist() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .delete(String.format(DELETE_USER, UUID.randomUUID(), UUID.randomUUID())))
            .andExpect(status().isUnauthorized());
}
```

### Try to run the test

```java
[ERROR] testDeleteUserWhenRequesterDoesNotExist  Time elapsed: 0.017 s  <<< FAILURE!
java.lang.AssertionError: Status expected:<401> but was:<200>
        at com.atul.gitbook.learn.users.service.ControllerTest.testDeleteUserWhenRequesterDoesNotExist(ControllerTest.java:183)
```

### Let's try to resolve the failure

Let's modify the `deleteUser()` in `IUserService` to accept `requesterId` and call that method from `UserController`. Also, add the check to validate requester presence.

```java
@DeleteMapping("/v1/{requesterId}/user/{userId}")
public void deleteUser(@PathVariable("requesterId") UUID requesterId,
                       @PathVariable("userId") UUID userId) {
    fUserService.deleteUser(requesterId, userId);
}
```

```java
/**
 * Deletes the user.
 *
 * @param requesterId the userId of the user making the request.
 * @param id          userId of the user to be deleted.
 */
void deleteUser(UUID requesterId, UUID id);
```

```java
@Override
public void deleteUser(UUID requesterId, UUID id) {
    validateNotNull(id);
    try {
        fUserRepository.getUser(requesterId);
    } catch (NoSuchElementException e) {
        throw new UnauthorizedException("Requester is not present.");
    }
    fUserRepository.deleteUser(id);
}
```

###  Try to run the test

```java
[INFO] -------------------------------------------------------------
[ERROR] COMPILATION ERROR : 
[INFO] -------------------------------------------------------------
[ERROR] /Users/creations/IdeaProjects/rest-using-spring-boot/user-service/v10/src/test/java/com/atul/gitbook/learn/users/service/UserServiceTest.java:[106,83] method deleteUser in interface com.atul.gitbook.learn.users.service.IUserService cannot be applied to given types;
  required: java.util.UUID,java.util.UUID
  found: <nulltype>
  reason: actual and formal argument lists differ in length
[ERROR] /Users/creations/IdeaProjects/rest-using-spring-boot/user-service/v10/src/test/java/com/atul/gitbook/learn/users/service/UserServiceTest.java:[111,81] method deleteUser in interface com.atul.gitbook.learn.users.service.IUserService cannot be applied to given types;
  required: java.util.UUID,java.util.UUID
  found: java.util.UUID
  reason: actual and formal argument lists differ in length
[ERROR] /Users/creations/IdeaProjects/rest-using-spring-boot/user-service/v10/src/test/java/com/atul/gitbook/learn/users/service/UserServiceTest.java:[118,57] method deleteUser in interface com.atul.gitbook.learn.users.service.IUserService cannot be applied to given types;
  required: java.util.UUID,java.util.UUID
  found: java.util.UUID
  reason: actual and formal argument lists differ in length

```

The controller test passed but we broke some of the service tests.

### Let's try to resolve the failure

```java
@Test
void testDeleteUserWhenIdIsNull() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> fUserService.deleteUser(null, null));
}

@Test
void testDeleteUserWhenUserIsNotPresent() {
    Assertions.assertThrows(NoSuchElementException.class, () -> fUserService.deleteUser(ADMINISTRATOR.getId(), UUID.randomUUID()));
}

@Test
void testDeleteUser() {
    final var userDto = new UserDto("Ramsay", "9876483456", "abc@gmail.com");
    final var user = fUserService.createUser(ADMINISTRATOR.getId(), userDto);
    Assertions.assertDoesNotThrow(() -> fUserService.deleteUser(user.getId(), user.getId()));
    Assertions.assertThrows(NoSuchElementException.class, () -> fUserService.getUser(ADMINISTRATOR.getId(), user.getId()));
}
```

###  Try to run the test

The tests should all be passing now

### Consume the endpoint

```java
curl -X DELETE \
  http://localhost:8080/v1/6bfd12c0-49a6-11eb-b378-0242ac130002/user/6bfd12c0-49a6-11eb-b378-0242ac130002 \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 74f08301-15fd-8139-ba08-3cbea0840de6'
```

```java
{
    "status": 401,
    "error": "Requester is not present.",
    "timestamp": "2021-01-02T14:22:13Z",
    "path": "/v1/6bfd12c0-49a6-11eb-b378-0242ac130002/user/6bfd12c0-49a6-11eb-b378-0242ac130002",
    "message": "Unauthorized"
}
```

The response returns a status and error message correctly.

## Write a new test

```java
@Test
void testDeleteUserWhenRequesterIsDifferentFromUser() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .delete(String.format(DELETE_USER, USER.getId(), UUID.randomUUID())))
            .andExpect(status().isForbidden());
}
```

###  Try to run the test

```java
[ERROR] testDeleteUserWhenRequesterIsDifferentFromUser  Time elapsed: 0.022 s  <<< FAILURE!
java.lang.AssertionError: Status expected:<403> but was:<500>
        at com.atul.gitbook.learn.users.service.ControllerTest.testDeleteUserWhenRequesterIsDifferentFromUser(ControllerTest.java:190)
```

### Let's try to resolve the failure

Let's add the validation to ensure the user deletes own profile.

```text
@Override
public void deleteUser(UUID requesterId, UUID id) {
    validateNotNull(requesterId);
    validateNotNull(id);
    try {
        fUserRepository.getUser(requesterId);
    } catch (NoSuchElementException e) {
        throw new UnauthorizedException("Requester is not present.");
    }
    if (!requesterId.equals(id)) {
        throw new ForbiddenException("User can only delete own profile details.");
    }
    fUserRepository.deleteUser(id);
}
```

### Try to run the test

 The controller test passed but we broke some of the service tests.

### Let's try to resolve the failure

```java
@Test
void testDeleteUserWhenUserIsNotPresent() {
    Assertions.assertThrows(ForbiddenException.class, () -> fUserService.deleteUser(ADMINISTRATOR.getId(), UUID.randomUUID()));
}
```

### Try to run the test

The tests should all be passing now.

### Consume the endpoint

```java
curl -X DELETE \
  http://localhost:8080/v1/1109a8c8-49a3-4921-aa80-65e730d587fe/user/6bfd12c0-49a6-11eb-b378-0242ac130002 \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: d50a6822-3183-398e-9fd0-6c720f4f548f'
```

```java
{
    "status": 403,
    "error": "User can only delete own profile details.",
    "timestamp": "2021-01-02T14:28:48Z",
    "path": "/v1/1109a8c8-49a3-4921-aa80-65e730d587fe/user/6bfd12c0-49a6-11eb-b378-0242ac130002",
    "message": "Forbidden"
}
```

The response returns a status and error message correctly.

## Write a new test

```java
@Test
void testDeleteUserWhenRequestIsDeletingOwnProfile() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .delete(String.format(DELETE_USER, USER.getId(), USER.getId())))
            .andExpect(status().is2xxSuccessful());
}
```

### Try to run the test

```java
[ERROR] testGetUserWhenRequesterIsDifferentFromUser  Time elapsed: 0.013 s  <<< FAILURE!
java.lang.AssertionError: Status expected:<403> but was:<401>
        at com.atul.gitbook.learn.users.service.ControllerTest.testGetUserWhenRequesterIsDifferentFromUser(ControllerTest.java:103)

[ERROR] testGetUserWhenRequesterIsFetchingOwnDetails  Time elapsed: 0.005 s  <<< FAILURE!
java.lang.AssertionError: Range for response status value 401 expected:<SUCCESSFUL> but was:<CLIENT_ERROR>
        at com.atul.gitbook.learn.users.service.ControllerTest.testGetUserWhenRequesterIsFetchingOwnDetails(ControllerTest.java:111)

[ERROR] testCreateUserWhenRequesterExistsButIsNotAdministrator  Time elapsed: 0.006 s  <<< FAILURE!
java.lang.AssertionError: Status expected:<403> but was:<401>
        at com.atul.gitbook.learn.users.service.ControllerTest.testCreateUserWhenRequesterExistsButIsNotAdministrator(ControllerTest.java:61)

[ERROR] testDeleteUserWhenRequesterIsDifferentFromUser  Time elapsed: 0.004 s  <<< FAILURE!
java.lang.AssertionError: Status expected:<403> but was:<401>
        at com.atul.gitbook.learn.users.service.ControllerTest.testDeleteUserWhenRequesterIsDifferentFromUser(ControllerTest.java:190)

[ERROR] testUpdateUserWhenRequestIsDifferentFromUser  Time elapsed: 0.004 s  <<< FAILURE!
java.lang.AssertionError: Status expected:<403> but was:<401>
        at com.atul.gitbook.learn.users.service.ControllerTest.testUpdateUserWhenRequestIsDifferentFromUser(ControllerTest.java:153)

[ERROR] testUpdateUserWhenRequesterIsUpdatingOwnDetails  Time elapsed: 0.006 s  <<< FAILURE!
java.lang.AssertionError: Range for response status value 401 expected:<SUCCESSFUL> but was:<CLIENT_ERROR>
        at com.atul.gitbook.learn.users.service.ControllerTest.testUpdateUserWhenRequesterIsUpdatingOwnDetails(ControllerTest.java:162)
```

Quite a few of the tests are failing now. 

### Let's try to resolve the failure

 We've deleted the stored `USER` object from `InMemoryRepository` which is required to pass other Controller tests. 

In the controller we have tested all the method independently till now. We can create a user in the delete test and try deleting that itself. 

```java
@Test
void testDeleteUserWhenRequestIsDeletingOwnProfile() throws Exception {
    final var user = fUserService.createUser(ADMINISTRATOR.getId(), new UserDto("Mike Selby", "8765436548", "selby@mark.com"));
    fMockMvc.perform(MockMvcRequestBuilders
            .delete(String.format(DELETE_USER, user.getId(), user.getId())))
            .andExpect(status().is2xxSuccessful());
}
```

### Try to run the test

The tests should all be passing now.

### Consume the endpoint

```java
curl -X DELETE \
  http://localhost:8080/v1/1109a8c8-49a3-4921-aa80-65e730d587fe/user/1109a8c8-49a3-4921-aa80-65e730d587fe \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 1751f2b7-b049-0b70-109f-8aa4b50ba0a5'
```

This gives a OK\(200\) in the response. Works as expected.

## Project Status

We have successfully written tests for the User CRUD controllers now. We have handled several restrictions for each method and the tests can now act as spec sheet of the service.

Although, there is a lot of repetitive code in our codebase, we're confident that our tests handle everything. We can look for refactoring them now. 

The UserServiceTest is now redundant as we have everything covered in the ControllerTest. You can remove it if you'd like. I will be removing it.

In the next chapter, we'll look into refactoring the whole codebase with the help of our tests. This maybe a long exercise and can give you insights on how to write tests and production code by taking a few jumps beforehand and have a cleaner and concise codebase in the first go itself.

