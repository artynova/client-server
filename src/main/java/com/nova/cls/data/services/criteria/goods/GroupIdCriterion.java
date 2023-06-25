package com.nova.cls.data.services.criteria.goods;

public class GroupIdCriterion extends GoodsCriterion {
    private static final String SQL = "groupId = ?";

    public GroupIdCriterion(long groupId) {
        super(SQL, groupId);
    }
}
