package com.nova.cls.data.services.criteria.goods;

public class MinPriceCriterion extends GoodsCriterion {
    private static final String SQL = "price >= ?";

    public MinPriceCriterion(int price) {
        super(SQL, price);
    }
}
