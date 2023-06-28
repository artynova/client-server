package com.nova.cls.data.services.criteria.goods;

import com.nova.cls.data.models.Good;
import com.nova.cls.data.services.criteria.Criterion;

public class NamePrefixCriterion extends Criterion<Good, String> {
    public static final String SQL = "goodName LIKE ? || '%'";
    public static final String QUERY_PARAM_NAME = "namePrefix";

    public NamePrefixCriterion(String prefix) {
        super(prefix, SQL, QUERY_PARAM_NAME);
    }
}
