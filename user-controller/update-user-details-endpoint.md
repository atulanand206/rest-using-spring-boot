# Update User Details Endpoint

{% api-method method="put" host="https://api.content.com" path="/v1/{requesterId}/users/{userId}" %}
{% api-method-summary %}
Update user details
{% endapi-method-summary %}

{% api-method-description %}
A requester can pass in their own userId and update their profile details. 
{% endapi-method-description %}

{% api-method-spec %}
{% api-method-request %}
{% api-method-path-parameters %}
{% api-method-parameter name="userId" type="string" required=false %}
Id of the user whose profile is being updated.
{% endapi-method-parameter %}

{% api-method-parameter name="requesterId" type="string" required=false %}
Id of the requester
{% endapi-method-parameter %}
{% endapi-method-path-parameters %}

{% api-method-body-parameters %}
{% api-method-parameter name="name" type="string" required=false %}
name of the user 
{% endapi-method-parameter %}

{% api-method-parameter name="phone" type="string" required=false %}
phone of the user
{% endapi-method-parameter %}

{% api-method-parameter name="email" type="string" required=false %}
email of the user
{% endapi-method-parameter %}
{% endapi-method-body-parameters %}
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
private static final String UPDATE_USER = "/v1/%s/user/%s";

@Test
void testUpdateUserWhenRequesterIsNull() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .put(String.format(UPDATE_USER, null, null))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isBadRequest());
}
```

### Try to run the test

```java
[ERROR] testUpdateUserWhenRequesterIsNull  Time elapsed: 0.012 s  <<< FAILURE!
java.lang.AssertionError: Status expected:<400> but was:<405>
        at com.atul.gitbook.learn.users.service.ControllerTest.testUpdateUserWhenRequesterIsNull(ControllerTest.java:125)
```

### Let's try to resolve the failure

Let's add a PUT method in the UserController for updating user details. We can't use the UserDto as RequestBody because it has the fAdministrator field and that can't be updated. We'd need another DTO here. Let's define UpdateUserDto as well.

```java
public class UpdateUserDto {

    @JsonProperty("name")
    private String fName;

    @JsonProperty("phone")
    private String fPhone;

    @JsonProperty("email")
    private String fEmail;

    public UpdateUserDto() {
    }

    /**
     * @param name  the name of the user.
     * @param phone the phone number of the user.
     * @param email the email of the user.
     * @throws IllegalArgumentException in any of the following fails to be conformant:
     *                                  1. Phone number : Must be 10 characters in length and all numeric.
     *                                  2. Email : Must contain @ in the middle of the string.
     */
    public UpdateUserDto(String name, String phone, String email) throws IllegalArgumentException {
        validateNotNull(name);
        validateNotNull(phone);
        validateNotNull(email);
        validatePhoneNumber(phone);
        validateEmail(email);
        fName = name;
        fPhone = phone;
        fEmail = email;
    }

    public String getName() {
        return fName;
    }

    public String getPhone() {
        return fPhone;
    }

    public String getEmail() {
        return fEmail;
    }

    @Override
    public String toString() {
        return "{\n"
                + "\"name\" : \"" + fName + "\",\n"
                + "\"phone\" : \"" + fPhone + "\",\n"
                + "\"email\" : \"" + fEmail + "\"\n"
                + "}";
    }
}
```

I have not worked on the UpdateUserDto Tests as it's pretty much a copy of UserDto and I'm confident that nothing needs to be tested additionally. I'd recommend you to write those tests as well.

```java
@PutMapping("/v1/{requesterId}/user/{userId}")
public User updateUser(@PathVariable("requesterId") UUID requesterId,
                       @PathVariable("userId") UUID userId,
                       @RequestBody UpdateUserDto userDto) {
    return null;
}
```

### Try to run the test

The tests should be passing now.

### Consume the endpoint

```java
curl -X PUT \
  http://localhost:8080/v1/null/user/null \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 6d362d08-7f86-f18c-ad66-acb34f0a34a5'
```

```java
{
    "status": 400,
    "error": "Invalid UUID string: null",
    "timestamp": "2021-01-02T12:19:35Z",
    "path": "/v1/null/user/null",
    "message": "Bad Request"
}
```

The response returns expected status and error.

## Write a new test

```java
@Test
void testUpdateUserWhenUserIdIsNull() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .put(String.format(UPDATE_USER, UUID.randomUUID(), null))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isBadRequest());
}
```

### Try to run the test

The tests should be passing without any code changes.

### Consume the endpoint

```java
curl -X PUT \
  http://localhost:8080/v1/6bfd12c0-49a6-11eb-b378-0242ac130002/user/null \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 8e2775ab-2954-6614-a95b-23a16e95c4fe'
```

```java
{
    "status": 400,
    "error": "Invalid UUID string: null",
    "timestamp": "2021-01-02T12:21:37Z",
    "path": "/v1/6bfd12c0-49a6-11eb-b378-0242ac130002/user/null",
    "message": "Bad Request"
}
```

The response returns expected status and error.

## Write a new test

```java
@Test
void testUpdateUserWhenRequesterDoesNotExist() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .get(String.format(UPDATE_USER, UUID.randomUUID(), UUID.randomUUID()))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
}
```

### Try to run the test

```java
[ERROR] testUpdateUserWhenRequesterDoesNotExist  Time elapsed: 0.015 s  <<< FAILURE!
java.lang.AssertionError: Status expected:<401> but was:<400>
        at com.atul.gitbook.learn.users.service.ControllerTest.testUpdateUserWhenRequesterDoesNotExist(ControllerTest.java:141)
```

### Let's try to resolve the failure

Let's modify the `updateUser` method in `IUserService` and call it from `UserController` . The `userDto` would now be an instance of `UpdateUserDto` . As we are returning the updated `User` in the controller, the methods in service would also be making a get call to the repository to return the updated `User` object. I made the series of changes in one go here. You can keep running the tests in between to see what needs to change if you are comfortable that way. But, these are simple enough changes to not require a walkthrough.

```java
@PutMapping("/v1/{requesterId}/user/{userId}")
public User updateUser(@PathVariable("requesterId") UUID requesterId,
                       @PathVariable("userId") UUID userId,
                       @RequestBody UpdateUserDto userDto) {
    return fUserService.updateUser(requesterId, userId, userDto);
}
```

```java
public interface IUserService {

...
    /**
     * Updates the user.
     *
     * @param requesterId the userId of the user making the request.
     * @param id userId of the user being queried.
     * @param userDto contains the new information for the User.
     */
    User updateUser(UUID requesterId, UUID id, UpdateUserDto userDto);

...
}
```

```java
public class UserService implements IUserService {

    ...    
    @Override
    public User updateUser(UUID requesterId, UUID id, UpdateUserDto userDto) {
        validateNotNull(requesterId);
        validateNotNull(id);
        validateNotNull(userDto);
        fUserRepository.updateUser(id, userDto);
        return fUserRepository.getUser(id);
    }
    ...
}
```

```java
public interface IUserRepository {
...
    /**
     * Updates the user.
     *
     * @param id userId of the user being queried.
     * @param userDto contains the new information for the User.
     */
    void updateUser(UUID id, UpdateUserDto userDto);
...
} 
```

```java
public class InMemoryRepository implements IUserRepository {
...    
    @Override
    public void updateUser(UUID id, UpdateUserDto userDto) {
        validateNotNull(id);
        validateNotNull(userDto);
        for (var i = 0; i < users.size(); i++) {
            final var user = users.get(i);
            if (user.getId().equals(id)) {
                users.set(i, User.with(userDto, user));
                return;
            }
        }
        throw new NoSuchElementException();
    }
...
}
```

A new `User.with` method is added as the `UpdateUserDto` does not have the `fAdministrator` field and the data entry has to be done with the whole object.

```java
public static User with(UpdateUserDto userDto, User user) {
    return new User(user.getId(), userDto.getName(), userDto.getPhone(), userDto.getEmail(), user.isAdministrator());
}
```

### Try to run the test

```java
[INFO] -------------------------------------------------------------
[ERROR] COMPILATION ERROR : 
[INFO] -------------------------------------------------------------
[ERROR] /Users/creations/IdeaProjects/rest-using-spring-boot/user-service/v9/src/test/java/com/atul/gitbook/learn/users/service/UserServiceTest.java:[69,83] method updateUser in interface com.atul.gitbook.learn.users.service.IUserService cannot be applied to given types;
  required: java.util.UUID,java.util.UUID,com.atul.gitbook.learn.users.models.UpdateUserDto
  found: java.util.UUID,com.atul.gitbook.learn.users.models.UserDto
  reason: actual and formal argument lists differ in length
[ERROR] /Users/creations/IdeaProjects/rest-using-spring-boot/user-service/v9/src/test/java/com/atul/gitbook/learn/users/service/UserServiceTest.java:[82,81] method updateUser in interface com.atul.gitbook.learn.users.service.IUserService cannot be applied to given types;
  required: java.util.UUID,java.util.UUID,com.atul.gitbook.learn.users.models.UpdateUserDto
  found: java.util.UUID,com.atul.gitbook.learn.users.models.UserDto
  reason: actual and formal argument lists differ in length
[ERROR] /Users/creations/IdeaProjects/rest-using-spring-boot/user-service/v9/src/test/java/com/atul/gitbook/learn/users/service/UserServiceTest.java:[93,21] method updateUser in interface com.atul.gitbook.learn.users.service.IUserService cannot be applied to given types;
  required: java.util.UUID,java.util.UUID,com.atul.gitbook.learn.users.models.UpdateUserDto
  found: java.util.UUID,com.atul.gitbook.learn.users.models.UserDto
  reason: actual and formal argument lists differ in length

```

We broke some service tests when we changed the parameter from UserDto to UpdateUserDto.

### Let's try to resolve the failure

```java
@ParameterizedTest
@MethodSource("streamForUpdateUserWhenParametersAreNull")
void testUpdateUserWhenParametersAreNull(UUID requesterId, UUID userId, UpdateUserDto userDto) {
    Assertions.assertThrows(IllegalArgumentException.class, () -> fUserService.updateUser(requesterId, userId, userDto));
}

private static Stream<Arguments> streamForUpdateUserWhenParametersAreNull() {
    return Stream.of(
            Arguments.of(null, null, null),
            Arguments.of(null, null, new UpdateUserDto("Rachel", "9876543214", "abc@def.com")),
            Arguments.of(null, UUID.randomUUID(), null),
            Arguments.of(UUID.randomUUID(), null, null)
    );
}

@Test
void testUpdateUserWhenUserIsNotPresent() {
    Assertions.assertThrows(NoSuchElementException.class, () -> fUserService.updateUser(UUID.randomUUID(), UUID.randomUUID(), new UpdateUserDto("Rachel", "9876543214", "abc@def.com")));
}

@Test
void testUpdateUser() {
    final var userDto = new UserDto("Ramsay", "9876483456", "abc@gmail.com");
    final var user = fUserService.createUser(ADMINISTRATOR.getId(), userDto);
    final var newName = "Abc";
    final var newPhone = "7583929275";
    final var newEmail = "rewr@afsa.com";
    final var newUserDto = new UpdateUserDto(newName, newPhone, newEmail);
    fUserService.updateUser(user.getId(), user.getId(), newUserDto);
    final var actual = fUserService.getUser(user.getId(), user.getId());
    Assertions.assertEquals(user.getId(), actual.getId());
    Assertions.assertEquals(newName, actual.getName());
    Assertions.assertEquals(newPhone, actual.getPhone());
    Assertions.assertEquals(newEmail, actual.getEmail());
}
```

### Try to run the test

```java
[ERROR] testUpdateUserWhenRequesterDoesNotExist  Time elapsed: 0.013 s  <<< FAILURE!
java.lang.AssertionError: Status expected:<401> but was:<400>
        at com.atul.gitbook.learn.users.service.ControllerTest.testUpdateUserWhenRequesterDoesNotExist(ControllerTest.java:141)
```

### Let's try to resolve the failure

Let's add the check in `UserService` to ensure requester exists before we can proceed.

```java
@Override
public User updateUser(UUID requesterId, UUID id, UpdateUserDto userDto) {
    validateNotNull(requesterId);
    validateNotNull(id);
    validateNotNull(userDto);
    try {
        fUserRepository.getUser(requesterId);
    } catch (NoSuchElementException e) {
        throw new UnauthorizedException("Requester is not present.");
    }
    fUserRepository.updateUser(id, userDto);
    return fUserRepository.getUser(id);
}
```

### Try to run the test

The controller test passed but we broke one of the service test. 

### Let's try to resolve the failure

```java
@Test
void testUpdateUserWhenUserIsNotPresent() {
    Assertions.assertThrows(UnauthorizedException.class, () -> fUserService.updateUser(UUID.randomUUID(), UUID.randomUUID(), new UpdateUserDto("Rachel", "9876543214", "abc@def.com")));
}
```

### Try to run the test

The tests should be passing now.

### Consume the endpoint

```java
curl -X PUT \
  http://localhost:8080/v1/6bfd12c0-49a6-11eb-b378-0242ac130002/user/6bfd12c0-49a6-11eb-b378-0242ac130002 \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 38e16e3a-634e-6639-4538-81b7e76f34f3' \
  -d '{
	"name": "Mark Allen",
	"phone": "5378574234",
	"email": "mark@allen.io"
}'
```

```java
{
    "status": 401,
    "error": "Requester is not present.",
    "timestamp": "2021-01-02T12:49:50Z",
    "path": "/v1/6bfd12c0-49a6-11eb-b378-0242ac130002/user/6bfd12c0-49a6-11eb-b378-0242ac130002",
    "message": "Unauthorized"
}
```

The status and error is as expected.

## Write a new test

```java
@Test
void testUpdateUserWhenRequestIsDifferentFromUser() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .put(String.format(UPDATE_USER, USER.getId(), UUID.randomUUID()))
            .content(new UpdateUserDto("Mike Selby", "8765436548", "selby@mark.com").toString())
            .contentType(APPLICATION_JSON))
            .andExpect(status().isForbidden());
}
```

### Try to run the test

```java
[ERROR] testUpdateUserWhenRequestIsDifferentFromUser  Time elapsed: 0.014 s  <<< FAILURE!
java.lang.AssertionError: Status expected:<403> but was:<500>
        at com.atul.gitbook.learn.users.service.ControllerTest.testUpdateUserWhenRequestIsDifferentFromUser(ControllerTest.java:152)
```

### Let's try to resolve the failure

```java
@Override
public User updateUser(UUID requesterId, UUID id, UpdateUserDto userDto) {
    validateNotNull(requesterId);
    validateNotNull(id);
    validateNotNull(userDto);
    try {
        fUserRepository.getUser(requesterId);
    } catch (NoSuchElementException e) {
        throw new UnauthorizedException("Requester is not present.");
    }
    if (!requesterId.equals(id)) {
        throw new ForbiddenException("User can only update own profile details.");
    }
    fUserRepository.updateUser(id, userDto);
    return fUserRepository.getUser(id);
}
```

### Try to run the test

The tests should all be passing now.

### Consume the endpoint

```java
curl -X PUT \
  http://localhost:8080/v1/1109a8c8-49a3-4921-aa80-65e730d587fe/user/6bfd12c0-49a6-11eb-b378-0242ac130002 \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 690dd593-f942-4893-0262-660b9e8016b1' \
  -d '{
	"name": "Mark Allen",
	"phone": "5378574234",
	"email": "mark@allen.io"
}'
```

```java
{
    "status": 403,
    "error": "User can only update own profile details.",
    "timestamp": "2021-01-02T13:09:07Z",
    "path": "/v1/1109a8c8-49a3-4921-aa80-65e730d587fe/user/6bfd12c0-49a6-11eb-b378-0242ac130002",
    "message": "Forbidden"
}
```

The status and error message is as expected.

## Write a new test

```java
@Test
void testUpdateUserWhenRequesterIsUpdatingOwnDetails() throws Exception {
    fMockMvc.perform(MockMvcRequestBuilders
            .put(String.format(UPDATE_USER, USER.getId(), USER.getId()))
            .content(new UpdateUserDto("Mike Selby", "8765436548", "selby@mark.com").toString())
            .contentType(APPLICATION_JSON))
            .andExpect(status().is2xxSuccessful());
}
```

### Try to run the test

The tests should be passing without any code changes.

### Consume the endpoint

```java
curl -X PUT \
  http://localhost:8080/v1/1109a8c8-49a3-4921-aa80-65e730d587fe/user/1109a8c8-49a3-4921-aa80-65e730d587fe \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 58dfd372-8d00-d26f-56df-c9bb5c2002e0' \
  -d '{
	"name": "Mark Allen",
	"phone": "5378574234",
	"email": "mark@allen.io"
}'
```

```java
{
    "name": "Mark Allen",
    "id": "1109a8c8-49a3-4921-aa80-65e730d587fe",
    "phone": "5378574234",
    "email": "mark@allen.io",
    "administrator": false
}
```

The endpoint works as expected.

