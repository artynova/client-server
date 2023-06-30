package com.nova.cls.data.criteria.goods;

import com.nova.cls.data.criteria.Criterion;
import com.nova.cls.data.models.Good;

public class MaxQuantityCriterion extends Criterion<Good, Long> {
    public static final String SQL = "quantity <= ?";
    public static final String QUERY_PARAM_NAME = "maxQuantity";

    public MaxQuantityCriterion(Long quantity) {
        super(quantity, SQL, QUERY_PARAM_NAME);
    }
}
