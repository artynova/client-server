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

public class GoodsReadEndpoint extends Endpoint {
    public static final HttpMethod HTTP_METHOD = HttpMethod.GET;
    public static final String ROUTE = "/{id}";
    private final CloseableThreadLocal<GoodsService> goodsServiceLocal;
    private final GoodsMapper mapper = new GoodsMapper();

    public GoodsReadEndpoint(CloseableThreadLocal<GoodsService> goodsServiceLocal) {
        super(HTTP_METHOD, ROUTE);
        this.goodsServiceLocal = goodsServiceLocal;
    }

    @Override
    protected Response process(Map<String, String> routeParams, Map<String, String> queryParams, String body)
        throws Exception {
        Long id = Long.parseLong(routeParams.get("id"));
        Good good = goodsServiceLocal.get().findOne(id);
        return new Response(HttpCode.OK, mapper.toReadJson(good));
    }
}
