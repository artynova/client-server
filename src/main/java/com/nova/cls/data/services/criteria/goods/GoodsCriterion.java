package com.nova.cls.data.services.criteria.goods;

import com.nova.cls.data.services.criteria.Criterion;

/**
 * Intermediate marker class.
 */
public abstract class GoodsCriterion extends Criterion {
    public GoodsCriterion(String sql, Object... values) {
        super(sql, values);
    }
}
