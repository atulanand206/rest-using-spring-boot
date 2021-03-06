# Read User

## Project Recap

We have the create user service working and there are tests to support that the feature is working. We have refactored our code so that every method performs its own function. 

We can now work on the get user service method. The place to begin would be to write a new test in the `UserServiceTest`.

## Write a new test

```java
@Test
void testGetUserWhenIdIsNull() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> fUserService.getUser(null));
}
```

### Try to run the test

```java
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.8.1:testCompile (default-testCompile) on project learn: Compilation failure
[ERROR] /Users/creations/IdeaProjects/rest-using-spring-boot/get-user/v1/src/test/java/com/atul/gitbook/learn/users/service/UserServiceTest.java:[42,40] cannot find symbol
[ERROR]   symbol:   method getUser(java.util.UUID)
[ERROR]   location: variable fUserService of type com.atul.gitbook.learn.users.service.IUserService
```

###  Let's try to resolve the failure

Let's declare the `getUser()` method in the `interface` and implement it in the `service`.

```java
/**
 * Returns the user with the provided userId.
 * 
 * @param id the userId of the user being queried.
 */
User getUser(UUID id);
```

```java
@Override
public User getUser(UUID id) {
    return null;
}
```

Try again

```java
[ERROR] Failures: 
[ERROR]   UserServiceTest.testGetUserWhenIdIsNull:40 Expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown.
```

Let's add the validation check to ensure exception is thrown when null is passed as the id.

```java
@Override
public User getUser(UUID id) {
    validateNotNull(id);
    return null;
}
```

One more try...

```java
[ERROR] Errors: 
[ERROR]   UserServiceTest.testGetUser:48 NullPointer
```

Let's return a random user object now.

```java
@Override
public User getUser(UUID id) {
    validateNotNull(id);
    return new User(UUID.randomUUID(), "Abc", "8787897134", "abc@def.com");
}
```

 The test should be passing now.

## Write a new test

```java
@Test
void testGetUserWhenUserIsNotPresent() {
    Assertions.assertThrows(NoSuchElementException.class, () ->fUserService.getUser(UUID.randomUUID()));
}
```

### Try to run the test

```java
[ERROR] Failures: 
[ERROR]   UserServiceTest.testGetUserWhenUserIsNotPresent:48 Expected java.util.NoSuchElementException to be thrown, but nothing was thrown.
```

### Let's try to resolve the failure

We can throw NoSuchElementException instead of returning random user.

```java
@Override
public User getUser(UUID id) {
    validateNotNull(id);
    throw new NoSuchElementException();
}
```

 Try again

The test should be passing now.

##  Write a new test

```java
@Test
void testGetUser() {
    final var userDto = new UserDto("Ramsay", "9876483456", "abc@gmail.com");
    final var expected = fUserService.createUser(userDto);
    final var actual = fUserService.getUser(expected.getId());
    Assertions.assertEquals(expected.getId(), actual.getId());
    Assertions.assertEquals(expected.getName(), actual.getName());
    Assertions.assertEquals(expected.getPhone(), actual.getPhone());
    Assertions.assertEquals(expected.getEmail(), actual.getEmail());
}
```

### Try to run the test

```java
[ERROR] Errors: 
[ERROR]   UserServiceTest.testGetUser:56 NoSuchElement
```

### Let's try to resolve the failure

It's time to work on the test failure. The method is returning an exception in all cases but we'd like to return user data. Let's return a new `User` with random data in an attempt to fix the exception.

```java
    @Override
    public User getUser(UUID id) {
        validateNotNull(id);
        return new User(UUID.randomUUID(), "Abc", "8787897134", "abc@def.com");
    }
```

 Try again

```java
[ERROR] Failures: 
[ERROR]   UserServiceTest.testGetUser:57 expected: <fdc3c3d7-c77e-4510-925a-967eaec26ce6> but was: <005a63f9-f650-4379-90a2-13b26cee0ed5>
[ERROR]   UserServiceTest.testGetUserWhenUserIsNotPresent:48 Expected java.util.NoSuchElementException to be thrown, but nothing was thrown.
```

This breaks the other test, which means the approach we took to solve the failure was not correct.   
As we are returning new information and not looking from the already created user. We should look for storing the users in the service. We can use a list of users to store it and return the user from the list if the id matches. We can achieve that by refactoring the service class.

```java
public class UserService implements IUserService {

    private List<User> users = new ArrayList<>();

    @Override
    public User createUser(UserDto userDto) {
        validateNotNull(userDto);
        final var user = User.with(userDto, UUID.randomUUID());
        users.add(user);
        return user;
    }

    @Override
    public User getUser(UUID id) {
        validateNotNull(id);
        for (var user : users) {
            if (id.equals(user.getId()))
                return user;
        }
        throw new NoSuchElementException();
    }
}
```

 One more try..

The tests should all be passing now.

The current implementation returns the user with a matching id and throws an exception if the user is not present.

{% hint style="info" %}
You'll notice that the Exceptions are not present in the method signature. This is because the exceptions in use extends RuntimeException and Spring Boot assumes that RuntimeException can be thrown by the methods and are not required to be mentioned in the method signature. I have taken them out from the interface as well.
{% endhint %}

```java
public interface IUserService {

    /**
     * Creates and returns a new user.
     *
     * @param userDto contains the necessary information for the User.
     * @return the created user
     */
    User createUser(UserDto userDto);

    /**
     * Returns the user with the provided userId.
     *
     * @param id the userId of the user being queried.
     */
    User getUser(UUID id);
}
```

## Project Sta tus

We are now finished with the Create and Read operation. In the next chapter we'll be working on the Update operation.

