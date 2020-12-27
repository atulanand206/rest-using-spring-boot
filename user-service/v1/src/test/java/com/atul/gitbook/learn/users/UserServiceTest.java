package com.atul.gitbook.learn.users;

import com.atul.gitbook.learn.AppConfig;
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
}
