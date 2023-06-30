package com.nova.cls.network;

import com.nova.cls.data.mappers.ModelMapper;
import com.nova.cls.exceptions.MapperException;
import com.nova.cls.exceptions.RequestFailureException;

import java.util.List;
import java.util.Map;

public abstract class CollectionClient<Model> extends AuthenticatedClient {
    private final ModelMapper<Model> mapper;
    private final String baseRoute;

    public CollectionClient(LoginClient loginClient, Session session, ModelMapper<Model> mapper,
        String collectionName) {
        super(loginClient, session);
        this.mapper = mapper;
        this.baseRoute = "/api/" + collectionName.toLowerCase(); // e.g. /api/goods for goods
    }

    public Model findOne(long id) throws RequestFailureException {
        try {
            return mapper.fromReadJson(request(HttpMethod.GET, baseRoute + "/" + id));
        } catch (MapperException e) {
            // server is trusted to return valid JSON, so client cannot possibly know what to do if that is not the case
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<Model> findAll(Map<String, String> criteria) throws RequestFailureException {
        try {
            return mapper.fromReadManyJson(request(HttpMethod.GET,
                baseRoute + "?" + mapper.toCriteriaParams(criteria)));
        } catch (MapperException e) {
            // same rationale for RuntimeException as in findOne
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<Model> findAll() throws RequestFailureException {
        return findAll(Map.of());
    }

    public long create(Model model) throws RequestFailureException {
        return Long.parseLong(request(HttpMethod.POST, baseRoute, mapper.toCreateJson(model)));
    }

    public void update(Model model) throws RequestFailureException {
        request(HttpMethod.PUT, baseRoute + "/" + getId(model), mapper.toUpdateJson(model));
    }

    public void delete(long id) throws RequestFailureException {
        request(HttpMethod.DELETE, baseRoute + "/" + id);
    }

    protected abstract long getId(Model model);
}
