package com.nova.cls.data.models;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class GoodsJsonMapper {
    private final JsonMapper mapper;

    //    private final ObjectReader o;
    public GoodsJsonMapper() {
        mapper = JsonMapper.builder().disable(MapperFeature.DEFAULT_VIEW_INCLUSION).build();
    }

}
