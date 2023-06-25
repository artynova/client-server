package com.nova.cls.data.services;

import com.nova.cls.data.BadRequestException;
import com.nova.cls.data.models.Good;
import com.nova.cls.data.services.criteria.goods.GoodsCriterion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class GoodsService extends Service<Good, GoodsCriterion> {
    private static final String TABLE_NAME = "Goods";
    private static final String ID_NAME = "goodId";
    private static final String[] CREATE_FIELDS =
        new String[] {"goodName", "description", "manufacturer", "price",
            "groupId"};
    private static final String[] UPDATE_FIELDS =
        Arrays.copyOf(CREATE_FIELDS, 4);


    private final PreparedStatement offsetQuantityStatement;

    public GoodsService(Connection connection) {
        super(connection, TABLE_NAME, ID_NAME, CREATE_FIELDS, UPDATE_FIELDS);
        this.offsetQuantityStatement = initOffsetQuantityStatement();
    }

    private PreparedStatement initOffsetQuantityStatement() {
        String query =
            "UPDATE Goods SET quantity = quantity + ? WHERE goodId = ?;";
        try {
            return connection.prepareStatement(query);
        } catch (SQLException e) {
            throw new DatabaseFailureException(
                "Could not initialize offset quantity of Goods statement", e);
        }
    }

    public void addQuantity(int goodId, int addedQuantity) {
        offsetQuantity(goodId, addedQuantity);
    }

    public void subtractQuantity(int goodId, int subtractedQuantity) {
        offsetQuantity(goodId, -subtractedQuantity);
    }

    private void offsetQuantity(int goodId, int offsetQuantity) {
        try {
            offsetQuantityStatement.setObject(1, offsetQuantity);
            offsetQuantityStatement.setObject(2, goodId);
        } catch (SQLException e) {
            throw new DatabaseFailureException(
                "Unexpected offset quantity statement fill error", e);
        }
        try {
            if (offsetQuantityStatement.executeUpdate() < 1) {
                throw new BadRequestException(
                    "Offsetting quantity of nonexistent entity " + goodId
                        + " in Goods table");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == DatabaseHandler.CONSTRAINT_ERROR_CODE) {
                throw new BadRequestException(
                    "Constraint failure when offsetting quantity of entity "
                        + goodId + " in Goods table: " + e.getMessage(), e);
            }
            throw new DatabaseFailureException(
                "Could not execute offset quantity query", e);
        }
    }

    @Override
    protected Good getModelUnsafe(ResultSet set) throws SQLException {
        Good good = new Good();
        good.setGoodId(set.getInt("goodId"));
        good.setGoodName(set.getString("goodName"));
        good.setDescription(set.getString("description"));
        good.setManufacturer(set.getString("manufacturer"));
        good.setQuantity(set.getInt("quantity"));
        good.setPrice(set.getInt("price"));
        good.setGroupId(set.getInt("groupId"));
        return good;
    }

    @Override
    protected void fillCreateParamsUnsafe(Good good,
        PreparedStatement statement) throws SQLException {
        fillUpdateParamsUnsafe(good, statement);
        statement.setObject(5, good.getGroupId());
    }

    @Override
    protected void fillUpdateParamsUnsafe(Good good,
        PreparedStatement statement) throws SQLException {
        statement.setObject(1, good.getGoodName());
        statement.setObject(2, good.getDescription());
        statement.setObject(3, good.getManufacturer());
        statement.setObject(4, good.getPrice());
    }

    @Override
    protected int getId(Good good) {
        return good.getGoodId();
    }

    @Override
    public void close() throws Exception {
        if (isClosed()) {
            return;
        }
        super.close();
        offsetQuantityStatement.close();
    }
}
