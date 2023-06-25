package com.nova.cls.data.services.criteria.groups;

import com.nova.cls.data.models.Group;
import com.nova.cls.data.services.criteria.Criterion;

public class HasGoodsCriterion extends Criterion<Group> {
    private static final String SQL = """
        ? = (EXISTS (
            SELECT *
            FROM Goods
            WHERE Goods.groupId = Groups.groupId
        ))""";

    public HasGoodsCriterion(boolean hasGoods) {
        super(SQL, hasGoods);
    }
}
