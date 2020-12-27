package com.atul.gitbook.learn.users.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class UserDtoTest {

    @ParameterizedTest
    @MethodSource("streamForCreateUserInvalidInput")
    void testCreateUserInvalidInput(String name, String phone, String email) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new UserDto(name, phone, email));
    }

    private static Stream<Arguments> streamForCreateUserInvalidInput() {
        return Stream.of(
                Arguments.of("Julie", "78978297a4", "abc@de.com"),
                Arguments.of("Julie", "789789728", "abc@de.com"),
                Arguments.of("Julie", "7897839780", "abc@")
        );
    }

    @Test
    void testCreateUserValidInput() {
        Assertions.assertDoesNotThrow(() -> new UserDto("Julie", "7897897280", "abc@de.com"));
    }
}