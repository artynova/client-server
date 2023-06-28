package com.nova.cls.data.services.criteria.goods;

import com.nova.cls.data.models.Good;
import com.nova.cls.data.services.criteria.Criterion;

public class GroupIdCriterion extends Criterion<Good, Long> {
    public static final String SQL = "groupId = ?";
    public static final String QUERY_PARAM_NAME = "groupId";

    public GroupIdCriterion(Long groupId) {
        super(groupId, SQL, QUERY_PARAM_NAME);
    }
}
