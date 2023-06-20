package com.nova.cls.data.services.criteria.groups;

public class HasDescriptionCriterion extends GroupsCriterion {
    private static final String SQL = "(length(description) > 0) = ?";

    public HasDescriptionCriterion(boolean hasDescription) {
        super(SQL, hasDescription);
    }
}
