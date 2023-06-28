package com.nova.cls.data.services.criteria.goods;

import com.nova.cls.data.models.Good;
import com.nova.cls.data.services.criteria.Criterion;

public class MaxQuantityCriterion extends Criterion<Good, Long> {
    public static final String SQL = "quantity <= ?";
    public static final String QUERY_PARAM_NAME = "maxQuantity";

    public MaxQuantityCriterion(Long quantity) {
        super(quantity, SQL, QUERY_PARAM_NAME);
    }
}
