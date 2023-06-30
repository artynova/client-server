package com.nova.cls.network;

import com.nova.cls.data.mappers.GroupsMapper;
import com.nova.cls.data.models.Group;

public class GroupsClient extends CollectionClient<Group> {
    public GroupsClient(LoginClient loginClient, Session session) {
        super(loginClient, session, new GroupsMapper(), "groups");
    }

    @Override
    protected long getId(Group good) {
        return good.getGroupId();
    }
}
