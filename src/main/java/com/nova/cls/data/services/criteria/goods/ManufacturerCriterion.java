package com.nova.cls.data.services.criteria.goods;

public class ManufacturerCriterion extends GoodsCriterion {
    private static final String SQL = "manufacturer = ?";

    public ManufacturerCriterion(String manufacturer) {
        super(SQL, manufacturer);
    }
}
