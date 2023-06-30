package com.nova.cls.services;

import com.nova.cls.data.models.User;
import com.nova.cls.exceptions.request.ConflictException;
import com.nova.cls.exceptions.request.NotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UsersServiceTests {
    private UsersService usersService;
    @Mock
    private PreparedStatement mockInsertStatement;
    @Mock
    private PreparedStatement mockSelectOneStatement;
    @Mock
    private PreparedStatement mockUpdateStatement;
    @Mock
    private PreparedStatement mockDeleteStatement;
    @Mock
    private PreparedStatement mockFindPasswordHashByLoginStatement;

    @Before
    public void setUp() throws SQLException {
        Connection mockConnection = mock(Connection.class);
        MockitoAnnotations.openMocks(this);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockInsertStatement,
            mockSelectOneStatement,
            mockUpdateStatement,
            mockDeleteStatement,
            mockFindPasswordHashByLoginStatement);

        usersService = new UsersService(mockConnection);
    }

    @After
    public void tearDown() throws SQLException {
        usersService.close();
    }

    @Test
    public void createValidModelReturnsGeneratedId() throws SQLException {
        User user = new User();
        setTestValues(user);

        ResultSet mockGeneratedIdSet = mock(ResultSet.class);
        when(mockGeneratedIdSet.getLong(anyString())).thenReturn(1L);
        when(mockInsertStatement.executeQuery()).thenReturn(mockGeneratedIdSet);

        Long generatedId = usersService.create(user);

        assertEquals(1L, generatedId.longValue());
        verify(mockInsertStatement).setObject(1, user.getLogin());
        verify(mockInsertStatement).setObject(2, user.getPasswordHash());
        verify(mockGeneratedIdSet).close();
    }

    @Test(expected = ConflictException.class)
    public void createConstraintViolationThrowsConflictException() throws SQLException {
        User user = new User();
        setTestValues(user);

        when(mockInsertStatement.executeQuery()).thenThrow(new SQLException("Constraint violation",
            null,
            ConstraintExceptionAdapter.CONSTRAINT_ERROR_CODE));

        usersService.create(user);
    }

    @Test
    public void findOneExistingIdReturnsModel() throws SQLException {
        Long userId = 1L;
        User expectedUser = new User();
        expectedUser.setUserId(userId);
        expectedUser.setLogin("Test User");
        expectedUser.setPasswordHash("Test Password Hash");

        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong(anyString())).thenReturn(userId);
        when(mockResultSet.getString("login")).thenReturn("Test User");
        when(mockSelectOneStatement.executeQuery()).thenReturn(mockResultSet);

        User actualUser = usersService.findOne(userId);

        assertNotNull(actualUser);
        assertEquals(expectedUser.getUserId(), actualUser.getUserId());
        assertEquals(expectedUser.getLogin(), actualUser.getLogin());
        verify(mockSelectOneStatement).setObject(1, userId);
        verify(mockResultSet).close();
    }

    @Test(expected = NotFoundException.class)
    public void findOneNonexistentIdThrowsNotFoundException() throws SQLException {
        Long userId = 1L;

        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockResultSet.next()).thenReturn(false);
        when(mockSelectOneStatement.executeQuery()).thenReturn(mockResultSet);

        usersService.findOne(userId);
    }

    @Test
    public void updateValidModelUpdatesRecord() throws SQLException {
        User user = new User();
        user.setUserId(1L);
        setTestValues(user);

        Long userId = user.getUserId();
        when(mockUpdateStatement.executeUpdate()).thenReturn(1);

        usersService.update(user);

        verify(mockUpdateStatement).setObject(1, user.getLogin());
        verify(mockUpdateStatement).setObject(2, user.getPasswordHash());
        verify(mockUpdateStatement).setObject(3, userId);
    }

    @Test(expected = NotFoundException.class)
    public void updateNonexistentModelThrowsNotFoundException() throws SQLException {
        User user = new User();
        user.setUserId(1L);
        setTestValues(user);

        when(mockUpdateStatement.executeUpdate()).thenReturn(0);

        usersService.update(user);
    }

    @Test(expected = ConflictException.class)
    public void updateConstraintViolationThrowsConflictException() throws SQLException {
        User user = new User();
        user.setUserId(1L);
        setTestValues(user);

        when(mockUpdateStatement.executeUpdate()).thenThrow(new SQLException("Constraint violation",
            null,
            ConstraintExceptionAdapter.CONSTRAINT_ERROR_CODE));

        usersService.update(user);
    }

    @Test
    public void deleteExistingIdDeletesRecord() throws SQLException {
        Long userId = 1L;

        when(mockDeleteStatement.executeUpdate()).thenReturn(1);

        usersService.delete(userId);

        verify(mockDeleteStatement).setObject(1, userId);
    }

    @Test(expected = NotFoundException.class)
    public void deleteNonexistentIdThrowsNotFoundException() throws SQLException {
        Long userId = 1L;

        when(mockDeleteStatement.executeUpdate()).thenReturn(0);

        usersService.delete(userId);
    }

    @Test
    public void closeNotClosedClosesStatements() throws SQLException {
        usersService.close();

        verify(mockInsertStatement).close();
        verify(mockUpdateStatement).close();
        verify(mockDeleteStatement).close();
    }

    @Test
    public void closeAlreadyClosedDoesNothing() throws SQLException {
        usersService.close();

        usersService.close();

        verify(mockInsertStatement).close();
        verify(mockUpdateStatement).close();
        verify(mockDeleteStatement).close();
    }

    @Test
    public void isClosedNotClosedReturnsFalse() {
        boolean closed = usersService.isClosed();

        assertFalse(closed);
    }

    @Test
    public void isClosedClosedReturnsTrue() throws SQLException {
        usersService.close();

        boolean closed = usersService.isClosed();

        assertTrue(closed);
    }

    @Test
    public void findHashExistingLoginReturnsModel() throws SQLException {
        String login = "Test Login";
        String hash = "Test Hash";

        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString(anyString())).thenReturn(hash);
        when(mockFindPasswordHashByLoginStatement.executeQuery()).thenReturn(mockResultSet);

        usersService.findPasswordHash(login);

        verify(mockFindPasswordHashByLoginStatement).setObject(1, login);
        verify(mockResultSet).close();
    }

    @Test(expected = NotFoundException.class)
    public void findHashNonexistentLoginThrowsNotFoundException() throws SQLException {
        String login = "Test Login";

        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockResultSet.next()).thenReturn(false);
        when(mockFindPasswordHashByLoginStatement.executeQuery()).thenReturn(mockResultSet);

        usersService.findPasswordHash(login);
    }

    private void setTestValues(User user) {
        user.setLogin("Test User");
        user.setPasswordHash("Test Password Hash");
    }
}
