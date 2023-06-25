package com.nova.cls.data.services.criteria.groups;

public class HasGoodsCriterion extends GroupsCriterion {
    private static final String SQL = """
        ? = (EXISTS (
            SELECT *
            FROM Goods
            WHERE Goods.groupId = groupId
        ))""";

    public HasGoodsCriterion(boolean hasGoods) {
        super(SQL, hasGoods);
    }
}
