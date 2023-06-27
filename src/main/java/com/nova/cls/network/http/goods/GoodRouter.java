package com.nova.cls.network.http.goods;

import com.nova.cls.data.services.GoodsService;
import com.nova.cls.network.http.Router;
import com.nova.cls.util.CloseableThreadLocal;

public class GoodRouter extends Router {
    public static final String BASE_ROUTE = "/api/good";

    public GoodRouter(CloseableThreadLocal<GoodsService> goodsServiceLocal) {
        super(BASE_ROUTE, new GoodCreateEndpoint(goodsServiceLocal), new GoodReadEndpoint(goodsServiceLocal),
            new GoodUpdateEndpoint(goodsServiceLocal), new GoodDeleteEndpoint(goodsServiceLocal));
    }
}
