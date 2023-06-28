package com.nova.cls.data.services.criteria.goods;

import com.nova.cls.data.models.Good;
import com.nova.cls.data.services.criteria.Criterion;

public class MaxPriceCriterion extends Criterion<Good, Long> {
    public static final String SQL = "price <= ?";
    public static final String QUERY_PARAM_NAME = "maxPrice";

    public MaxPriceCriterion(Long price) {
        super(price, SQL, QUERY_PARAM_NAME);
    }
}
