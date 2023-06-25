package com.nova.cls.data.services.criteria.goods;

import com.nova.cls.data.models.Good;
import com.nova.cls.data.services.criteria.Criterion;

public class ManufacturerCriterion extends Criterion<Good> {
    private static final String SQL = "manufacturer = ?";

    public ManufacturerCriterion(String manufacturer) {
        super(SQL, manufacturer);
    }
}
