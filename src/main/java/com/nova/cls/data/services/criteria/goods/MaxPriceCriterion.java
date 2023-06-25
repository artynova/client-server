package com.nova.cls.data.services.criteria.goods;

public class MaxPriceCriterion extends GoodsCriterion {
    private static final String SQL = "price <= ?";

    public MaxPriceCriterion(long price) {
        super(SQL, price);
    }
}
