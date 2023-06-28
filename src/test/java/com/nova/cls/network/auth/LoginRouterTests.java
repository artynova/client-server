package com.nova.cls.network.auth;

import com.nova.cls.data.services.AuthService;
import com.nova.cls.data.services.GoodsService;
import com.nova.cls.network.Endpoint;
import com.nova.cls.network.auth.Auth;
import com.nova.cls.network.auth.LoginEndpoint;
import com.nova.cls.network.auth.LoginRouter;
import com.nova.cls.util.CloseableThreadLocal;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;

public class LoginRouterTests {

    private CloseableThreadLocal<AuthService> authServiceLocal;
    private LoginRouter goodRouter;

    @Before
    public void setup() {
        authServiceLocal = new CloseableThreadLocal<>(() -> Mockito.mock(AuthService.class));
        goodRouter = new LoginRouter(authServiceLocal);
    }

    @After
    public void teardown() throws Exception {
        authServiceLocal.close();
    }

    @Test
    public void checkEndpointsPresence() {
        Endpoint[] endpoints = goodRouter.getEndpoints();
        assertTrue(endpoints[0] instanceof LoginEndpoint);
    }
}
