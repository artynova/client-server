package com.nova.cls.network.groups;

import com.nova.cls.network.Endpoint;
import com.nova.cls.network.Router;
import com.nova.cls.services.GroupsService;
import com.nova.cls.util.CloseableThreadLocal;
import com.nova.cls.util.Decryptor;
import com.nova.cls.util.Encryptor;

public class GroupsRouter extends Router {
    public static final String BASE_ROUTE = "/api/groups";

    public GroupsRouter(CloseableThreadLocal<GroupsService> groupsServiceLocal, Encryptor encryptor, Decryptor decryptor) {
        super(encryptor, decryptor, BASE_ROUTE,
            new GroupsCreateEndpoint(groupsServiceLocal),
            new GroupsReadEndpoint(groupsServiceLocal),
            new GroupsReadManyEndpoint(groupsServiceLocal),
            new GroupsUpdateEndpoint(groupsServiceLocal),
            new GroupsDeleteEndpoint(groupsServiceLocal));
    }
}
