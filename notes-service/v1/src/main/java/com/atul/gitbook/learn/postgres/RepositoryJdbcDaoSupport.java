package com.atul.gitbook.learn.postgres;

import com.atul.gitbook.learn.jackson.Serializer;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.NoSuchElementException;

public class RepositoryJdbcDaoSupport extends JdbcDaoSupport {

    private final RepoConfig fRepoConfig;

    public RepositoryJdbcDaoSupport(DataSource dataSource,
                                    RepoConfig repoConfig) {
        setDataSource(dataSource);
        fRepoConfig = repoConfig;
    }

    public <T> void create(
            final String id,
            final T body,
            final Serializer<T> serializer) throws SQLException {
        StoredProcedureValidator.validateStoredProcedure(fRepoConfig.getCreateSproc());
        generateCallableStatementWithIdAndBody(id, body, fRepoConfig.getCreateSproc(), serializer, getConnection()).execute();
    }

    public <T> T get(
            final String id,
            final RowMapper<T> rowMapper) {
        StoredProcedureValidator.validateStoredProcedure(fRepoConfig.getGetSproc());
        final PreparedStatementCreator getCallableStatement = (Connection connection) ->
                generateCallableStatementWithId(id, fRepoConfig.getGetSproc(), connection);
        final var items = getJdbcTemplate().query(getCallableStatement, rowMapper);
        final var item = items.stream().findFirst();
        if (item.isPresent())
            return item.get();
        throw new NoSuchElementException();
    }

    public <T> void update(
            final String id,
            final T body,
            final Serializer<T> serializer) throws SQLException {
        StoredProcedureValidator.validateStoredProcedure(fRepoConfig.getUpdateSproc());
        final var updateCallableStatement = generateCallableStatementWithIdAndBody(id,
                body, fRepoConfig.getUpdateSproc(), serializer, getConnection());
        updateCallableStatement.execute();
    }

    public void delete(final String id) throws SQLException {
        StoredProcedureValidator.validateStoredProcedure(fRepoConfig.getDeleteSproc());
        generateCallableStatementWithId(id, fRepoConfig.getDeleteSproc(), getConnection()).execute();
    }

    private <T> CallableStatement generateCallableStatementWithIdAndBody(
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

    private CallableStatement generateCallableStatementWithId(
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
