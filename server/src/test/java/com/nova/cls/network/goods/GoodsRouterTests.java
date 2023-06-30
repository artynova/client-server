package com.nova.cls.network.goods;

import com.nova.cls.network.Endpoint;
import com.nova.cls.services.GoodsService;
import com.nova.cls.util.CloseableThreadLocal;
import com.nova.cls.util.Decryptor;
import com.nova.cls.util.Encryptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;

public class GoodsRouterTests {

    private CloseableThreadLocal<GoodsService> goodsServiceLocal;
    private GoodsRouter goodsRouter;

    @Before
    public void setup() {
        goodsServiceLocal = new CloseableThreadLocal<>(() -> Mockito.mock(GoodsService.class));
        goodsRouter = new GoodsRouter(goodsServiceLocal, Mockito.mock(Encryptor.class), Mockito.mock(Decryptor.class));
    }

    @After
    public void teardown() throws Exception {
        goodsServiceLocal.close();
    }

    @Test
    public void checkEndpointsPresence() {
        Endpoint[] endpoints = goodsRouter.getEndpoints();
        assertTrue(endpoints[0] instanceof GoodsCreateEndpoint);
        assertTrue(endpoints[1] instanceof GoodsReadEndpoint);
        assertTrue(endpoints[2] instanceof GoodsReadManyEndpoint);
        assertTrue(endpoints[3] instanceof GoodsUpdateEndpoint);
        assertTrue(endpoints[4] instanceof GoodsDeleteEndpoint);
    }
}
