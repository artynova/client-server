package com.nova.cls.data.criteria.goods;

import com.nova.cls.data.criteria.Criterion;
import com.nova.cls.data.models.Good;

public class NamePrefixCriterion extends Criterion<Good, String> {
    public static final String SQL = "goodName LIKE ? || '%'";
    public static final String QUERY_PARAM_NAME = "namePrefix";

    public NamePrefixCriterion(String prefix) {
        super(prefix, SQL, QUERY_PARAM_NAME);
    }
}
