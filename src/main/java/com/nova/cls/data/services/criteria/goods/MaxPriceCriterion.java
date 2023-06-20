package com.nova.cls.data.services.criteria.goods;

public class MaxPriceCriterion extends GoodsCriterion {
    private static final String SQL = "price <= ?";

    public MaxPriceCriterion(int price) {
        super(SQL, price);
    }
}
