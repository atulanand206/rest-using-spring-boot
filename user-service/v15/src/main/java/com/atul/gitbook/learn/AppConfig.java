package com.atul.gitbook.learn;

import com.atul.gitbook.learn.users.service.IUserRepository;
import com.atul.gitbook.learn.users.service.IUserService;
import com.atul.gitbook.learn.users.service.impl.PostgresRepository;
import com.atul.gitbook.learn.users.service.impl.UserRepositoryJdbcDaoSupport;
import com.atul.gitbook.learn.users.service.impl.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;

@Configuration
@Import(DataSourceConfig.class)
public class AppConfig {

    @Bean
    IUserService configureUserService(IUserRepository fUserRepository) {
        return new UserService(fUserRepository);
    }

    @Bean
    IUserRepository configureUserRepository(UserRepositoryJdbcDaoSupport jdbcDaoSupport,
                                            @Value("${function.user.create}") String createUserSproc,
                                            @Value("${function.user.get}") String getUserSproc) {
        return new PostgresRepository(jdbcDaoSupport, createUserSproc, getUserSproc);
    }

    @Bean
    UserRepositoryJdbcDaoSupport configureJdbc(DataSource dataSource) {
        return new UserRepositoryJdbcDaoSupport(dataSource);
    }
}
