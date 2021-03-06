# Refactoring Service Validations

We should look at the whole class in one go to figure out what's common. You can suggest other ways of handling this by raising a pull request.

```java
@Override
public User createUser(UUID requesterId, UserDto userDto) {
    validateNotNull(requesterId);
    validateNotNull(userDto);
    try {
        final var user = fUserRepository.getUser(requesterId);
        if (!user.isAdministrator())
            throw new ForbiddenException("Requester is not an administrator and cannot request user creation.");
    } catch (NoSuchElementException e) {
        throw new UnauthorizedException("Requester is not present.");
    }
    return fUserRepository.createUser(userDto);
}

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

## Validate requester is user

Except for the `createUser`, all of the methods have a requester-id matching checking with a different message. It makes sense to me to start there. This method would have 3 parameters, `requesterId`, `userId` and a `message`.

```java
private void validateRequesterSameAsUser(UUID requesterId, UUID userId, String message) {
    if (!requesterId.equals(userId))
        throw new ForbiddenException(message);
}
```

Plug this in the service methods.

## Validate requester presence

The other common part looks like the requester presence check. All of the methods are calling the repository's `getUser` method and throwing an exception if the requester is not present. The `createUser` method has an additional check of ensuring that the requester is an administrator. 

Let's separate those concerns first and then the code block would be identical. The refactored method can then return a user object and which can be used if required and ignored otherwise.

Remember to keep running your test on code changes. They are your single source of truth at this point. 

```javascript
private User getRequester(UUID requesterId) {
    try {
        return fUserRepository.getUser(requesterId);
    } catch (NoSuchElementException e) {
        throw new UnauthorizedException("Requester is not present.");
    }
}
```

We're almost done. Let's look at the CRUD methods now.

```java
@Override
public User createUser(UUID requesterId, UserDto userDto) {
    validateNotNull(requesterId);
    validateNotNull(userDto);
    final var requester = getRequester(requesterId);
    if (!requester.isAdministrator())
        throw new ForbiddenException("Requester is not an administrator and cannot request user creation.");
    return fUserRepository.createUser(userDto);
}

@Override
public User getUser(UUID requesterId, UUID userId) {
    validateNotNull(requesterId);
    validateNotNull(userId);
    final var requester = getRequester(requesterId);
    if (requester.isAdministrator()) {
        return fUserRepository.getUser(userId);
    }
    validateRequesterSameAsUser(requesterId, userId, "User can only request own profile details.");
    return fUserRepository.getUser(userId);
}

@Override
public User updateUser(UUID requesterId, UUID userId, UpdateUserDto userDto) {
    validateNotNull(requesterId);
    validateNotNull(userId);
    validateNotNull(userDto);
    getRequester(requesterId);
    validateRequesterSameAsUser(requesterId, userId, "User can only update own profile details.");
    fUserRepository.updateUser(userId, userDto);
    return fUserRepository.getUser(userId);
}

@Override
public void deleteUser(UUID requesterId, UUID userId) {
    validateNotNull(requesterId);
    validateNotNull(userId);
    getRequester(requesterId);
    validateRequesterSameAsUser(requesterId, userId, "User can only delete own profile details.");
    fUserRepository.deleteUser(userId);
}
```

We can't really do anything more with the `updateUser` and `deleteUser` anymore. The only thing left is to refactor out that administrator check from `createUser` and `getUser` now. 

It should be straightforward for the `createUser` block. A private method taking the requester and throwing an exception if the user is not an administrator. Let's get that over with first.

```java
private void validateRequesterCanCreateUser(User requester) {
    if (!requester.isAdministrator())
        throw new ForbiddenException("Requester is not an administrator and cannot request user creation.");
}
```

## Validate requester can create

Coming to the dreadful `getUser` method which makes 2 calls to the fUserRepository, which is definitely a code smell and maybe we can get away with a single call if we handle both the validations in a single method. We can call that method as `validateRequesterCanGetUser()`. Let's see how we can implement this. 

```java
private void validateRequesterCanGetUser(User requester, UUID userId) {
    if (!requester.isAdministrator() && !userId.equals(requester.getId()))
        throw new ForbiddenException("Requester can not request to get the user's profile details.");
}
```

Now, our `getUser` method looks cleaner and conveys the workflow in domain terms.

```java
@Override
public User getUser(UUID requesterId, UUID userId) {
    validateNotNull(requesterId);
    validateNotNull(userId);
    final var requester = getRequester(requesterId);
    validateRequesterCanGetUser(requester, userId);
    return fUserRepository.getUser(userId);
}
```

If you'll run the tests, they should all be passing. We've not broken anything.

We have developed the components of the user service in a non-linear fashion. When a part of the code started working correctly, we moved onto a different component and came back to refactor later. I did that because I wanted to give way to all the components as we required all the components to finish our tests as early as we can. Now, that we have all the scenarios accounted for in the tests, we can confidently refactor our code without any trouble. In actual software engineering projects, every single commit must be refactored till you can, and that is the basis of clean and scalable code.

## Refactor messages

One final refactor I can think of is moving the exception message strings as static constants. We won't want to create a new string every time an exception is thrown when we can reuse. Decide a convention of how you want to name the strings and keep it similar for all the messages. Usually, one validation method would throw a single message and the message can named along the lines of the method name. 

```java
private static final String ERROR_UPDATE_OWN_PROFILE = "User can only update own profile details.";
private static final String ERROR_DELETE_OWN_PROFILE = "User can only delete own profile details.";
private static final String ERROR_REQUESTER_UNAVAILABLE = "Requester is not present.";
private static final String ERROR_REQUESTER_CANT_CREATE = "Requester is not an administrator and cannot request user creation.";
private static final String ERROR_REQUESTER_CANT_GET = "Requester can not request to get the user's profile details.";
```

The error messages at a single place also give the freedom to change the string as we want without breaking anything as that object referenced everywhere the error is supposed to come up. This approach is also suitable for the project's stakeholders as they can suggest changes to the error messages without diving into the actual source code. Edits happen a lot to these messages in reality. 

Usually, the messages and validations would stay in the service class itself. If there are multiple service classes which may use the same validations, you can look for creating a utils class and have the common validations there. In a micro-services based system, as everything is decoupled, every micro-service would need to define the messages and validations separately.

## Project Status

We have the user service crafted to be used now. We can move onto the other parts of the system.

