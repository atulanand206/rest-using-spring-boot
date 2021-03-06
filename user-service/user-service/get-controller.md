# Get User Details Endpoint

{% api-method method="get" host="https://api.content.com" path="/v1/{requesterId}/user/{userId}" %}
{% api-method-summary %}
Get user details
{% endapi-method-summary %}

{% api-method-description %}
This endpoint allows you to fetch user details. A user can only fetch his/her own details. 
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
User successfully retrieved.
{% endapi-method-response-example-description %}

```javascript
{
    "name": "David Marshal",
    "id": "1109a8c8-49a3-4921-aa80-65e730d587fe",
    "phone": "9999999999",
    "email": "david@marshall.com",
    "administrator": false
}
```
{% endapi-method-response-example %}

{% api-method-response-example httpCode=400 %}
{% api-method-response-example-description %}

{% endapi-method-response-example-description %}

```java
{
    "status": 400,
    "error": "Invalid UUID string: null",
    "timestamp": "2021-01-02T10:33:51Z",
    "path": "/v1/null/user/null",
    "message": "Bad Request"
}
```
{% endapi-method-response-example %}

{% api-method-response-example httpCode=401 %}
{% api-method-response-example-description %}
When requester is not present.
{% endapi-method-response-example-description %}

```javascript
{
    "status": 401,
    "error": "Requester is not present.",
    "timestamp": "2021-01-02T10:50:27Z",
    "path": "/v1/6bfd12c0-49a6-11eb-b378-0242ac130002/user/6bfd12c0-49a6-11eb-b378-0242ac130002",
    "message": "Unauthorized"
}
```
{% endapi-method-response-example %}

{% api-method-response-example httpCode=403 %}
{% api-method-response-example-description %}

{% endapi-method-response-example-description %}

```java
{
    "status": 403,
    "error": "User can only request own profile details.",
    "timestamp": "2021-01-02T10:59:06Z",
    "path": "/v1/f994c61d-ebd1-463c-a8d8-ebe5989aa501/user/6bfd12c0-49a6-11eb-b378-0242ac130002",
    "message": "Forbidden"
}
```
{% endapi-method-response-example %}
{% endapi-method-response %}
{% endapi-method-spec %}
{% endapi-method %}

## Write a new test

We will again begin our journey by handling all the negative cases first before moving on to making the endpoint succeed. 

For writing a web services test, we will be initialising and request with a particular endpoint and using MockMvc to perform the request and expect a desired response status.

```java
private static final String GET_USERS = "/v1/%s/user/%s";

@Test
void testGetUserWhenRequesterIsNull() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .get(String.format(GET_USER, null, null))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isBadRequest());
}
```

###  Try to run the test

By this time you must have figured out that we can simply use `mvn clean install` which builds the jar and runs all the tests for us without running the `mvn test` command. If you haven't, now you know it.

```java
[ERROR] Tests run: 5, Failures: 1, Errors: 0, Skipped: 0, Time elapsed: 0.24 s <<< FAILURE! - in com.atul.gitbook.learn.users.service.ControllerTest
[ERROR] testGetUserWhenRequesterIsNull  Time elapsed: 0.056 s  <<< FAILURE!
java.lang.AssertionError: Status expected:<400> but was:<404>
        at com.atul.gitbook.learn.users.service.ControllerTest.testGetUserWhenRequesterIsNull(ControllerTest.java:76)
```

The failure mentions that 404 is returned after hitting the endpoint, viz., the test was not able to find the endpoint anywhere in the service.

### Let's try to resolve the failure

Let's add a method to the UserController which will act on a GET request with the decided endpoint.

```java
@RestController
public class UserController {

    ...

    @GetMapping("/v1/{requesterId}/user/{userId}")
    public User getUser(@PathVariable("requesterId") UUID requesterId,
                        @PathVariable("userId") UUID userId) {
        return null;
    }
}
```

We have mentioned the type of the parameters as UUID. Any request with a malformed UUID will not reach the controller method.

### Try to run the test

If you'd run the test, they will all be passing now.

### Consume the endpoint

```java
curl -X GET \
  http://localhost:8080/v1/null/user/null \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 1397f3fd-41eb-0ba1-b6df-43cdbffa1e0c'
```

```java
{
    "status": 400,
    "error": "Invalid UUID string: null",
    "timestamp": "2021-01-02T10:33:51Z",
    "path": "/v1/null/user/null",
    "message": "Bad Request"
}
```

This request is giving correct status code and error message.

## Write a new test

```java
@Test
void testGetUserWhenUserIdIsNull() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .get(String.format(GET_USER, UUID.randomUUID(), null))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isBadRequest());
}
```

### Try to run the test

The tests should all be passing without any code changes.

### Consume the endpoint

```java
curl -X GET \
  http://localhost:8080/v1/6bfd12c0-49a6-11eb-b378-0242ac130002/users/null \
  -H 'cache-control: no-cache' \
  -H 'postman-token: 9235f2ef-9ad3-859f-cc3d-efe315b9e02f'
```

```java
{
    "status": 400,
    "error": "Invalid UUID string: null",
    "timestamp": "2021-01-02T10:36:16Z",
    "path": "/v1/6bfd12c0-49a6-11eb-b378-0242ac130002/user/null",
    "message": "Bad Request"
}
```

The status code as well as the error message clearly conveys the cause of failure.

## Write a new test

We must check whether the requester exists in the system before further processing.

```java
@Test
void testGetUserWhenRequesterDoesNotExist() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .get(String.format(GET_USER, UUID.randomUUID(), UUID.randomUUID()))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
}
```

### Try to run the test

```java
[ERROR] Tests run: 7, Failures: 1, Errors: 0, Skipped: 0, Time elapsed: 0.219 s <<< FAILURE! - in com.atul.gitbook.learn.users.service.ControllerTest
[ERROR] testGetUserWhenRequesterDoesNotExist  Time elapsed: 0.012 s  <<< FAILURE!
java.lang.AssertionError: Status expected:<401> but was:<200>
        at com.atul.gitbook.learn.users.service.ControllerTest.testGetUserWhenRequesterDoesNotExist(ControllerTest.java:92)
```

### Let's try to resolve the failure

We can modify the getUser method in the `UserService` to take in requesterId along with userId and call that from the controller.

```java
@RestController
public class UserController {

    @GetMapping("/v1/{requesterId}/users/{userId}")
    public User getUser(@PathVariable("requesterId") UUID requesterId,
                        @PathVariable("userId") UUID userId) {
        return fUserService.getUser(requesterId, userId);
    }
}
```

```java
/**
 * Returns the user with the provided userId.
 *
 * @param requesterId the userId of the user making the request.
 * @param userId the userId of the user being queried.
 */
User getUser(UUID requesterId, UUID userId);
```

```java
@Override
public User getUser(UUID requesterId, UUID userId) {
    validateNotNull(requesterId);
    validateNotNull(userId);
    return fUserRepository.getUser(userId);
}
```

### Try to run the test

```java
[INFO] -------------------------------------------------------------
[ERROR] COMPILATION ERROR : 
[INFO] -------------------------------------------------------------
[ERROR] /Users/creations/IdeaProjects/rest-using-spring-boot/user-service/v8/src/test/java/com/atul/gitbook/learn/users/service/UserServiceTest.java:[45,83] method getUser in interface com.atul.gitbook.learn.users.service.IUserService cannot be applied to given types;
  required: java.util.UUID,java.util.UUID
  found: <nulltype>
  reason: actual and formal argument lists differ in length
[ERROR] /Users/creations/IdeaProjects/rest-using-spring-boot/user-service/v8/src/test/java/com/atul/gitbook/learn/users/service/UserServiceTest.java:[50,81] method getUser in interface com.atul.gitbook.learn.users.service.IUserService cannot be applied to given types;
  required: java.util.UUID,java.util.UUID
  found: java.util.UUID
  reason: actual and formal argument lists differ in length
[ERROR] /Users/creations/IdeaProjects/rest-using-spring-boot/user-service/v8/src/test/java/com/atul/gitbook/learn/users/service/UserServiceTest.java:[57,40] method getUser in interface com.atul.gitbook.learn.users.service.IUserService cannot be applied to given types;
  required: java.util.UUID,java.util.UUID
  found: java.util.UUID
  reason: actual and formal argument lists differ in length
[ERROR] /Users/creations/IdeaProjects/rest-using-spring-boot/user-service/v8/src/test/java/com/atul/gitbook/learn/users/service/UserServiceTest.java:[92,40] method getUser in interface com.atul.gitbook.learn.users.service.IUserService cannot be applied to given types;
  required: java.util.UUID,java.util.UUID
  found: java.util.UUID
  reason: actual and formal argument lists differ in length
[ERROR] /Users/creations/IdeaProjects/rest-using-spring-boot/user-service/v8/src/test/java/com/atul/gitbook/learn/users/service/UserServiceTest.java:[114,81] method getUser in interface com.atul.gitbook.learn.users.service.IUserService cannot be applied to given types;
  required: java.util.UUID,java.util.UUID
  found: java.util.UUID
  reason: actual and formal argument lists differ in length
```

You'll see that a bunch of tests failed. As we modified the service method, the tests which were calling the `getUser` method failed. For simplicity, you can pass the userId of the user whose details are being retrieved.

### Let's try to resolve the failure

```java
class UserServiceTest extends TestBase{

    ...
    
    @Test
    void testGetUserWhenIdIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> fUserService.getUser(null, null));
    }

    @Test
    void testGetUserWhenUserIsNotPresent() {
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            final var id = UUID.randomUUID();
            fUserService.getUser(id, id);
        });
    }

    @Test
    void testGetUser() {
        final var userDto = new UserDto("Ramsay", "9876483456", "abc@gmail.com");
        final var expected = fUserService.createUser(ADMINISTRATOR.getId(), userDto);
        final var actual = fUserService.getUser(expected.getId(), expected.getId());
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getPhone(), actual.getPhone());
        Assertions.assertEquals(expected.getEmail(), actual.getEmail());
    }

    @Test
    void testUpdateUser() {
        final var userDto = new UserDto("Ramsay", "9876483456", "abc@gmail.com");
        final var user = fUserService.createUser(ADMINISTRATOR.getId(), userDto);
        final var newName = "Abc";
        final var newPhone = "7583929275";
        final var newEmail = "rewr@afsa.com";
        final var newUserDto = new UserDto(newName, newPhone, newEmail);
        fUserService.updateUser(user.getId(), newUserDto);
        final var actual = fUserService.getUser(user.getId(), user.getId());
        Assertions.assertEquals(user.getId(), actual.getId());
        Assertions.assertEquals(newName, actual.getName());
        Assertions.assertEquals(newPhone, actual.getPhone());
        Assertions.assertEquals(newEmail, actual.getEmail());
    }

    @Test
    void testDeleteUser() {
        final var userDto = new UserDto("Ramsay", "9876483456", "abc@gmail.com");
        final var user = fUserService.createUser(ADMINISTRATOR.getId(), userDto);
        Assertions.assertDoesNotThrow(() -> fUserService.deleteUser(user.getId()));
        Assertions.assertThrows(NoSuchElementException.class, () -> fUserService.getUser(user.getId(), user.getId()));
    }
}
```

###  Try to run the test

Eventually, we will be in a position to delete the `UserServiceTest` class as the `UserControllerTest` will be performing the same function and it would seem redundant. As our service will have many endpoints and the only way to interact with the system would be using the controller, we will no longer need to test the internal components. The behavioural testing should be able to cover our needs. For now, let's not get ahead of us though. Let's fix the tests and move forward.Try to run the test

```java
[ERROR] testGetUserWhenRequesterDoesNotExist  Time elapsed: 0.012 s  <<< FAILURE!
java.lang.AssertionError: Status expected:<401> but was:<500>
        at com.atul.gitbook.learn.users.service.ControllerTest.testGetUserWhenRequesterDoesNotExist(ControllerTest.java:92)
```

### Let's try to resolve the failure

Let's validate the presence of requester and throw the exception otherwise.

```java
@Override
public User getUser(UUID requesterId, UUID userId) {
    validateNotNull(requesterId);
    validateNotNull(userId);
    try {
        fUserRepository.getUser(requesterId);
    } catch (NoSuchElementException e) {
        throw new UnauthorizedException("Requester is not present.");
    }
    return fUserRepository.getUser(userId);
}
```

### Try to run the test

The Controller tests should all be passing now. But, we broke a couple of Service Tests.

```java
[ERROR] testGetUserWhenUserIsNotPresent  Time elapsed: 0.011 s  <<< FAILURE!
org.opentest4j.AssertionFailedError: Unexpected exception type thrown ==> expected: <java.util.NoSuchElementException> but was: <com.atul.gitbook.learn.exceptions.UnauthorizedException>
        at com.atul.gitbook.learn.users.service.UserServiceTest.testGetUserWhenUserIsNotPresent(UserServiceTest.java:50)
Caused by: com.atul.gitbook.learn.exceptions.UnauthorizedException: Requester is not present.
        at com.atul.gitbook.learn.users.service.UserServiceTest.lambda$testGetUserWhenUserIsNotPresent$3(UserServiceTest.java:52)
        at com.atul.gitbook.learn.users.service.UserServiceTest.testGetUserWhenUserIsNotPresent(UserServiceTest.java:50)

[ERROR] testDeleteUser  Time elapsed: 0.003 s  <<< FAILURE!
org.opentest4j.AssertionFailedError: Unexpected exception type thrown ==> expected: <java.util.NoSuchElementException> but was: <com.atul.gitbook.learn.exceptions.UnauthorizedException>
        at com.atul.gitbook.learn.users.service.UserServiceTest.testDeleteUser(UserServiceTest.java:117)
Caused by: com.atul.gitbook.learn.exceptions.UnauthorizedException: Requester is not present.
        at com.atul.gitbook.learn.users.service.UserServiceTest.lambda$testDeleteUser$9(UserServiceTest.java:117)
        at com.atul.gitbook.learn.users.service.UserServiceTest.testDeleteUser(UserServiceTest.java:117)
```

### Let's try to resolve the failure

```java
@Test
void testGetUserWhenUserIsNotPresent() {
    Assertions.assertThrows(NoSuchElementException.class, () -> {
        fUserService.getUser(ADMINISTRATOR.getId(), UUID.randomUUID());
    });
}

@Test
void testDeleteUser() {
    final var userDto = new UserDto("Ramsay", "9876483456", "abc@gmail.com");
    final var user = fUserService.createUser(ADMINISTRATOR.getId(), userDto);
    Assertions.assertDoesNotThrow(() -> fUserService.deleteUser(user.getId()));
    Assertions.assertThrows(NoSuchElementException.class, () -> fUserService.getUser(ADMINISTRATOR.getId(), user.getId()));
}
```

### Try to run the test

The tests should all be passing now.

### Consume the endpoint

```java
curl -X GET \
  http://localhost:8080/v1/6bfd12c0-49a6-11eb-b378-0242ac130002/user/6bfd12c0-49a6-11eb-b378-0242ac130002 \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: ba3d2ca8-f907-5d48-d025-0f0979a2e065'
```

```java
{
    "status": 401,
    "error": "Requester is not present.",
    "timestamp": "2021-01-02T10:50:27Z",
    "path": "/v1/6bfd12c0-49a6-11eb-b378-0242ac130002/user/6bfd12c0-49a6-11eb-b378-0242ac130002",
    "message": "Unauthorized"
}
```

The request responds with correct error message and status code.

## Write a new test

```java
@Test
void testGetUserWhenRequesterIsDifferentFromUser() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .get(String.format(GET_USER, USER.getId(), UUID.randomUUID()))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isForbidden());
}
```

### Try to run the test

```java
[ERROR] testGetUserWhenRequesterIsDifferentFromUser  Time elapsed: 0.012 s  <<< FAILURE!
java.lang.AssertionError: Status expected:<403> but was:<500>
        at com.atul.gitbook.learn.users.service.ControllerTest.testGetUserWhenRequesterIsDifferentFromUser(ControllerTest.java:100)
```

### Let's try to resolve the failure

```java
@Override
public User getUser(UUID requesterId, UUID userId) {
    validateNotNull(requesterId);
    validateNotNull(userId);
    try {
        fUserRepository.getUser(requesterId);
    } catch (NoSuchElementException e) {
        throw new UnauthorizedException("Requester is not present.");
    }
    if (!requesterId.equals(userId)) {
        throw new ForbiddenException("User can only request own profile details.");
    }
    return fUserRepository.getUser(userId);
}
```

### Try to run the test

The Controller tests should all be passing now. But, we broke a couple of Service Tests.

### Let's try to resolve the failure

```java
@Test
void testGetUserWhenUserIsNotPresent() {
    Assertions.assertThrows(ForbiddenException.class, () -> {
        fUserService.getUser(ADMINISTRATOR.getId(), UUID.randomUUID());
    });
}

@Test
void testDeleteUser() {
    final var userDto = new UserDto("Ramsay", "9876483456", "abc@gmail.com");
    final var user = fUserService.createUser(ADMINISTRATOR.getId(), userDto);
    Assertions.assertDoesNotThrow(() -> fUserService.deleteUser(user.getId()));
    Assertions.assertThrows(ForbiddenException.class, () -> fUserService.getUser(ADMINISTRATOR.getId(), user.getId()));
}
```

###  Try to run the test

The tests should all be passing now.

### Consume the endpoint

```java
curl -X GET \
  http://localhost:8080/v1/f994c61d-ebd1-463c-a8d8-ebe5989aa501/user/6bfd12c0-49a6-11eb-b378-0242ac130002 \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 23d56836-9995-fb83-f41c-eda3abbe2ba2'
```

```java
{
    "status": 403,
    "error": "User can only request own profile details.",
    "timestamp": "2021-01-02T10:59:06Z",
    "path": "/v1/f994c61d-ebd1-463c-a8d8-ebe5989aa501/user/6bfd12c0-49a6-11eb-b378-0242ac130002",
    "message": "Forbidden"
}
```

The endpoint is responding with correct error message and status code.

## Write a new test

```java
@Test
void testGetUserWhenRequesterIsFetchingOwnDetails() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .get(String.format(GET_USER, USER.getId(), USER.getId()))
            .contentType(APPLICATION_JSON))
            .andExpect(status().is2xxSuccessful());
}
```

### Try to run the test

The tests should all be passing without any code changes.

### Consume the endpoint

```java
curl -X GET \
  http://localhost:8080/v1/1109a8c8-49a3-4921-aa80-65e730d587fe/user/1109a8c8-49a3-4921-aa80-65e730d587fe \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 6995f8fa-8c4f-3316-786a-da02c6a0bf01'
```

```java
{
    "name": "David Marshal",
    "id": "1109a8c8-49a3-4921-aa80-65e730d587fe",
    "phone": "9999999999",
    "email": "david@marshall.com",
    "administrator": false
}
```

Endpoint working as expected.

## Write a new test

An administrator should be able to fetch anyone's profile.

```text
@Test
void testGetUserWhenRequesterIsAdministratorAndUserIsPresent() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .get(String.format(GET_USER, ADMINISTRATOR.getId(), USER.getId()))
            .contentType(APPLICATION_JSON))
            .andExpect(status().is2xxSuccessful());
}
```

### Try to run the test

```text
[ERROR] testGetUserWhenRequesterIsAdministratorAndUserIsPresent  Time elapsed: 0.022 s  <<< FAILURE!
java.lang.AssertionError: Range for response status value 403 expected:<SUCCESSFUL> but was:<CLIENT_ERROR>
        at com.atul.gitbook.learn.users.service.ControllerTest.testGetUserWhenRequesterIsAdministratorAndUserIsPresent(ControllerTest.java:116)
```

### Let's try to resolve the failure

```java
@Override
public User getUser(UUID requesterId, UUID userId) {
    validateNotNull(requesterId);
    validateNotNull(userId);
    User requester;
    try {
        requester = fUserRepository.getUser(requesterId);
    } catch (NoSuchElementException e) {
        throw new UnauthorizedException("Requester is not present.");
    }
    if (requester.isAdministrator()) {
        return fUserRepository.getUser(userId);
    }
    if (!requesterId.equals(userId)) {
        throw new ForbiddenException("User can only request own profile details.");
    }
    return fUserRepository.getUser(userId);
}
```

### Try to run the test

 The Controller tests should all be passing now. But, we broke a couple of Service Tests.

### Let's try to resolve the failure

```java
@Test
void testGetUserWhenUserIsNotPresent() {
    Assertions.assertThrows(NoSuchElementException.class, () -> {
        fUserService.getUser(ADMINISTRATOR.getId(), UUID.randomUUID());
    });
}

@Test
void testDeleteUser() {
    final var userDto = new UserDto("Ramsay", "9876483456", "abc@gmail.com");
    final var user = fUserService.createUser(ADMINISTRATOR.getId(), userDto);
    Assertions.assertDoesNotThrow(() -> fUserService.deleteUser(user.getId()));
    Assertions.assertThrows(NoSuchElementException.class, () -> fUserService.getUser(ADMINISTRATOR.getId(), user.getId()));
}Try to run the test
```

The tests should all be passing now.

### Consume the endpoint

```java
curl -X GET \
  http://localhost:8080/v1/f994c61d-ebd1-463c-a8d8-ebe5989aa501/user/1109a8c8-49a3-4921-aa80-65e730d587fe \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 92fabe30-6e19-3452-1a88-64fbaec4457f'
```

```java
{
    "name": "David Marshal",
    "id": "1109a8c8-49a3-4921-aa80-65e730d587fe",
    "phone": "9999999999",
    "email": "david@marshall.com",
    "administrator": false
}
```

Endpoint working as expected.

 

 

 

 

 

 



 

 

 

 

 

 

 

