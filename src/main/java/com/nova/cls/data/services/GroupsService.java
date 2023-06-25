package com.nova.cls.data.services;

import com.nova.cls.data.models.Group;
import com.nova.cls.data.services.criteria.groups.GroupsCriterion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class GroupsService extends Service<Group, GroupsCriterion> {
    private static final String TABLE_NAME = "Groups";
    private static final String ID_NAME = "groupId";
    private static final String[] CREATE_FIELDS = new String[] {"groupName", "description"};
    private static final String[] UPDATE_FIELDS = Arrays.copyOf(CREATE_FIELDS, CREATE_FIELDS.length);

    public GroupsService(Connection connection) {
        super(connection, TABLE_NAME, ID_NAME, CREATE_FIELDS, UPDATE_FIELDS);
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
    protected long getId(Group group) {
        return group.getGroupId();
    }
}
