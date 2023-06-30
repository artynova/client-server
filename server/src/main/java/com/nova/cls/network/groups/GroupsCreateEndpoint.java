package com.nova.cls.network.groups;

import com.nova.cls.data.mappers.GroupsMapper;
import com.nova.cls.data.models.Group;
import com.nova.cls.network.Endpoint;
import com.nova.cls.network.HttpCode;
import com.nova.cls.network.HttpMethod;
import com.nova.cls.network.Response;
import com.nova.cls.services.GroupsService;
import com.nova.cls.util.CloseableThreadLocal;

import java.util.Map;

public class GroupsCreateEndpoint extends Endpoint {
    // changed from PUT in requirements because Create endpoint is not idempotent
    public static final HttpMethod HTTP_METHOD = HttpMethod.POST;
    public static final String ROUTE = "";
    private final CloseableThreadLocal<GroupsService> groupsServiceLocal;
    private final GroupsMapper mapper = new GroupsMapper();

    public GroupsCreateEndpoint(CloseableThreadLocal<GroupsService> groupsServiceLocal) {
        super(HTTP_METHOD, ROUTE);
        this.groupsServiceLocal = groupsServiceLocal;
    }

    @Override
    protected Response process(Map<String, String> routeParams, Map<String, String> queryParams, String body)
        throws Exception {
        Group group = mapper.fromCreateJson(body);
        return new Response(HttpCode.CREATED, String.valueOf(groupsServiceLocal.get().create(group)));
    }
}
