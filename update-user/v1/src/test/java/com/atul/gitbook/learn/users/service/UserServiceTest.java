package com.atul.gitbook.learn.users.service;

import com.atul.gitbook.learn.AppConfig;
import com.atul.gitbook.learn.users.models.User;
import com.atul.gitbook.learn.users.models.UserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootTest(
        properties = "spring.main.allow-bean-definition-overriding=true",
        classes = {AppConfig.class})
class UserServiceTest {

    @Autowired
    IUserService fUserService;

    @Test
    void testCreateUserWhenDtoIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> fUserService.createUser(null));
    }

    @Test
    void testCreateUserWhenDtoContainsAllTheFields() {
        final var userDto = new UserDto("Ramsay", "9876483456", "abc@gmail.com");
        Assertions.assertDoesNotThrow(() -> fUserService.createUser(userDto));
    }

    @Test
    void testCreateUser() {
        final var expected = new UserDto("Ramsay", "9876483456", "abc@gmail.com");
        final var actual = fUserService.createUser(expected);
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getPhone(), actual.getPhone());
        Assertions.assertEquals(expected.getEmail(), actual.getEmail());
    }

    @Test
    void testGetUserWhenIdIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> fUserService.getUser(null));
    }

    @Test
    void testGetUserWhenUserIsNotPresent() {
        Assertions.assertThrows(NoSuchElementException.class, () -> fUserService.getUser(UUID.randomUUID()));
    }

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

    @Test
    void testUpdateUserWhenUserIsNotPresent() {
        Assertions.assertThrows(NoSuchElementException.class, () -> fUserService.updateUser(UUID.randomUUID(), new UserDto("Rachel", "9876543214", "abc@def.com")));
    }

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
}
