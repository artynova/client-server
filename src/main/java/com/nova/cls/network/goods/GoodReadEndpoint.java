package com.nova.cls.network.goods;

import com.nova.cls.data.models.Good;
import com.nova.cls.data.mappers.GoodsMapper;
import com.nova.cls.data.services.GoodsService;
import com.nova.cls.network.constants.Codes;
import com.nova.cls.network.Endpoint;
import com.nova.cls.network.constants.Method;
import com.nova.cls.network.Response;
import com.nova.cls.util.CloseableThreadLocal;

import java.util.Map;

public class GoodReadEndpoint extends Endpoint {
    public static final Method METHOD = Method.GET;
    public static final String ROUTE = "/{id}";
    private final CloseableThreadLocal<GoodsService> goodsServiceLocal;
    private final GoodsMapper mapper = new GoodsMapper();

    public GoodReadEndpoint(CloseableThreadLocal<GoodsService> goodsServiceLocal) {
        super(METHOD, ROUTE);
        this.goodsServiceLocal = goodsServiceLocal;
    }

    @Override
    protected Response process(Map<String, String> routeParams, Map<String, String> queryParams, String body)
        throws Exception {
        Long id = Long.parseLong(routeParams.get("id"));
        Good good = goodsServiceLocal.get().findOne(id);
        return new Response(Codes.OK, mapper.toReadJson(good));
    }
}
