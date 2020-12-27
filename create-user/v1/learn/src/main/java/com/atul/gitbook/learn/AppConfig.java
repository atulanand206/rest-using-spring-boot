package com.atul.gitbook.learn;

import com.atul.gitbook.learn.users.IUserService;
import com.atul.gitbook.learn.users.impl.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    IUserService configureUserService() {
        return new UserService();
    }
}
