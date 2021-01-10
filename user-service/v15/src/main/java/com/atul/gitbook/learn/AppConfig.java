package com.atul.gitbook.learn;

import com.atul.gitbook.learn.postgres.RepoConfig;
import com.atul.gitbook.learn.postgres.RepositoryJdbcDaoSupport;
import com.atul.gitbook.learn.users.models.User;
import com.atul.gitbook.learn.users.service.IUserRepository;
import com.atul.gitbook.learn.users.service.IUserService;
import com.atul.gitbook.learn.users.service.impl.PostgresRepository;
import com.atul.gitbook.learn.users.service.impl.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.UUID;

@Configuration
@Import(DataSourceConfig.class)
public class AppConfig {

    @Bean
    IUserService configureUserService(IUserRepository fUserRepository) {
        return new UserService(fUserRepository);
    }

    @Bean
    IUserRepository configureUserRepository(RepositoryJdbcDaoSupport jdbcDaoSupport,
                                            RowMapper<User> rowMapper) {
        return new PostgresRepository(jdbcDaoSupport, rowMapper);
    }

    @Bean
    RepoConfig configureRepoConfig(@Value("${function.user.create}") String createUserSproc,
                                   @Value("${function.user.get}") String getUserSproc,
                                   @Value("${function.user.update}") String updateUserSproc,
                                   @Value("${function.user.delete}") String deleteUserSproc) {
        return new RepoConfig(createUserSproc, getUserSproc, updateUserSproc, deleteUserSproc);
    }

    @Bean
    RepositoryJdbcDaoSupport configureJdbc(DataSource dataSource,
                                           RepoConfig repoConfig) {
        return new RepositoryJdbcDaoSupport(dataSource, repoConfig);
    }

    @Bean
    RowMapper<User> configureUserRowMapper() {
        return (final ResultSet rs, final int rowCount) -> {
            final var id = rs.getObject("id", UUID.class);
            final var name = rs.getString("name");
            final var phone = rs.getString("phone");
            final var email = rs.getString("email");
            final var administrator = rs.getBoolean("administrator");
            return new User(id, name, phone, email, administrator);
        };
    }
}
