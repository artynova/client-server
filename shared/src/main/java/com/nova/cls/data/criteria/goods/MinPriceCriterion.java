package com.nova.cls.data.criteria.goods;

import com.nova.cls.data.criteria.Criterion;
import com.nova.cls.data.models.Good;

public class MinPriceCriterion extends Criterion<Good, Long> {
    public static final String SQL = "price >= ?";
    public static final String QUERY_PARAM_NAME = "minPrice";

    public MinPriceCriterion(long price) {
        super(price, SQL, QUERY_PARAM_NAME);
    }
}
