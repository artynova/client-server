package com.nova.cls.network.auth;

import com.nova.cls.network.Endpoint;
import com.nova.cls.services.AuthService;
import com.nova.cls.util.CloseableThreadLocal;
import com.nova.cls.util.Decryptor;
import com.nova.cls.util.Encryptor;
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
        goodRouter = new LoginRouter(authServiceLocal, Mockito.mock(Encryptor.class), Mockito.mock(Decryptor.class));
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
