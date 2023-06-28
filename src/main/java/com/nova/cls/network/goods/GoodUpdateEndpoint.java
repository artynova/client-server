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

public class GoodUpdateEndpoint extends Endpoint {
    // changed from POST in requirements because Update endpoint is idempotent
    public static final Method METHOD = Method.PUT;
    public static final String ROUTE = "/{id}";
    private final CloseableThreadLocal<GoodsService> goodsServiceLocal;
    private final GoodsMapper mapper = new GoodsMapper();

    public GoodUpdateEndpoint(CloseableThreadLocal<GoodsService> goodsServiceLocal) {
        super(METHOD, ROUTE);
        this.goodsServiceLocal = goodsServiceLocal;
    }

    @Override
    protected Response process(Map<String, String> routeParams, Map<String, String> queryParams, String body)
        throws Exception {
        Good good = mapper.fromUpdateJson(body);
        good.setGoodId(Long.parseLong(routeParams.get("id")));
        goodsServiceLocal.get().update(good);
        return new Response(Codes.NO_CONTENT);
    }
}
