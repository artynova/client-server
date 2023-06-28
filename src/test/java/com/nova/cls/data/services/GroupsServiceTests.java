package com.nova.cls.data.services;

import com.nova.cls.data.models.Group;
import com.nova.cls.data.services.ConstraintExceptionAdapter;
import com.nova.cls.data.services.GroupsService;
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

public class GroupsServiceTests {

    private GroupsService groupsService;
    @Mock
    private PreparedStatement mockInsertStatement;
    @Mock
    private PreparedStatement mockSelectOneStatement;
    @Mock
    private PreparedStatement mockUpdateStatement;
    @Mock
    private PreparedStatement mockDeleteStatement;

    @Before
    public void setUp() throws SQLException {
        Connection mockConnection = mock(Connection.class);
        MockitoAnnotations.openMocks(this);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockInsertStatement,
            mockSelectOneStatement,
            mockUpdateStatement,
            mockDeleteStatement);

        groupsService = new GroupsService(mockConnection);
    }

    @After
    public void tearDown() throws SQLException {
        groupsService.close();
    }

    @Test
    public void createValidModelReturnsGeneratedId() throws SQLException {
        Group group = new Group();
        setTestValues(group);

        ResultSet mockGeneratedIdSet = mock(ResultSet.class);
        when(mockGeneratedIdSet.getLong(anyString())).thenReturn(1L);
        when(mockInsertStatement.executeQuery()).thenReturn(mockGeneratedIdSet);

        Long generatedId = groupsService.create(group);

        assertEquals(1L, generatedId.longValue());
        verify(mockInsertStatement).setObject(1, group.getGroupName());
        verify(mockInsertStatement).setObject(2, group.getDescription());
        verify(mockGeneratedIdSet).close();
    }

    @Test(expected = ConflictException.class)
    public void createConstraintViolationThrowsConflictException() throws SQLException {
        Group group = new Group();
        setTestValues(group);

        when(mockInsertStatement.executeQuery()).thenThrow(new SQLException("Constraint violation",
            null,
            ConstraintExceptionAdapter.CONSTRAINT_ERROR_CODE));

        groupsService.create(group);
    }

    @Test
    public void findOneExistingIdReturnsModel() throws SQLException {
        Long groupId = 1L;
        Group expectedGroup = new Group();
        expectedGroup.setGroupId(groupId);
        setTestValues(expectedGroup);


        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong(anyString())).thenReturn(groupId);
        when(mockResultSet.getString("groupName")).thenReturn("Test Group");
        when(mockResultSet.getString("description")).thenReturn("Test Description");
        when(mockSelectOneStatement.executeQuery()).thenReturn(mockResultSet);

        Group actualGroup = groupsService.findOne(groupId);

        assertNotNull(actualGroup);
        assertEquals(expectedGroup.getGroupId(), actualGroup.getGroupId());
        assertEquals(expectedGroup.getGroupName(), actualGroup.getGroupName());
        assertEquals(expectedGroup.getDescription(), actualGroup.getDescription());
        verify(mockSelectOneStatement).setObject(1, groupId);
        verify(mockResultSet).close();
    }

    @Test(expected = NotFoundException.class)
    public void findOneNonexistentIdThrowsNotFoundException() throws SQLException {
        Long groupId = 1L;

        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockResultSet.next()).thenReturn(false);
        when(mockSelectOneStatement.executeQuery()).thenReturn(mockResultSet);

        groupsService.findOne(groupId);
    }

    @Test
    public void updateValidModelUpdatesRecord() throws SQLException {
        Group group = new Group();
        group.setGroupId(1L);
        setTestValues(group);

        Long groupId = group.getGroupId();
        when(mockUpdateStatement.executeUpdate()).thenReturn(1);

        groupsService.update(group);

        verify(mockUpdateStatement).setObject(1, group.getGroupName());
        verify(mockUpdateStatement).setObject(2, group.getDescription());
        verify(mockUpdateStatement).setObject(3, groupId);
    }

    @Test(expected = NotFoundException.class)
    public void updateNonexistentModelThrowsNotFoundException() throws SQLException {
        Group group = new Group();
        group.setGroupId(1L);
        setTestValues(group);

        when(mockUpdateStatement.executeUpdate()).thenReturn(0);

        groupsService.update(group);
    }

    @Test(expected = ConflictException.class)
    public void updateConstraintViolationThrowsConflictException() throws SQLException {
        Group group = new Group();
        group.setGroupId(1L);
        setTestValues(group);

        when(mockUpdateStatement.executeUpdate()).thenThrow(new SQLException("Constraint violation",
            null,
            ConstraintExceptionAdapter.CONSTRAINT_ERROR_CODE));

        groupsService.update(group);
    }

    @Test
    public void deleteExistingIdDeletesRecord() throws SQLException {
        Long groupId = 1L;

        when(mockDeleteStatement.executeUpdate()).thenReturn(1);

        groupsService.delete(groupId);

        verify(mockDeleteStatement).setObject(1, groupId);
    }

    @Test(expected = NotFoundException.class)
    public void deleteNonexistentIdThrowsNotFoundException() throws SQLException {
        Long groupId = 1L;

        when(mockDeleteStatement.executeUpdate()).thenReturn(0);

        groupsService.delete(groupId);
    }

    @Test
    public void closeNotClosedClosesStatements() throws SQLException {
        groupsService.close();

        verify(mockInsertStatement).close();
        verify(mockUpdateStatement).close();
        verify(mockDeleteStatement).close();
    }

    @Test
    public void closeAlreadyClosedDoesNothing() throws SQLException {
        groupsService.close();

        groupsService.close();

        verify(mockInsertStatement).close();
        verify(mockUpdateStatement).close();
        verify(mockDeleteStatement).close();
    }

    @Test
    public void isClosedNotClosedReturnsFalse() {
        boolean closed = groupsService.isClosed();

        assertFalse(closed);
    }

    @Test
    public void isClosedClosedReturnsTrue() throws SQLException {
        groupsService.close();

        boolean closed = groupsService.isClosed();

        assertTrue(closed);
    }

    private void setTestValues(Group group) {
        group.setGroupName("Test Group");
        group.setDescription("Test Description");
    }
}
