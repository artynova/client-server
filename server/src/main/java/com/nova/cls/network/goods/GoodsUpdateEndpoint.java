package com.nova.cls.network.goods;

import com.nova.cls.data.mappers.GoodsMapper;
import com.nova.cls.data.models.Good;
import com.nova.cls.network.Endpoint;
import com.nova.cls.network.HttpCode;
import com.nova.cls.network.HttpMethod;
import com.nova.cls.network.Response;
import com.nova.cls.services.GoodsService;
import com.nova.cls.util.CloseableThreadLocal;
import com.nova.cls.util.Decryptor;
import com.nova.cls.util.Encryptor;

import java.util.Map;

public class GoodsUpdateEndpoint extends Endpoint {
    // changed from POST in requirements because Update endpoint is idempotent
    public static final HttpMethod HTTP_METHOD = HttpMethod.PUT;
    public static final String ROUTE = "/{id}";
    private final CloseableThreadLocal<GoodsService> goodsServiceLocal;
    private final GoodsMapper mapper = new GoodsMapper();

    public GoodsUpdateEndpoint(CloseableThreadLocal<GoodsService> goodsServiceLocal) {
        super(HTTP_METHOD, ROUTE);
        this.goodsServiceLocal = goodsServiceLocal;
    }

    @Override
    protected Response process(Map<String, String> routeParams, Map<String, String> queryParams, String body)
        throws Exception {
        Good good = mapper.fromUpdateJson(body);
        good.setGoodId(Long.parseLong(routeParams.get("id")));
        goodsServiceLocal.get().update(good);
        return new Response(HttpCode.NO_CONTENT);
    }
}
