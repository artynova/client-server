package com.nova.cls.network.groups;

import com.nova.cls.data.mappers.GroupsMapper;
import com.nova.cls.data.models.Group;
import com.nova.cls.network.Endpoint;
import com.nova.cls.network.HttpCode;
import com.nova.cls.network.HttpMethod;
import com.nova.cls.network.Response;
import com.nova.cls.services.GroupsService;
import com.nova.cls.util.CloseableThreadLocal;
import com.nova.cls.util.Decryptor;
import com.nova.cls.util.Encryptor;

import java.util.Map;

public class GroupsUpdateEndpoint extends Endpoint {
    // changed from POST in requirements because Update endpoint is idempotent
    public static final HttpMethod HTTP_METHOD = HttpMethod.PUT;
    public static final String ROUTE = "/{id}";
    private final CloseableThreadLocal<GroupsService> groupsServiceLocal;
    private final GroupsMapper mapper = new GroupsMapper();

    public GroupsUpdateEndpoint(CloseableThreadLocal<GroupsService> groupsServiceLocal) {
        super(HTTP_METHOD, ROUTE);
        this.groupsServiceLocal = groupsServiceLocal;
    }

    @Override
    protected Response process(Map<String, String> routeParams, Map<String, String> queryParams, String body)
        throws Exception {
        Group group = mapper.fromUpdateJson(body);
        group.setGroupId(Long.parseLong(routeParams.get("id")));
        groupsServiceLocal.get().update(group);
        return new Response(HttpCode.NO_CONTENT);
    }
}
