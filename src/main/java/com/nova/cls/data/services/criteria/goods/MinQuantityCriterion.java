package com.nova.cls.data.services.criteria.goods;

import com.nova.cls.data.models.Good;
import com.nova.cls.data.services.criteria.Criterion;

public class MinQuantityCriterion extends Criterion<Good, Long> {
    public static final String SQL = "quantity >= ?";
    public static final String QUERY_PARAM_NAME = "minQuantity";

    public MinQuantityCriterion(long quantity) {
        super(quantity, SQL, QUERY_PARAM_NAME);
    }
}
