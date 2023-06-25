package com.nova.cls.data.services.criteria.groups;

public record GroupsCriteriaAggregate(Boolean hasGoods) {
    public GroupsCriterion[] criteria() {
        if (hasGoods() != null) {
            return new GroupsCriterion[] {new HasGoodsCriterion(hasGoods())};
        }
        return new GroupsCriterion[0];
    }
}
