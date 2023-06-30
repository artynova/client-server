package com.nova.cls.services;

import com.nova.cls.data.models.Good;
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

public class GoodsServiceTests {

    private GoodsService goodsService;
    @Mock
    private PreparedStatement mockInsertStatement;
    @Mock
    private PreparedStatement mockSelectOneStatement;
    @Mock
    private PreparedStatement mockUpdateStatement;
    @Mock
    private PreparedStatement mockDeleteStatement;
    @Mock
    private PreparedStatement mockOffsetQuantityStatement;

    @Before
    public void setUp() throws SQLException {
        Connection mockConnection = mock(Connection.class);
        MockitoAnnotations.openMocks(this);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockInsertStatement,
            mockSelectOneStatement,
            mockUpdateStatement,
            mockDeleteStatement,
            mockOffsetQuantityStatement);

        goodsService = new GoodsService(mockConnection);
    }

    @After
    public void tearDown() throws SQLException {
        goodsService.close();
    }

    @Test
    public void createValidModelReturnsGeneratedId() throws SQLException {
        Good good = new Good();
        setTestValues(good);

        ResultSet mockGeneratedIdSet = mock(ResultSet.class);
        when(mockGeneratedIdSet.getLong(anyString())).thenReturn(1L);
        when(mockInsertStatement.executeQuery()).thenReturn(mockGeneratedIdSet);

        Long generatedId = goodsService.create(good);

        assertEquals(1L, generatedId.longValue());
        verify(mockInsertStatement).setObject(1, good.getGoodName());
        verify(mockInsertStatement).setObject(2, good.getDescription());
        verify(mockInsertStatement).setObject(3, good.getManufacturer());
        verify(mockInsertStatement).setObject(4, good.getPrice());
        verify(mockGeneratedIdSet).close();
    }

    @Test(expected = ConflictException.class)
    public void createConstraintViolationThrowsConflictException() throws SQLException {
        Good good = new Good();
        setTestValues(good);

        when(mockInsertStatement.executeQuery()).thenThrow(new SQLException("Constraint violation",
            null,
            ConstraintExceptionAdapter.CONSTRAINT_ERROR_CODE));

        goodsService.create(good);
    }

    @Test
    public void findOneExistingIdReturnsModel() throws SQLException {
        Long goodId = 1L;
        Good expectedGood = new Good();
        expectedGood.setGoodId(goodId);
        setTestValues(expectedGood);

        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong(anyString())).thenReturn(goodId);
        when(mockResultSet.getString("goodName")).thenReturn("Test Good");
        when(mockResultSet.getString("description")).thenReturn("Test Description");
        when(mockResultSet.getString("manufacturer")).thenReturn("Test Manufacturer");
        when(mockResultSet.getLong("price")).thenReturn(1000L);
        when(mockSelectOneStatement.executeQuery()).thenReturn(mockResultSet);

        Good actualGood = goodsService.findOne(goodId);

        assertNotNull(actualGood);
        assertEquals(expectedGood.getGoodId(), actualGood.getGoodId());
        assertEquals(expectedGood.getGoodName(), actualGood.getGoodName());
        assertEquals(expectedGood.getDescription(), actualGood.getDescription());
        assertEquals(expectedGood.getManufacturer(), actualGood.getManufacturer());
        assertEquals(expectedGood.getPrice(), actualGood.getPrice());
        verify(mockSelectOneStatement).setObject(1, goodId);
        verify(mockResultSet).close();
    }

    @Test(expected = NotFoundException.class)
    public void findOneNonexistentIdThrowsNotFoundException() throws SQLException {
        Long goodId = 1L;

        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockResultSet.next()).thenReturn(false);
        when(mockSelectOneStatement.executeQuery()).thenReturn(mockResultSet);

        goodsService.findOne(goodId);
    }

    @Test
    public void updateValidModelUpdatesRecord() throws SQLException {
        Good good = new Good();
        good.setGoodId(1L);
        setTestValues(good);

        Long goodId = good.getGoodId();
        when(mockUpdateStatement.executeUpdate()).thenReturn(1);

        goodsService.update(good);

        verify(mockUpdateStatement).setObject(1, good.getGoodName());
        verify(mockUpdateStatement).setObject(2, good.getDescription());
        verify(mockUpdateStatement).setObject(3, good.getManufacturer());
        verify(mockUpdateStatement).setObject(4, good.getPrice());
        verify(mockUpdateStatement).setObject(5, goodId);
    }

    @Test(expected = NotFoundException.class)
    public void updateNonexistentModelThrowsNotFoundException() throws SQLException {
        Good good = new Good();
        good.setGoodId(1L);
        setTestValues(good);

        when(mockUpdateStatement.executeUpdate()).thenReturn(0);

        goodsService.update(good);
    }

    @Test(expected = ConflictException.class)
    public void updateConstraintViolationThrowsConflictException() throws SQLException {
        Good good = new Good();
        good.setGoodId(1L);
        setTestValues(good);

        when(mockUpdateStatement.executeUpdate()).thenThrow(new SQLException("Constraint violation",
            null,
            ConstraintExceptionAdapter.CONSTRAINT_ERROR_CODE));

        goodsService.update(good);
    }

    @Test
    public void deleteExistingIdDeletesRecord() throws SQLException {
        Long goodId = 1L;

        when(mockDeleteStatement.executeUpdate()).thenReturn(1);

        goodsService.delete(goodId);

        verify(mockDeleteStatement).setObject(1, goodId);
    }

    @Test(expected = NotFoundException.class)
    public void deleteNonexistentIdThrowsNotFoundException() throws SQLException {
        Long goodId = 1L;

        when(mockDeleteStatement.executeUpdate()).thenReturn(0);

        goodsService.delete(goodId);
    }

    @Test
    public void closeNotClosedClosesStatements() throws SQLException {
        goodsService.close();

        verify(mockInsertStatement).close();
        verify(mockUpdateStatement).close();
        verify(mockDeleteStatement).close();
    }

    @Test
    public void closeAlreadyClosedDoesNothing() throws SQLException {
        goodsService.close();

        goodsService.close();

        verify(mockInsertStatement).close();
        verify(mockUpdateStatement).close();
        verify(mockDeleteStatement).close();
    }

    @Test
    public void isClosedNotClosedReturnsFalse() {
        boolean closed = goodsService.isClosed();

        assertFalse(closed);
    }

    @Test
    public void isClosedClosedReturnsTrue() throws SQLException {
        goodsService.close();

        boolean closed = goodsService.isClosed();

        assertTrue(closed);
    }

    @Test
    public void offsetValidQuantityOffsetsOnRecord() throws SQLException {
        Long goodId = 1L;
        Long offset = 100L;

        when(mockOffsetQuantityStatement.executeUpdate()).thenReturn(1);

        goodsService.offsetQuantity(goodId, offset);

        verify(mockOffsetQuantityStatement).setObject(1, offset);
        verify(mockOffsetQuantityStatement).setObject(2, goodId);
    }

    @Test(expected = NotFoundException.class)
    public void offsetQuantityOnNonexistentThrowsNotFoundException() throws SQLException {
        Long goodId = 1L;
        Long offset = 100L;

        when(mockOffsetQuantityStatement.executeUpdate()).thenReturn(0);

        goodsService.offsetQuantity(goodId, offset);
    }

    @Test(expected = ConflictException.class)
    public void offsetQuantityConstraintViolationThrowsConflictException() throws SQLException {
        Long goodId = 1L;
        Long offset = -100L;

        when(mockOffsetQuantityStatement.executeUpdate()).thenThrow(new SQLException("Constraint violation",
            null,
            ConstraintExceptionAdapter.CONSTRAINT_ERROR_CODE));

        goodsService.offsetQuantity(goodId, offset);
    }

    private void setTestValues(Good good) {
        good.setGoodName("Test Good");
        good.setDescription("Test Description");
        good.setManufacturer("Test Manufacturer");
        good.setPrice(1000L);
    }
}
