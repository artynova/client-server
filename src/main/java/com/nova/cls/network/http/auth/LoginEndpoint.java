package com.nova.cls.network.http.auth;

import com.nova.cls.data.exceptions.request.BadRequestException;
import com.nova.cls.data.models.UsersJsonMapper;
import com.nova.cls.data.services.AuthService;
import com.nova.cls.network.http.Codes;
import com.nova.cls.network.http.Endpoint;
import com.nova.cls.network.http.Method;
import com.nova.cls.network.http.Response;
import com.nova.cls.util.CloseableThreadLocal;

import java.util.Map;
import java.util.Set;

public class LoginEndpoint extends Endpoint {
    public static final Method METHOD = Method.POST;
    public static final String ROUTE = "";
    public static final Set<String> MANDATORY_QUERY_PARAMS = Set.of("login", "passwordHash");
    private final CloseableThreadLocal<AuthService> authServiceLocal;

    public LoginEndpoint(CloseableThreadLocal<AuthService> authServiceLocal) {
        // as per requirement, login and passwordHash are query parameters
        super(METHOD, ROUTE, MANDATORY_QUERY_PARAMS);
        this.authServiceLocal = authServiceLocal;
    }

    @Override
    protected Response process(Map<String, String> routeParams, Map<String, String> queryParams, String body)
        throws Exception {
        if (!body.isEmpty()) {
            throw new BadRequestException("Unexpected payload: " + body);
        }
        String login = queryParams.get("login");
        String passwordHash = queryParams.get("passwordHash");
        // can throw an UnauthorizedException that will get intercepted by the base endpoint code
        authServiceLocal.get().validateCredentials(login, passwordHash);
        return new Response(Codes.OK, authServiceLocal.get().generateJwtToken(login));
    }
}
