package com.nova.cls.data.services.criteria.goods;

public class MaxQuantityCriterion extends GoodsCriterion {
    private static final String SQL = "quantity <= ?";

    public MaxQuantityCriterion(long quantity) {
        super(SQL, quantity);
    }
}
