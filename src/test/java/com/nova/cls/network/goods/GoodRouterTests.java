package com.nova.cls.network.goods;

import com.nova.cls.data.services.GoodsService;
import com.nova.cls.network.Endpoint;
import com.nova.cls.util.CloseableThreadLocal;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;

public class GoodRouterTests {

    private CloseableThreadLocal<GoodsService> goodsServiceLocal;
    private GoodRouter goodRouter;

    @Before
    public void setup() {
        goodsServiceLocal = new CloseableThreadLocal<>(() -> Mockito.mock(GoodsService.class));
        goodRouter = new GoodRouter(goodsServiceLocal);
    }

    @After
    public void teardown() throws Exception {
        goodsServiceLocal.close();
    }

    @Test
    public void checkEndpointsPresence() {
        Endpoint[] endpoints = goodRouter.getEndpoints();
        assertTrue(endpoints[0] instanceof GoodCreateEndpoint);
        assertTrue(endpoints[1] instanceof GoodReadEndpoint);
        assertTrue(endpoints[2] instanceof GoodReadManyEndpoint);
        assertTrue(endpoints[3] instanceof GoodUpdateEndpoint);
        assertTrue(endpoints[4] instanceof GoodDeleteEndpoint);
    }
}
