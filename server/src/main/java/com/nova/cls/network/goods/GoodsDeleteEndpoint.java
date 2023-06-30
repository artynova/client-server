package com.nova.cls.network.goods;

import com.nova.cls.network.Endpoint;
import com.nova.cls.network.HttpCode;
import com.nova.cls.network.HttpMethod;
import com.nova.cls.network.Response;
import com.nova.cls.services.GoodsService;
import com.nova.cls.util.CloseableThreadLocal;

import java.util.Map;

public class GoodsDeleteEndpoint extends Endpoint {
    public static final HttpMethod HTTP_METHOD = HttpMethod.DELETE;
    public static final String ROUTE = "/{id}";
    private final CloseableThreadLocal<GoodsService> goodsServiceLocal;

    public GoodsDeleteEndpoint(CloseableThreadLocal<GoodsService> goodsServiceLocal) {
        super(HTTP_METHOD, ROUTE);
        this.goodsServiceLocal = goodsServiceLocal;
    }

    @Override
    protected Response process(Map<String, String> routeParams, Map<String, String> queryParams, String body)
        throws Exception {
        Long id = Long.parseLong(routeParams.get("id"));
        goodsServiceLocal.get().delete(id);
        return new Response(HttpCode.NO_CONTENT);
    }
}
