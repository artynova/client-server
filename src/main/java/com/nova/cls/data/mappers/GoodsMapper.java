package com.nova.cls.data.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nova.cls.data.models.Good;
import com.nova.cls.data.models.OffsetGoodQuantity;
import com.nova.cls.data.services.criteria.Criterion;
import com.nova.cls.data.services.criteria.goods.GroupIdCriterion;
import com.nova.cls.data.services.criteria.goods.ManufacturerPrefixCriterion;
import com.nova.cls.data.services.criteria.goods.MaxPriceCriterion;
import com.nova.cls.data.services.criteria.goods.MaxQuantityCriterion;
import com.nova.cls.data.services.criteria.goods.MinPriceCriterion;
import com.nova.cls.data.services.criteria.goods.MinQuantityCriterion;
import com.nova.cls.data.services.criteria.goods.NamePrefixCriterion;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

public class GoodsMapper extends ModelMapper<Good> {
    private static final Map<String, Function<String, ? extends Criterion<Good, ?>>> CRITERIA_PARSERS = Map.of(
        GroupIdCriterion.QUERY_PARAM_NAME,
        value -> new GroupIdCriterion(Long.parseLong(value)),
        ManufacturerPrefixCriterion.QUERY_PARAM_NAME,
        ManufacturerPrefixCriterion::new,
        MaxPriceCriterion.QUERY_PARAM_NAME,
        value -> new MaxPriceCriterion(Long.parseLong(value)),
        MaxQuantityCriterion.QUERY_PARAM_NAME,
        value -> new MaxQuantityCriterion(Long.parseLong(value)),
        MinPriceCriterion.QUERY_PARAM_NAME,
        value -> new MinPriceCriterion(Long.parseLong(value)),
        MinQuantityCriterion.QUERY_PARAM_NAME,
        value -> new MinQuantityCriterion(Long.parseLong(value)),
        NamePrefixCriterion.QUERY_PARAM_NAME,
        NamePrefixCriterion::new);

    public GoodsMapper() {
        super(Good.class, CRITERIA_PARSERS);
    }

    public OffsetGoodQuantity fromOffsetQuantityJson(String json) throws IOException {
        return updateReader.readValue(json, OffsetGoodQuantity.class);
    }

    public String toOffsetQuantityJson(OffsetGoodQuantity quantity) throws JsonProcessingException {
        return updateWriter.writeValueAsString(quantity);
    }
}
