package com.nova.cls.services;

import com.nova.cls.data.models.Group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class GroupsService extends CrudService<Group> {
    private static final String TABLE_NAME = "Groups";
    private static final String ID_NAME = "groupId";
    private static final String[] READ_FIELDS = new String[] {"groupId", "groupName", "description"};
    private static final String[] CREATE_FIELDS = Arrays.copyOfRange(READ_FIELDS, 1, READ_FIELDS.length);
    private static final String[] UPDATE_FIELDS = Arrays.copyOf(CREATE_FIELDS, CREATE_FIELDS.length);

    public GroupsService(Connection connection) {
        super(connection, TABLE_NAME, ID_NAME, CREATE_FIELDS, UPDATE_FIELDS, READ_FIELDS);
    }

    @Override
    protected Group getModelUnsafe(ResultSet set) throws SQLException {
        Group group = new Group();
        group.setGroupId(set.getLong(ID_NAME));
        group.setGroupName(set.getString("groupName"));
        group.setDescription(set.getString("description"));
        return group;
    }

    @Override
    protected void fillCreateParamsUnsafe(Group group, PreparedStatement statement) throws SQLException {
        statement.setObject(1, group.getGroupName());
        statement.setObject(2, group.getDescription());
    }

    @Override
    protected void fillUpdateParamsUnsafe(Group group, PreparedStatement statement) throws SQLException {
        fillCreateParamsUnsafe(group, statement);
    }

    @Override
    protected Long getId(Group group) {
        return group.getGroupId();
    }
}
