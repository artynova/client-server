package com.nova.cls.data.services.criteria.goods;

import com.nova.cls.data.models.Good;
import com.nova.cls.data.services.criteria.Criterion;

public class MinPriceCriterion extends Criterion<Good> {
    private static final String SQL = "price >= ?";

    public MinPriceCriterion(long price) {
        super(SQL, price);
    }
}
