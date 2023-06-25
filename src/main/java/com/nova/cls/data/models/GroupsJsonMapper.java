package com.nova.cls.data.models;

import com.nova.cls.data.services.criteria.groups.GroupsCriteriaAggregate;

public class GroupsJsonMapper extends ModelJsonMapper<Group> {
    public GroupsJsonMapper() {
        super(Group.class, GroupsCriteriaAggregate.class);
    }
}
