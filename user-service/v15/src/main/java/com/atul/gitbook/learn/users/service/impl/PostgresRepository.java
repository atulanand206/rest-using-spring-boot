package com.atul.gitbook.learn.users.service.impl;

import com.atul.gitbook.learn.jackson.Serializer;
import com.atul.gitbook.learn.jackson.Serializers;
import com.atul.gitbook.learn.postgres.RepositoryJdbcDaoSupport;
import com.atul.gitbook.learn.users.models.UpdateUserDto;
import com.atul.gitbook.learn.users.models.User;
import com.atul.gitbook.learn.users.models.UserDto;
import com.atul.gitbook.learn.users.service.IUserRepository;
import org.springframework.jdbc.core.RowMapper;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.UUID;

public class PostgresRepository extends IUserRepository {

    private final RepositoryJdbcDaoSupport fJdbcDaoSupport;
    private final RowMapper<User> fRowMapper;

    private final Serializer<UserDto> SERIALIZER_USER_DTO = Serializers.newJsonSerializer(UserDto.class);
    private final Serializer<UpdateUserDto> SERIALIZER_UPDATE_USER_DTO = Serializers.newJsonSerializer(UpdateUserDto.class);

    public PostgresRepository(RepositoryJdbcDaoSupport jdbcDaoSupport,
                              RowMapper<User> rowMapper) {
        fJdbcDaoSupport = jdbcDaoSupport;
        fRowMapper = rowMapper;
        setDefaultAdministrator(createUserIfNotPresent(UUID.fromString("f994c61d-ebd1-463c-a8d8-ebe5989aa501"),
                new UserDto("King Kong", "9999999999", "king@kong.com", true)));
        setDefaultUser(createUserIfNotPresent(UUID.fromString("1109a8c8-49a3-4921-aa80-65e730d587fe"),
                new UserDto("David Marshal", "9999999999", "david@marshall.com", false)));
    }

    @Override
    public User createUser(UserDto userDto) {
        return createUser(UUID.randomUUID(), userDto);
    }

    private User createUser(UUID userId, UserDto userDto) {
        try {
            fJdbcDaoSupport.create(userId.toString(), userDto, SERIALIZER_USER_DTO);
            return getUser(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private User createUserIfNotPresent(UUID userId, UserDto userDto) {
        try {
            return getUser(userId);
        } catch (NoSuchElementException e) {
            return createUser(userId, userDto);
        }
    }

    @Override
    public User getUser(UUID id) {
        return fJdbcDaoSupport.get(id.toString(), fRowMapper);
    }

    @Override
    public void updateUser(UUID userId, UpdateUserDto userDto) {
        try {
            fJdbcDaoSupport.update(userId.toString(), userDto, SERIALIZER_UPDATE_USER_DTO);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUser(UUID id) {
        try {
            fJdbcDaoSupport.delete(id.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
