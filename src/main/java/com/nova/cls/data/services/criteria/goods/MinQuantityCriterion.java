package com.nova.cls.data.services.criteria.goods;

import com.nova.cls.data.models.Good;
import com.nova.cls.data.services.criteria.Criterion;

public class MinQuantityCriterion extends Criterion<Good> {
    private static final String SQL = "quantity >= ?";

    public MinQuantityCriterion(long quantity) {
        super(SQL, quantity);
    }
}
