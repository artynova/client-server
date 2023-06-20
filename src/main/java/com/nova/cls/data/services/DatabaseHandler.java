package com.nova.cls.data.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {
    public static final int CONSTRAINT_ERROR_CODE = 19;
    private static String DB_DRIVER_CLASSNAME = "org.sqlite.JDBC";
    private static String DB_FILE_URL = "jdbc:sqlite:data.db";

    public static String getDbDriverClassname() {
        return DB_DRIVER_CLASSNAME;
    }

    public static void setDbDriverClassname(String dbDriverClassname) {
        DB_DRIVER_CLASSNAME = dbDriverClassname;
    }

    public static String getDbFileUrl() {
        return DB_FILE_URL;
    }

    public static void setDbFileUrl(String dbFileUrl) {
        DB_FILE_URL = dbFileUrl;
    }

    private static final String NEW_CONNECTION_CONFIG_QUERY = """
            PRAGMA foreign_keys = ON;
            PRAGMA journal_mode=WAL;""";

    // groupId is an alias for sqlite's rowid
    private static final String CREATE_GROUPS_TABLE = """
            CREATE TABLE IF NOT EXISTS Groups (
                groupId INTEGER PRIMARY KEY,
                groupName TEXT NOT NULL UNIQUE,
                description TEXT NOT NULL
            ) STRICT;""";

    // goodId is an alias for sqlite's rowid
    private static final String CREATE_GOODS_TABLE = """
            CREATE TABLE IF NOT EXISTS Goods (
                goodId INTEGER PRIMARY KEY,
                goodName TEXT NOT NULL UNIQUE,
                description TEXT NOT NULL,
                manufacturer TEXT NOT NULL,
                quantity INTEGER NOT NULL DEFAULT 0 CHECK (quantity >= 0),
                price INTEGER NOT NULL CHECK (price >= 0),
                groupId INTEGER NOT NULL,
                FOREIGN KEY (groupId) REFERENCES Groups(groupId)
                    ON UPDATE CASCADE
                    ON DELETE CASCADE
            ) STRICT;""";

    public static void initDatabase() {
        Connection connection = getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.execute(CREATE_GROUPS_TABLE);
            statement.execute(CREATE_GOODS_TABLE);
        } catch (SQLException e) {
            throw new DatabaseFailureException("Could not initialize the database", e);
        } finally {
            closeConnection(connection);
        }
    }

    public static Connection getConnection() {
        try {
            Class.forName(DB_DRIVER_CLASSNAME);
            Connection connection = DriverManager.getConnection(DB_FILE_URL);
            applyForeignKeysPragma(connection); // without it SQLite ignores foreign key constraints
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            throw new DatabaseFailureException("Could not open database connection", e);
        }
    }

    public static void applyForeignKeysPragma(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.execute(NEW_CONNECTION_CONFIG_QUERY);
        } catch (SQLException e) {
            throw new DatabaseFailureException("Could not apply foreign keys pragma", e);
        }
    }

    public static void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new DatabaseFailureException("Could not close database connection", e);
        }
    }
}
