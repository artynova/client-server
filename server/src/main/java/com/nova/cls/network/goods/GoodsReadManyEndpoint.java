package com.nova.cls.network.goods;

import com.nova.cls.data.criteria.Criterion;
import com.nova.cls.data.criteria.goods.GroupIdCriterion;
import com.nova.cls.data.criteria.goods.ManufacturerPrefixCriterion;
import com.nova.cls.data.criteria.goods.MaxPriceCriterion;
import com.nova.cls.data.criteria.goods.MaxQuantityCriterion;
import com.nova.cls.data.criteria.goods.MinPriceCriterion;
import com.nova.cls.data.criteria.goods.MinQuantityCriterion;
import com.nova.cls.data.criteria.goods.NamePrefixCriterion;
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

import java.util.List;
import java.util.Map;
import java.util.Set;

public class GoodsReadManyEndpoint extends Endpoint {
    public static final HttpMethod HTTP_METHOD = HttpMethod.GET;
    public static final String ROUTE = "";
    public static final Set<String> POSSIBLE_QUERY_PARAMS = Set.of(GroupIdCriterion.QUERY_PARAM_NAME,
        ManufacturerPrefixCriterion.QUERY_PARAM_NAME,
        MaxPriceCriterion.QUERY_PARAM_NAME,
        MaxQuantityCriterion.QUERY_PARAM_NAME,
        MinPriceCriterion.QUERY_PARAM_NAME,
        MinQuantityCriterion.QUERY_PARAM_NAME,
        NamePrefixCriterion.QUERY_PARAM_NAME);
    private final CloseableThreadLocal<GoodsService> goodsServiceLocal;
    private final GoodsMapper mapper = new GoodsMapper();

    public GoodsReadManyEndpoint(CloseableThreadLocal<GoodsService> goodsServiceLocal) {
        super(HTTP_METHOD,
            ROUTE,
            POSSIBLE_QUERY_PARAMS);
        this.goodsServiceLocal = goodsServiceLocal;
    }

    @Override
    protected Response process(Map<String, String> routeParams, Map<String, String> queryParams, String body)
        throws Exception {
        List<? extends Criterion<Good, ?>> criteria = mapper.fromCriteriaParams(queryParams);
        List<Good> goods = goodsServiceLocal.get().findAll(criteria);
        return new Response(HttpCode.OK, mapper.toReadManyJson(goods));
    }
}
