package com.nova.cls.data.services.criteria.goods;

import com.nova.cls.data.models.Good;
import com.nova.cls.data.services.criteria.Criterion;

public class GroupIdCriterion extends Criterion<Good> {
    private static final String SQL = "groupId = ?";

    public GroupIdCriterion(long groupId) {
        super(SQL, groupId);
    }
}
