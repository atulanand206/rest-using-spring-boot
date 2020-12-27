package com.atul.gitbook.learn.users.service;

import com.atul.gitbook.learn.AppConfig;
import com.atul.gitbook.learn.users.models.UserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
}
