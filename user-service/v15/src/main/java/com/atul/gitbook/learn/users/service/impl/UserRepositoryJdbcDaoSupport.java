package com.atul.gitbook.learn.users.service.impl;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.sql.DataSource;
import java.sql.Connection;

public class UserRepositoryJdbcDaoSupport extends JdbcDaoSupport {

    public UserRepositoryJdbcDaoSupport(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public Connection getConn() {
        return getConnection();
    }
}
