package com.nova.cls.data.services;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.nova.cls.exceptions.DatabaseFailureException;
import com.nova.cls.data.models.Group;
import com.nova.cls.data.models.User;
import com.nova.cls.data.Views;
import com.nova.cls.util.Hasher;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {
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
            price INTEGER NOT NULL CHECK (price > 0),
            groupId INTEGER NOT NULL,
            FOREIGN KEY (groupId) REFERENCES Groups(groupId)
                ON UPDATE CASCADE
                ON DELETE CASCADE
        ) STRICT;""";
    private static final String CREATE_USERS_TABLE = """
        CREATE TABLE IF NOT EXISTS Users (
            userId INTEGER PRIMARY KEY,
            login TEXT NOT NULL UNIQUE,
            passwordHash TEXT NOT NULL
        ) STRICT;""";
    private static final String INIT_USERS_RESOURCE_PATH = "initUsers.json";
    private static final String INIT_GROUPS_RESOURCE_PATH = "initGroups.json";
    private final static String DB_URL_PREFIX = "jdbc:sqlite:";
    private static String DB_DRIVER_CLASSNAME = "org.sqlite.JDBC";
    private static String DB_PROJECT_PATH = "data.db";

    private DatabaseHelper() {
    }

    public static String getDbDriverClassname() {
        return DB_DRIVER_CLASSNAME;
    }

    public static void setDbDriverClassname(String dbDriverClassname) {
        DB_DRIVER_CLASSNAME = dbDriverClassname;
    }

    public static String getDbUrl() {
        return DB_URL_PREFIX + DB_PROJECT_PATH;
    }

    public static String getDbProjectPath() {
        return DB_PROJECT_PATH;
    }

    public static void setDbProjectPath(String path) {
        DB_PROJECT_PATH = path;
    }

    /**
     * Inits the database if the file does not exist yet.
     */
    public static void initDatabase() {
        if (!Files.exists(Path.of(getDbProjectPath()))) {
            Connection connection = getConnection();
            try (Statement statement = connection.createStatement()) {
                statement.execute(CREATE_GROUPS_TABLE);
                statement.execute(CREATE_GOODS_TABLE);
                statement.execute(CREATE_USERS_TABLE);
                initDb(statement);
            } catch (SQLException | IOException e) {
                throw new DatabaseFailureException("Could not initialize the database", e);
            } finally {
                closeConnection(connection);
            }
        }
    }

    private static void initDb(Statement statement) throws SQLException, IOException {
        // init users
        try (InputStream initStream = DatabaseHelper.class.getClassLoader()
            .getResourceAsStream(INIT_USERS_RESOURCE_PATH)) {
            JsonMapper mapper = new JsonMapper();
            ObjectReader createReader = mapper.readerWithView(Views.CreateView.class);
            User[] users = createReader.readValue(initStream, User[].class);
            Hasher hasher = new Hasher();
            for (User user : users) {
                // since data is acquired from server's internal initialization JSON,
                // it is trusted to be safely insertable into the query directly
                // also passwords are plaintext in the initial files, so hashing them
                user.setPasswordHash(hasher.hash(user.getPasswordHash()));
                statement.execute("INSERT INTO Users (login, passwordHash) VALUES ('" + user.getLogin() + "', '"
                    + user.getPasswordHash() + "')");
            }
        }

        // init groups
        try (InputStream initStream = DatabaseHelper.class.getClassLoader()
            .getResourceAsStream(INIT_GROUPS_RESOURCE_PATH)) {
            JsonMapper mapper = new JsonMapper();
            ObjectReader createReader = mapper.readerWithView(Views.CreateView.class);
            Group[] groups = createReader.readValue(initStream, Group[].class);
            for (Group group : groups) {
                statement.execute(
                    "INSERT INTO Groups (groupName, description) VALUES ('" + group.getGroupName() + "', '"
                        + group.getDescription() + "')");
            }
        }
    }

    public static Connection getConnection() {
        try {
            Class.forName(DB_DRIVER_CLASSNAME);
            Connection connection = DriverManager.getConnection(getDbUrl());
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
