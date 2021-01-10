package com.atul.gitbook.learn.users.service.impl;

import com.atul.gitbook.learn.users.models.UpdateUserDto;
import com.atul.gitbook.learn.users.models.User;
import com.atul.gitbook.learn.users.models.UserDto;
import com.atul.gitbook.learn.users.service.IUserRepository;
import com.atul.gitbook.learn.utils.Serializer;
import com.atul.gitbook.learn.utils.Serializers;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.UUID;

public class PostgresRepository extends IUserRepository {

    private final UserRepositoryJdbcDaoSupport fJdbcDaoSupport;
    private final String fCreateUserSproc;
    private final String fGetUserSproc;

    private final Serializer<UserDto> SERIALIZER_USER_DTO = Serializers.newJsonSerializer(UserDto.class);

    public PostgresRepository(UserRepositoryJdbcDaoSupport jdbcDaoSupport,
                              String createUserSproc,
                              String getUserSproc) {
        fJdbcDaoSupport = jdbcDaoSupport;
        fCreateUserSproc = createUserSproc;
        fGetUserSproc = getUserSproc;
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
            final var createUserCallableStatement = generateCreateUserCallableStatement(userId.toString(),
                    userDto, fCreateUserSproc, SERIALIZER_USER_DTO, fJdbcDaoSupport.getConn());
            createUserCallableStatement.execute();
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
        final PreparedStatementCreator getUserCallableStatement = (Connection connection) ->
                generateGetUserCallableStatement(id.toString(), fGetUserSproc, connection);
        final var userList = fJdbcDaoSupport.getJdbcTemplate().query(getUserCallableStatement, getUserRowMapper());
        final var user = userList.stream().findFirst();
        if (user.isPresent())
            return user.get();
        throw new NoSuchElementException();
    }

    @Override
    public void updateUser(UUID id, UpdateUserDto userDto) {

    }

    @Override
    public void deleteUser(UUID id) {

    }

    private RowMapper<User> getUserRowMapper() {
        return (final ResultSet rs, final int rowCount) -> {
            final var id = rs.getObject("id", UUID.class);
            final var name = rs.getString("name");
            final var phone = rs.getString("phone");
            final var email = rs.getString("email");
            final var administrator = rs.getBoolean("administrator");
            return new User(id, name, phone, email, administrator);
        };
    }

    private <T> CallableStatement generateCreateUserCallableStatement(
            final String id,
            final T body,
            final String sproc,
            final Serializer<T> serializer,
            final Connection connection) throws SQLException {
        final var pgObject = buildPgObject(serializer.serialize(body));
        final var sql = String.format("{call %s(?, ?)}", sproc);
        final var cs = connection.prepareCall(sql);
        var param = 1;
        cs.setObject(param++, id);
        cs.setObject(param, pgObject);
        return cs;
    }

    private CallableStatement generateGetUserCallableStatement(
            final String id,
            final String sproc,
            final Connection connection) throws SQLException {
        final var sql = String.format("{call %s(?)}", sproc);
        final var cs = connection.prepareCall(sql);
        cs.setObject(1, id);
        return cs;
    }

    private static PGobject buildPgObject(String requestJson) throws SQLException {
        final var jsonObject = new PGobject();
        jsonObject.setType("jsonb");
        jsonObject.setValue(requestJson);
        return jsonObject;
    }
}
