package com.nova.cls.network.groups;

import com.nova.cls.data.criteria.Criterion;
import com.nova.cls.data.mappers.GroupsMapper;
import com.nova.cls.data.models.Group;
import com.nova.cls.network.Endpoint;
import com.nova.cls.network.HttpCode;
import com.nova.cls.network.HttpMethod;
import com.nova.cls.network.Response;
import com.nova.cls.services.GroupsService;
import com.nova.cls.util.CloseableThreadLocal;

import java.util.List;
import java.util.Map;

public class GroupsReadManyEndpoint extends Endpoint {
    public static final HttpMethod HTTP_METHOD = HttpMethod.GET;
    public static final String ROUTE = "";
    private final CloseableThreadLocal<GroupsService> groupsServiceLocal;
    private final GroupsMapper mapper = new GroupsMapper();

    public GroupsReadManyEndpoint(CloseableThreadLocal<GroupsService> groupsServiceLocal) {
        super(HTTP_METHOD, ROUTE);
        this.groupsServiceLocal = groupsServiceLocal;
    }

    @Override
    protected Response process(Map<String, String> routeParams, Map<String, String> queryParams, String body)
        throws Exception {
        List<? extends Criterion<Group, ?>> criteria = mapper.fromCriteriaParams(queryParams);
        List<Group> groups = groupsServiceLocal.get().findAll(criteria);
        return new Response(HttpCode.OK, mapper.toReadManyJson(groups));
    }
}
