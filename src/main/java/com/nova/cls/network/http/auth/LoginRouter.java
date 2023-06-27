package com.nova.cls.network.http.auth;

import com.nova.cls.data.services.AuthService;
import com.nova.cls.network.http.Router;
import com.nova.cls.util.CloseableThreadLocal;

public class LoginRouter extends Router {
    public static final String BASE_ROUTE = "/login";
    public LoginRouter(CloseableThreadLocal<AuthService> authServiceLocal) {
        super(BASE_ROUTE, new LoginEndpoint(authServiceLocal));
    }
}
