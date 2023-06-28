package com.nova.cls.data.services.criteria.goods;

import com.nova.cls.data.models.Good;
import com.nova.cls.data.services.criteria.Criterion;

public class ManufacturerPrefixCriterion extends Criterion<Good, String> {
    public static final String SQL = "manufacturer LIKE ? || '%'";
    public static final String QUERY_PARAM_NAME = "manufacturerPrefix";

    public ManufacturerPrefixCriterion(String manufacturerPrefix) {
        super(manufacturerPrefix, SQL, QUERY_PARAM_NAME);
    }
}
