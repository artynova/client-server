package com.nova.cls.data.services.criteria.goods;

import java.util.ArrayList;
import java.util.List;

/**
 * Intermediate class between a JSON message body and a set of Criteria that the message specifies for a list of goods.
 * A property that is equal to "null" means that a criterion is not used.
 */
public record GoodsCriteriaAggregate(Long groupId, String manufacturer, Long minPrice, Long maxPrice, Long minQuantity,
                                     Long maxQuantity) {
    public GoodsCriterion[] criteria() {
        List<GoodsCriterion> list = new ArrayList<>(6);
        if (groupId() != null) {
            list.add(new GroupIdCriterion(groupId()));
        }
        if (manufacturer() != null) {
            list.add(new ManufacturerCriterion(manufacturer()));
        }
        if (minPrice() != null) {
            list.add(new MinPriceCriterion(minPrice()));
        }
        if (maxPrice() != null) {
            list.add(new MaxPriceCriterion(maxPrice()));
        }
        if (minQuantity() != null) {
            list.add(new MinQuantityCriterion(minQuantity()));
        }
        if (maxQuantity() != null) {
            list.add(new MaxQuantityCriterion(maxQuantity()));
        }
        return list.toArray(new GoodsCriterion[0]);
    }
}
