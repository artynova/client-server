package com.nova.cls.network.http.goods;

import com.nova.cls.data.models.Good;
import com.nova.cls.data.models.GoodsJsonMapper;
import com.nova.cls.data.services.GoodsService;
import com.nova.cls.network.http.Codes;
import com.nova.cls.network.http.Endpoint;
import com.nova.cls.network.http.Method;
import com.nova.cls.network.http.Response;
import com.nova.cls.util.CloseableThreadLocal;

import java.util.Map;

public class GoodCreateEndpoint extends Endpoint {
    // changed from PUT in requirements because Create endpoint is not idempotent
    public static final Method METHOD = Method.POST;
    public static final String ROUTE = "";
    private final CloseableThreadLocal<GoodsService> goodsServiceLocal;
    private final GoodsJsonMapper mapper = new GoodsJsonMapper();

    public GoodCreateEndpoint(CloseableThreadLocal<GoodsService> goodsServiceLocal) {
        super(METHOD, ROUTE);
        this.goodsServiceLocal = goodsServiceLocal;
    }

    @Override
    protected Response process(Map<String, String> routeParams, Map<String, String> queryParams, String body)
        throws Exception {
        Good good = mapper.fromCreateJson(body);
        return new Response(Codes.CREATED, String.valueOf(goodsServiceLocal.get().create(good)));
    }
}
