package com.nova.cls.data.services.criteria.groups;

public record GroupsCriteriaAggregate(Boolean hasDescription) {
    public GroupsCriterion[] criteria() {
        if (hasDescription() != null) return new GroupsCriterion[]{new HasDescriptionCriterion(hasDescription())};
        return new GroupsCriterion[0];
    }
}
