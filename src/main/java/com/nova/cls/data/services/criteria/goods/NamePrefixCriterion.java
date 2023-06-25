package com.nova.cls.data.services.criteria.goods;

import com.nova.cls.data.models.Good;
import com.nova.cls.data.services.criteria.Criterion;

public class NamePrefixCriterion extends Criterion<Good> {
    public static final String SQL = "goodName LIKE ? || '%'";

    public NamePrefixCriterion(String prefix) {
        super(SQL, prefix);
    }
}
