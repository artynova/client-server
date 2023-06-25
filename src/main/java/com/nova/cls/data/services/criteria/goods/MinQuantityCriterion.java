package com.nova.cls.data.services.criteria.goods;

public class MinQuantityCriterion extends GoodsCriterion {
    private static final String SQL = "quantity >= ?";

    public MinQuantityCriterion(long quantity) {
        super(SQL, quantity);
    }
}
