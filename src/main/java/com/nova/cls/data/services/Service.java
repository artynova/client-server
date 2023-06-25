package com.nova.cls.data.services;

import com.nova.cls.data.BadRequestException;
import com.nova.cls.data.services.criteria.Criterion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Service<Model, ModelCriterion extends Criterion>
    implements AutoCloseable {
    protected final Connection connection;
    private final String tableName;
    private final String idName;
    private final String[] createFields;
    private final PreparedStatement insertStatement;
    private final PreparedStatement selectOneStatement;
    private final String[] updateFields;
    private final PreparedStatement updateStatement;
    private final PreparedStatement deleteStatement;
    private boolean closed = false;

    protected Service(Connection connection, String tableName, String idName,
        String[] createFields, String[] updateFields) {
        this.connection = connection;
        this.tableName = tableName;
        this.idName = idName;

        this.createFields = createFields;
        this.insertStatement = initInsertStatement();
        this.selectOneStatement = initSelectOneStatement();
        this.updateFields = updateFields;
        this.updateStatement = initUpdateStatement();
        this.deleteStatement = initDeleteStatement();
    }

    private PreparedStatement initInsertStatement() {
        String insertQuery =
            "INSERT INTO " + tableName + " (" + String.join(", ", createFields)
                + ") VALUES (" + ", ?".repeat(createFields.length).substring(2)
                + ") RETURNING " + idName + ";";
        try {
            return connection.prepareStatement(insertQuery);
        } catch (SQLException e) {
            throw new DatabaseFailureException(
                "Could not initialize insert " + tableName + " statement", e);
        }
    }

    private PreparedStatement initSelectOneStatement() {
        String selectQuery =
            "SELECT * FROM " + tableName + " WHERE " + idName + " = ?;";
        try {
            return connection.prepareStatement(selectQuery);
        } catch (SQLException e) {
            throw new DatabaseFailureException(
                "Could not initialize select one " + tableName + " statement",
                e);
        }
    }

    private PreparedStatement initUpdateStatement() {
        String updateQuery =
            "UPDATE " + tableName + " SET " + Arrays.stream(updateFields)
                .map(field -> field + " = ?").collect(Collectors.joining(", "))
                + " WHERE " + idName + " = ?;";
        try {
            return connection.prepareStatement(updateQuery);
        } catch (SQLException e) {
            throw new DatabaseFailureException(
                "Could not initialize update " + tableName + " statement", e);
        }
    }

    private PreparedStatement initDeleteStatement() {
        String deleteQuery =
            "DELETE FROM " + tableName + " WHERE " + idName + " = ?;";
        try {
            return connection.prepareStatement(deleteQuery);
        } catch (SQLException e) {
            throw new DatabaseFailureException(
                "Could not initialize delete " + tableName + " statement", e);
        }
    }

    public int create(Model model) {
        fillCreateParams(model, insertStatement);
        ResultSet generatedIdSet;
        int generatedId;
        try {
            generatedIdSet = insertStatement.executeQuery();
            generatedIdSet.next();
            generatedId = generatedIdSet.getInt(idName);
        } catch (SQLException e) {
            checkConstraintError(e,
                "Constraint failure when inserting " + model + " into "
                    + tableName + " table: " + e.getMessage());
            throw new DatabaseFailureException(
                "Failed to create " + model + " in " + tableName + " table: "
                    + e.getMessage(), e);
        }
        try {
            generatedIdSet.close();
        } catch (SQLException e) {
            throw new DatabaseFailureException(
                "Failed to close generated id result set");
        }
        return generatedId;
    }

    public Model findOne(int id) {
        fillId(selectOneStatement, 1, id);

        Model model;
        ResultSet set;
        try {
            set = selectOneStatement.executeQuery();
            if (!set.next()) {
                throw new BadRequestException(
                    "Requested entity " + id + " in " + tableName
                        + " table does not exist");
            }
            model = getModel(set);
        } catch (SQLException e) {
            throw new DatabaseFailureException(
                "Failed to find " + id + " in " + tableName + " table: "
                    + e.getMessage(), e);
        }

        try {
            set.close();
        } catch (SQLException e) {
            throw new DatabaseFailureException(
                "Could not close result set for select one from " + tableName
                    + " table", e);
        }
        return model;
    }

    /**
     * Updates the database record with id from given model with other information supplied in the model.
     */
    public void update(Model model) {
        fillUpdateParams(model, updateStatement);
        int id = getId(model);
        fillId(updateStatement, updateFields.length + 1, id);
        try {
            if (updateStatement.executeUpdate() < 1) {
                throw new BadRequestException(
                    "Updating nonexistent entry in " + tableName + ", " + id);
            }
        } catch (SQLException e) {
            checkConstraintError(e,
                "Constraint failure when updating " + id + " to " + model
                    + " in " + tableName + " table: " + e.getMessage());
            throw new BadRequestException(
                "Failed to update " + id + " to " + model + " in " + tableName
                    + " table: " + e.getMessage(), e);
        }
    }

    public void delete(int id) {
        fillId(deleteStatement, 1, id);
        try {
            if (deleteStatement.executeUpdate() < 1) {
                throw new BadRequestException(
                    "Deleting nonexistent entry in " + tableName + ", " + id);
            }
        } catch (SQLException e) {
            throw new BadRequestException(
                "Failed to delete " + id + " in " + tableName + " table: "
                    + e.getMessage(), e);
        }
    }

    @SafeVarargs
    public final List<Model> findAll(ModelCriterion... criteria) {
        String filteringPart = Arrays.stream(criteria).map(Criterion::getSql)
            .collect(Collectors.joining(" AND "));
        String query =
            "SELECT * FROM " + tableName + (filteringPart.isEmpty() ? ";"
                : " WHERE " + filteringPart + ";");
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);
            Object[] values = Arrays.stream(criteria)
                .flatMap(criterion -> Arrays.stream(criterion.getValues()))
                .toArray();
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }
        } catch (SQLException e) {
            throw new DatabaseFailureException(
                "Could not initiate find all " + tableName + " query", e);
        }
        List<Model> result = executeFindAll(statement);
        try {
            statement.close();
        } catch (SQLException e) {
            throw new DatabaseFailureException(
                "Could not close find all " + tableName + " statement");
        }
        return result;
    }

    private List<Model> executeFindAll(PreparedStatement statement) {
        List<Model> list = new ArrayList<>();
        try {
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                list.add(getModel(set));
            }
            set.close();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseFailureException(
                "Could not execute find all " + tableName + " query", e);
        }
    }

    protected abstract int getId(Model model);

    private void fillId(PreparedStatement statement, int index, int id) {
        try {
            statement.setObject(index, id);
        } catch (SQLException e) {
            throw new DatabaseFailureException(
                "Could not insert primary key into statement", e);
        }
    }

    // under normal programming exceptions in these are not expected
    protected abstract Model getModelUnsafe(ResultSet set) throws SQLException;

    private Model getModel(ResultSet set) {
        try {
            return getModelUnsafe(set);
        } catch (SQLException e) {
            throw new DatabaseFailureException(
                "Unexpected result set parsing error", e);
        }
    }

    protected abstract void fillCreateParamsUnsafe(Model model,
        PreparedStatement statement) throws SQLException;

    private void fillCreateParams(Model model, PreparedStatement statement) {
        try {
            fillCreateParamsUnsafe(model, statement);
        } catch (SQLException e) {
            throw new DatabaseFailureException(
                "Unexpected create statement fill error", e);
        }
    }

    protected abstract void fillUpdateParamsUnsafe(Model model,
        PreparedStatement statement) throws SQLException;

    private void fillUpdateParams(Model model, PreparedStatement statement) {
        try {
            fillUpdateParamsUnsafe(model, statement);
        } catch (SQLException e) {
            throw new DatabaseFailureException(
                "Unexpected update statement fill error", e);
        }
    }

    @Override
    public void close() throws Exception {
        if (isClosed()) {
            return;
        }
        insertStatement.close();
        updateStatement.close();
        deleteStatement.close();
        closed = true;
    }

    protected void checkConstraintError(SQLException e, String text) {
        if (e.getErrorCode() == DatabaseHandler.CONSTRAINT_ERROR_CODE) {
            throw new BadRequestException(text, e);
        }
    }

    public boolean isClosed() {
        return closed;
    }
}
