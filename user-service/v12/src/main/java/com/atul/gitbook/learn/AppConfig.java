package com.atul.gitbook.learn;

import com.atul.gitbook.learn.users.service.IUserRepository;
import com.atul.gitbook.learn.users.service.IUserService;
import com.atul.gitbook.learn.users.service.impl.InMemoryRepository;
import com.atul.gitbook.learn.users.service.impl.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    IUserService configureUserService(IUserRepository fUserRepository) {
        return new UserService(fUserRepository);
    }

    @Bean
    IUserRepository configureUserRepository() {
        return new InMemoryRepository();
    }
}
