package com.nova.cls.network.auth;

import com.nova.cls.exceptions.request.ForbiddenException;
import com.nova.cls.data.services.AuthService;
import com.nova.cls.network.constants.Codes;
import com.nova.cls.util.CloseableThreadLocal;
import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

public class Auth extends Authenticator {
    public static final String REALM = "cls";

    private final CloseableThreadLocal<AuthService> authServiceLocal;

    public Auth(CloseableThreadLocal<AuthService> authServiceLocal) {
        this.authServiceLocal = authServiceLocal;
    }

    @Override
    public Result authenticate(HttpExchange exchange) {
        Headers headers = exchange.getRequestHeaders();
        String authorizationHeader = headers.getFirst("Authorization");

        // test for invalid auth header
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return new Failure(Codes.FORBIDDEN);
        }
        String jwtToken = authorizationHeader.substring("Bearer ".length());

        try {
            String userName = authServiceLocal.get().validateJwtToken(jwtToken);
            return new Success(new HttpPrincipal(userName, REALM));
        } catch (ForbiddenException e) {
            return new Failure(Codes.FORBIDDEN);
        }
    }
}
