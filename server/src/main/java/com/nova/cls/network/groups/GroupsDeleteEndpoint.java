package com.nova.cls.network.groups;

import com.nova.cls.network.Endpoint;
import com.nova.cls.network.HttpCode;
import com.nova.cls.network.HttpMethod;
import com.nova.cls.network.Response;
import com.nova.cls.services.GroupsService;
import com.nova.cls.util.CloseableThreadLocal;
import com.nova.cls.util.Decryptor;
import com.nova.cls.util.Encryptor;

import java.util.Map;

public class GroupsDeleteEndpoint extends Endpoint {
    public static final HttpMethod HTTP_METHOD = HttpMethod.DELETE;
    public static final String ROUTE = "/{id}";
    private final CloseableThreadLocal<GroupsService> groupsServiceLocal;

    public GroupsDeleteEndpoint(CloseableThreadLocal<GroupsService> groupsServiceLocal) {
        super(HTTP_METHOD, ROUTE);
        this.groupsServiceLocal = groupsServiceLocal;
    }

    @Override
    protected Response process(Map<String, String> routeParams, Map<String, String> queryParams, String body)
        throws Exception {
        Long id = Long.parseLong(routeParams.get("id"));
        groupsServiceLocal.get().delete(id);
        return new Response(HttpCode.NO_CONTENT);
    }
}
