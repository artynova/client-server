package com.nova.cls.data.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nova.cls.data.services.criteria.goods.GoodsCriteriaAggregate;

import java.io.IOException;

public class GoodsJsonMapper extends ModelJsonMapper<Good> {
    public GoodsJsonMapper() {
        super(Good.class, GoodsCriteriaAggregate.class);
    }

    public OffsetGoodQuantity fromOffsetQuantityJson(String json) throws IOException {
        return updateReader.readValue(json, OffsetGoodQuantity.class);
    }

    public String toOffsetQuantityJson(OffsetGoodQuantity quantity) throws JsonProcessingException {
        return updateWriter.writeValueAsString(quantity);
    }
}
