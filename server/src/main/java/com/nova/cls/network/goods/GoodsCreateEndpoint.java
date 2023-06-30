package com.nova.cls.network.goods;

import com.nova.cls.data.mappers.GoodsMapper;
import com.nova.cls.data.models.Good;
import com.nova.cls.network.Endpoint;
import com.nova.cls.network.HttpCode;
import com.nova.cls.network.HttpMethod;
import com.nova.cls.network.Response;
import com.nova.cls.services.GoodsService;
import com.nova.cls.util.CloseableThreadLocal;

import java.util.Map;

public class GoodsCreateEndpoint extends Endpoint {
    // changed from PUT in requirements because Create endpoint is not idempotent
    public static final HttpMethod HTTP_METHOD = HttpMethod.POST;
    public static final String ROUTE = "";
    private final CloseableThreadLocal<GoodsService> goodsServiceLocal;
    private final GoodsMapper mapper = new GoodsMapper();

    public GoodsCreateEndpoint(CloseableThreadLocal<GoodsService> goodsServiceLocal) {
        super(HTTP_METHOD, ROUTE);
        this.goodsServiceLocal = goodsServiceLocal;
    }

    @Override
    protected Response process(Map<String, String> routeParams, Map<String, String> queryParams, String body)
        throws Exception {
        Good good = mapper.fromCreateJson(body);
        return new Response(HttpCode.CREATED, String.valueOf(goodsServiceLocal.get().create(good)));
    }
}
