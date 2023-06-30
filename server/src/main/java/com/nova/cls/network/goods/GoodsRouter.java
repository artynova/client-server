package com.nova.cls.network.goods;

import com.nova.cls.network.Router;
import com.nova.cls.services.GoodsService;
import com.nova.cls.util.CloseableThreadLocal;
import com.nova.cls.util.Decryptor;
import com.nova.cls.util.Encryptor;

public class GoodsRouter extends Router {
    public static final String BASE_ROUTE = "/api/goods";

    public GoodsRouter(CloseableThreadLocal<GoodsService> goodsServiceLocal, Encryptor encryptor, Decryptor decryptor) {
        super(encryptor, decryptor, BASE_ROUTE,
            new GoodsCreateEndpoint(goodsServiceLocal),
            new GoodsReadEndpoint(goodsServiceLocal),
            new GoodsReadManyEndpoint(goodsServiceLocal),
            new GoodsUpdateEndpoint(goodsServiceLocal),
            new GoodsDeleteEndpoint(goodsServiceLocal),
            new GoodsOffsetQuantityEndpoint(goodsServiceLocal));
    }
}
