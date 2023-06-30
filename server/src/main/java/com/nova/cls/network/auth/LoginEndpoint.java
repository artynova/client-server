package com.nova.cls.network.auth;

import com.nova.cls.data.mappers.UsersMapper;
import com.nova.cls.data.models.User;
import com.nova.cls.network.Endpoint;
import com.nova.cls.network.HttpCode;
import com.nova.cls.network.HttpMethod;
import com.nova.cls.network.Response;
import com.nova.cls.services.AuthService;
import com.nova.cls.util.CloseableThreadLocal;

import java.util.Map;

public class LoginEndpoint extends Endpoint {
    public static final HttpMethod HTTP_METHOD = HttpMethod.POST;
    public static final String ROUTE = "";
    private final UsersMapper mapper = new UsersMapper();
    private final CloseableThreadLocal<AuthService> authServiceLocal;

    public LoginEndpoint(CloseableThreadLocal<AuthService> authServiceLocal) {
        // as per requirement, login and passwordHash are query parameters
        super(HTTP_METHOD, ROUTE);
        this.authServiceLocal = authServiceLocal;
    }

    @Override
    protected Response process(Map<String, String> routeParams, Map<String, String> queryParams, String body)
        throws Exception {
        User user = mapper.fromLoginJson(body);
        // can throw an UnauthorizedException or ForbiddenException that will get intercepted by the base endpoint code
        authServiceLocal.get().validateCredentials(user.getLogin(), user.getPasswordHash());
        return new Response(HttpCode.OK, authServiceLocal.get().generateJwtToken(user.getLogin()));
    }
}
