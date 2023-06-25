package com.nova.cls.data.services.criteria.groups;

import com.fasterxml.jackson.annotation.JsonView;
import com.nova.cls.data.models.Group;
import com.nova.cls.data.models.Views;
import com.nova.cls.data.services.criteria.CriteriaAggregate;
import com.nova.cls.data.services.criteria.Criterion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonView(Views.ReadView.class)
public final class GroupsCriteriaAggregate implements CriteriaAggregate<Group> {
    private Boolean hasGoods;

    public GroupsCriteriaAggregate(Boolean hasGoods) {
        this.hasGoods = hasGoods;
    }

    public GroupsCriteriaAggregate() {
    }

    public List<Criterion<Group>> criteria() {
        List<Criterion<Group>> list = new ArrayList<>();
        if (getHasGoods() != null) {
            list.add(new HasGoodsCriterion(getHasGoods()));
        }
        return list;
    }

    public Boolean getHasGoods() {
        return hasGoods;
    }

    public void setHasGoods(Boolean hasGoods) {
        this.hasGoods = hasGoods;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        GroupsCriteriaAggregate aggregate = (GroupsCriteriaAggregate) o;
        return Objects.equals(getHasGoods(), aggregate.getHasGoods());
    }

    @Override
    public String toString() {
        return "GroupsCriteriaAggregate[" + "hasGoods=" + hasGoods + ']';
    }

}
