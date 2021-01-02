package com.atul.gitbook.learn.users.service;

import com.atul.gitbook.learn.TestBase;
import com.atul.gitbook.learn.exceptions.ForbiddenException;
import com.atul.gitbook.learn.exceptions.UnauthorizedException;
import com.atul.gitbook.learn.users.models.UpdateUserDto;
import com.atul.gitbook.learn.users.models.UserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

import static com.atul.gitbook.learn.users.service.impl.InMemoryRepository.ADMINISTRATOR;

class UserServiceTest extends TestBase {

    @Autowired
    IUserService fUserService;

    @Test
    void testCreateUserWhenDtoIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> fUserService.createUser(null, null));
    }

    @Test
    void testCreateUserWhenDtoContainsAllTheFields() {
        final var userDto = new UserDto("Ramsay", "9876483456", "abc@gmail.com");
        Assertions.assertDoesNotThrow(() -> fUserService.createUser(ADMINISTRATOR.getId(), userDto));
    }

    @Test
    void testCreateUser() {
        final var expected = new UserDto("Ramsay", "9876483456", "abc@gmail.com");
        final var actual = fUserService.createUser(ADMINISTRATOR.getId(), expected);
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getPhone(), actual.getPhone());
        Assertions.assertEquals(expected.getEmail(), actual.getEmail());
    }

    @Test
    void testGetUserWhenIdIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> fUserService.getUser(null, null));
    }

    @Test
    void testGetUserWhenUserIsNotPresent() {
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            fUserService.getUser(ADMINISTRATOR.getId(), UUID.randomUUID());
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
        Assertions.assertThrows(UnauthorizedException.class, () -> fUserService.updateUser(UUID.randomUUID(), UUID.randomUUID(), new UpdateUserDto("Rachel", "9876543214", "abc@def.com")));
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

    @Test
    void testDeleteUserWhenIdIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> fUserService.deleteUser(null, null));
    }

    @Test
    void testDeleteUserWhenUserIsNotPresent() {
        Assertions.assertThrows(ForbiddenException.class, () -> fUserService.deleteUser(ADMINISTRATOR.getId(), UUID.randomUUID()));
    }

    @Test
    void testDeleteUser() {
        final var userDto = new UserDto("Ramsay", "9876483456", "abc@gmail.com");
        final var user = fUserService.createUser(ADMINISTRATOR.getId(), userDto);
        Assertions.assertDoesNotThrow(() -> fUserService.deleteUser(user.getId(), user.getId()));
        Assertions.assertThrows(NoSuchElementException.class, () -> fUserService.getUser(ADMINISTRATOR.getId(), user.getId()));
    }
}
