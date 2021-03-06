# Repository Layer

## Project Recap

The UserService is responsible for storing data at the moment. There is no abstraction present to switch the storage modes. It has become prudent to introduce an interface and encapsulate the operation into a repository. As the information is currently stored in memory and it is not possible to scale such a system, we would be introducing a sort of database at some point and switching out the storage unit would be possible without disturbing the service domain.

## Bean Configuration

We will be introducing an `IUserRepository` interface which will be implemented by the `InMemoryRepository` and injected as a bean in the `AppConfig`. We have tests to help us with the refactor. The service is quite concise and straightforward, so it shouldn't be difficult to refactor.

```java
public interface IUserRepository {
}
```

```java
public class InMemoryRepository implements IUserRepository {
}
```

```java
@Bean
IUserRepository configureUserRepository() {
    return new InMemoryRepository();
}
```

The UserService will have a constructor taking in an instance of IUserRepository. Remember to keep running the tests after making every change. We don't want to stay in red for more than necessary.

```java
private final IUserRepository fUserRepository;

public UserService(IUserRepository fUserRepository) {
    this.fUserRepository = fUserRepository;
}
```

### Try to run the tests

```java
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.8.1:compile (default-compile) on project learn: Compilation failure
[ERROR] /Users/creations/IdeaProjects/rest-using-spring-boot/user-service/v7/src/main/java/com/atul/gitbook/learn/AppConfig.java:[15,32] cannot find symbol
[ERROR]   symbol:   variable fUserRepository
[ERROR]   location: class com.atul.gitbook.learn.AppConfig
```

### Let's try to resolve the failure

We must update the Bean configuration to accept an instance of `IUserRepository`.

```java
@Bean
IUserService configureUserService(IUserRepository fUserRepository) {
    return new UserService(fUserRepository);
}
```

The tests should be passing now.

## Refactor 

Let's refactor the UserService.

We'll begin by moving the create operation. The interface definition would be exactly the same as this service is only performing CRUD operations and we are moving that functionality into a repository.

#### The service method

```java
@Override
public User createUser(UserDto userDto) {
    validateNotNull(userDto);
    return fUserRepository.createUser(userDto);
}
```

#### The repository method

```text
@Override
public User createUser(UserDto userDto) {
    final var user = User.with(userDto, UUID.randomUUID());
    users.add(user);
    return user;
}
```

###  Try to run the tests

```text
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.8.1:compile (default-compile) on project learn: Compilation failure
[ERROR] /Users/creations/IdeaProjects/rest-using-spring-boot/user-service/v7/src/main/java/com/atul/gitbook/learn/users/service/impl/InMemoryRepository.java:[13,9] cannot find symbol
[ERROR]   symbol:   variable users
[ERROR]   location: class com.atul.gitbook.learn.users.service.impl.InMemoryRepository
```

### Let's try to the resolve the failure

We must move the users list to the repository. After we move the list away from the service, the other methods in the service won't have access to it. There are a couple of ways to resolve this and let's look at them and decide which refactor is better for us.

* Add a getter in the `IUserRepository` to return the list of users and use that reference wherever users is being used in the `UserService`. This involves altering the code at several places.
* Move all the methods to the repository as that's pretty straight-forward and does not require much effort as we did with the create. This means a major refactor in one go.

We'll be taking the second approach as that is simpler is just moving code and not editing it. Although, it looks like such a major refactor without the help of tests is a bad decision, it could also be termed as a deviation from TDD, this approach gets us back to green in very less time.

```java
public class UserService implements IUserService {

    private final IUserRepository fUserRepository;

    public UserService(IUserRepository fUserRepository) {
        this.fUserRepository = fUserRepository;
    }

    @Override
    public User createUser(UserDto userDto) {
        validateNotNull(userDto);
        return fUserRepository.createUser(userDto);
    }

    @Override
    public User getUser(UUID id) {
        validateNotNull(id);
        return fUserRepository.getUser(id);
    }

    @Override
    public void updateUser(UUID id, UserDto userDto) {
        validateNotNull(id);
        validateNotNull(userDto);
        fUserRepository.updateUser(id, userDto);
    }

    @Override
    public void deleteUser(UUID id) {
        validateNotNull(id);
        fUserRepository.deleteUser(id);
    }
}
```

```java
public interface IUserRepository {

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

    /**
     * Updates the user.
     *
     * @param id userId of the user being queried.
     * @param userDto contains the new information for the User.
     */
    void updateUser(UUID id, UserDto userDto);

    /**
     * Deletes the user.
     *
     * @param id userId of the user to be deleted.
     */
    void deleteUser(UUID id);
}
```

```java
public class InMemoryRepository implements IUserRepository {

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
}
```

 The tests should all be passing after this refactor.

## Project Status

* The service class now has a dependency on `IUserRepository`, currently being backed by the `InMemoryRepository`.
* We have tests written for the `UserDto` and `UserService` and are confident that the service layer works as expected.

## Next item on the agenda

* Introduce the web controller layer and add tests to access the application using REST APIs.

