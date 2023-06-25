package com.nova.cls.data.services.criteria.groups;

import com.nova.cls.data.services.criteria.Criterion;

/**
 * Intermediate marker class.
 */
public abstract class GroupsCriterion extends Criterion {
    protected GroupsCriterion(String sql, Object... values) {
        super(sql, values);
    }
}
