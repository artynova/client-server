package com.nova.cls.data.services.criteria.goods;

import com.nova.cls.data.models.Good;
import com.nova.cls.data.services.criteria.Criterion;

public class MaxPriceCriterion extends Criterion<Good> {
    private static final String SQL = "price <= ?";

    public MaxPriceCriterion(long price) {
        super(SQL, price);
    }
}
