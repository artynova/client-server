package com.nova.cls.data.services;

import com.nova.cls.data.models.User;
import com.nova.cls.data.exceptions.request.NotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class UsersService extends CrudService<User> {
    private static final String TABLE_NAME = "Users";
    private static final String ID_NAME = "userId";
    private static final String[] READ_FIELDS = new String[] {"userId", "login", "passwordHash"};
    private static final String[] CREATE_FIELDS = Arrays.copyOfRange(READ_FIELDS, 1, READ_FIELDS.length);

    private static final String[] UPDATE_FIELDS = Arrays.copyOf(CREATE_FIELDS, CREATE_FIELDS.length);

    private final PreparedStatement findPasswordHashByLogin;

    public UsersService(Connection connection) {
        super(connection, TABLE_NAME, ID_NAME, CREATE_FIELDS, UPDATE_FIELDS, READ_FIELDS);
        this.findPasswordHashByLogin = initFindPasswordHashByLogin();
    }

    private PreparedStatement initFindPasswordHashByLogin() {
        String query = "SELECT passwordHash FROM Users WHERE login = ?;";
        try {
            return connection.prepareStatement(query);
        } catch (SQLException e) {
            throw new DatabaseFailureException("Could not initialize offset quantity of Goods statement", e);
        }
    }

    public String findPasswordHash(String login) {
        try {
            findPasswordHashByLogin.setObject(1, login);
        } catch (SQLException e) {
            throw new DatabaseFailureException("Unexpected offset quantity statement fill error", e);
        }
        String passwordHash;
        ResultSet set;
        try {
            set = findPasswordHashByLogin.executeQuery();
            if (!set.next()) {
                throw new NotFoundException("User " + login + " does not exist");
            }
            passwordHash = set.getString("passwordHash");
        } catch (SQLException e) {
            throw new DatabaseFailureException("Failed to find user " + login + ": " + e.getMessage(), e);
        }

        try {
            set.close();
        } catch (SQLException e) {
            throw new DatabaseFailureException("Could not close result set for find password hash in Users table", e);
        }
        return passwordHash;
    }

    @Override
    protected Long getId(User user) {
        return user.getUserId();
    }

    @Override
    protected User getModelUnsafe(ResultSet set) throws SQLException {
        User user = new User();
        user.setUserId(set.getLong("userId"));
        user.setLogin(set.getString("login"));
        return user;
    }

    @Override
    protected void fillCreateParamsUnsafe(User user, PreparedStatement statement) throws SQLException {
        statement.setObject(1, user.getLogin());
        statement.setObject(2, user.getPasswordHash());
    }

    @Override
    protected void fillUpdateParamsUnsafe(User user, PreparedStatement statement) throws SQLException {
        fillCreateParamsUnsafe(user, statement);
    }

    @Override
    public void close() throws SQLException {
        if (isClosed()) {
            return;
        }
        findPasswordHashByLogin.close();
        super.close();
    }
}
