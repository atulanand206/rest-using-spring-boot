# Delete User

## Project Recap

The three pillars of CRUD are now working. Only the final pillar, viz. delete operation among the User CRUD is left to be implemented.

## Write a new test

```java
@Test
void testDeleteUserWhenIdIsNull() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> fUserService.deleteUser(null));
}
```

### Try to run the test

```java
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.8.1:testCompile (default-testCompile) on project learn: Compilation failure
[ERROR] /Users/creations/IdeaProjects/rest-using-spring-boot/user-service/v6/src/test/java/com/atul/gitbook/learn/users/service/UserServiceTest.java:[104,83] cannot find symbol
[ERROR]   symbol:   method deleteUser(<nulltype>)
[ERROR]   location: variable fUserService of type com.atul.gitbook.learn.users.service.IUserService
```

### Let's try to resolve the failure

Let's declare the deleteUser\(\) method in the interface and implement it in the service.

```java
/**
 * Deletes the user.
 * 
 * @param id userId of the user to be deleted.
 */
void deleteUser(UUID id);
```

```java
@Override
public void deleteUser(UUID id) {

}
```

Try again

```java
[ERROR] Failures: 
[ERROR]   UserServiceTest.testDeleteUserWhenIdIsNull:104 Expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown.
```

Let's add the validation for null entries.

```java
@Override
public void deleteUser(UUID id) {
    validateNotNull(id);
}
```

If you'd run the test, the tests should be passing now.

## Write a new test

```java
@Test
void testDeleteUserWhenUserIsNotPresent() {
    Assertions.assertThrows(NoSuchElementException.class, () -> fUserService.deleteUser(UUID.randomUUID()));
}
```

### Try to run the test

```java
[ERROR] Failures: 
[ERROR]   UserServiceTest.testDeleteUserWhenUserIsNotPresent:109 Expected java.util.NoSuchElementException to be thrown, but nothing was thrown.
```

### Let's try to resolve the failure

We can throw the NoSuchElementException.

```java
@Override
public void deleteUser(UUID id) {
    validateNotNull(id);
    throw new NoSuchElementException();
}
```

The tests should be passing now.

## Write a new test

```java
@Test
void testDeleteUser() {
    final var userDto = new UserDto("Ramsay", "9876483456", "abc@gmail.com");
    final var user = fUserService.createUser(userDto);
    Assertions.assertDoesNotThrow(() -> fUserService.deleteUser(user.getId()));
}
```

### Try to run the test

```java
[ERROR] Failures: 
[ERROR]   UserServiceTest.testDeleteUser:116 Unexpected exception thrown: java.util.NoSuchElementException
```

### Let's try to resolve the failure

We can loop through the users and return from the method once the item is present in the system and is deleted.

```java
@Override
public void deleteUser(UUID id) {
    validateNotNull(id);
    for (var i = 0; i<users.size(); i++) {
        if (users.get(i).getId().equals(id)) {
            users.remove(i);
            return;
        }
    }
    throw new NoSuchElementException();
}
```

The tests should be passing now.

## Project Status

User CRUD is now complete. We are now confident that we can manipulate the users in the system in tests at the moment. We will be exposing external controllers in the coming chapters which would make the service accessible on an URL.

