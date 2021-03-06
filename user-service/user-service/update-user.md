# Update User

## Project Recap

We have the Create and Read operation working in the User Service. The domain methods can be called with required information to create the user and a user can be fetched from the list of users using userId.

Let's work on the Update operation now. The update user method will take in a userId and userDto with new information to update the user details.

## Write a new test

There are two parameters in the method. We should be throwing an IllegalArgumentException when at least one of the parameters are null. We can use a ParameterizedTest to handle all the cases.

```java
@ParameterizedTest
@MethodSource("streamForUpdateUserWhenParametersAreNull")
void testUpdateUserWhenParametersAreNull(UUID userId, UserDto userDto) {
    Assertions.assertThrows(IllegalArgumentException.class, () -> fUserService.updateUser(userId, userDto));
}

private static Stream<Arguments> streamForUpdateUserWhenParametersAreNull() {
    return Stream.of(
            Arguments.of(null, null),
            Arguments.of(null, new UserDto("Rachel", "9876543214", "abc@def.com")),
            Arguments.of(UUID.randomUUID(), null)
    );
}
```

### Try to run the test

```text
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.8.1:testCompile (default-testCompile) on project learn: Compilation failure
[ERROR] /Users/creations/IdeaProjects/rest-using-spring-boot/get-user/v2/src/test/java/com/atul/gitbook/learn/users/service/UserServiceTest.java:[69,83] cannot find symbol
[ERROR]   symbol:   method updateUser(java.util.UUID,com.atul.gitbook.learn.users.models.UserDto)
[ERROR]   location: variable fUserService of type com.atul.gitbook.learn.users.service.IUserService
```

### Let's try to resolve the failure

Let's declare the updateUser method in the interface and implement it in the service.

```java
/**
 * Updates and returns the user.
 *
 * @param id userId of the user being queried.
 * @param userDto contains the new information for the User.
 * @return the updated user.
 */
void updateUser(UUID id, UserDto userDto);
```

```java
@Override
public void updateUser(UUID id, UserDto userDto) {
}
```

 Try again

```text
[ERROR] Failures: 
[ERROR]   UserServiceTest.testUpdateUserWhenParametersAreNull:69 Expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown.
[ERROR]   UserServiceTest.testUpdateUserWhenParametersAreNull:69 Expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown.
[ERROR]   UserServiceTest.testUpdateUserWhenParametersAreNull:69 Expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown.
```

Let's add the null validation for the parameters and run the tests again.

```java
@Override
public void updateUser(UUID id, UserDto userDto) {
    validateNotNull(id);
    validateNotNull(userDto);
}
```

When you'd run the test, you'll see that they are passing.

## Write a new test

Let's write a test to return NoSuchElementException when the user is not present.

```java
@Test
void testUpdateUserWhenUserIsNotPresent() {
    Assertions.assertThrows(NoSuchElementException.class, () ->fUserService.updateUser(UUID.randomUUID(), new UserDto("Rachel", "9876543214", "abc@def.com")));
}
```

### Try to run the test

```java
[ERROR] Failures: 
[ERROR]   UserServiceTest.testUpdateUserWhenUserIsNotPresent:82 Expected java.util.NoSuchElementException to be thrown, but nothing was thrown.
```

### Let's try to resolve the failure

Let's throw an exception instead of returning null and run the test again.

```java
@Override
public void updateUser(UUID id, UserDto userDto) {
    validateNotNull(id);
    validateNotNull(userDto);
    throw new NoSuchElementException();
}
```

The test should be passing now.

## Write a new test

We can trust the UserDto creation to handle invalid inputs and write the test to update the user with correct information.

```java
@Test
void testUpdateUser() {
    final var userDto = new UserDto("Ramsay", "9876483456", "abc@gmail.com");
    final var user = fUserService.createUser(userDto);
    final var newName = "Abc";
    final var newPhone = "7583929275";
    final var newEmail = "rewr@afsa.com";
    final var newUserDto = new UserDto(newName, newPhone, newEmail);
    fUserService.updateUser(user.getId(), newUserDto);
    final var actual = fUserService.getUser(user.getId());
    Assertions.assertEquals(user.getId(), actual.getId());
    Assertions.assertEquals(newName, actual.getName());
    Assertions.assertEquals(newPhone, actual.getPhone());
    Assertions.assertEquals(newEmail, actual.getEmail());
}
```

### Try to run the test

```java
[ERROR] Errors: 
[ERROR]   UserServiceTest.testUpdateUser:94 ? NoSuchElement
```

### Let's try to resolve the failure

We can loop through the list of users and update the list with the new information.

```java
@Override
public void updateUser(UUID id, UserDto userDto) {
    validateNotNull(id);
    validateNotNull(userDto);
    for (var i = 0; i<users.size(); i++) {
        if (users.get(i).getId().equals(id)) {
            users.set(i, User.with(userDto, id));
            return;
        }
    }
    throw new NoSuchElementException();
}
```

The test should be passing now.

## Project Status

The Update operation is working now. In the next chapter, we'll be implementing the Delete operation. 

