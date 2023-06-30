package com.nova.cls.network.auth;

import com.nova.cls.network.Router;
import com.nova.cls.services.AuthService;
import com.nova.cls.util.CloseableThreadLocal;
import com.nova.cls.util.Decryptor;
import com.nova.cls.util.Encryptor;

public class LoginRouter extends Router {
    public static final String BASE_ROUTE = "/login";

    public LoginRouter(CloseableThreadLocal<AuthService> authServiceLocal, Encryptor encryptor, Decryptor decryptor) {
        super(encryptor, decryptor, BASE_ROUTE, new LoginEndpoint(authServiceLocal));
    }
}
