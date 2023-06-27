package com.nova.cls.data.services.criteria.users;

import com.nova.cls.data.models.User;
import com.nova.cls.data.services.criteria.CriteriaAggregate;
import com.nova.cls.data.services.criteria.Criterion;

import java.util.ArrayList;
import java.util.List;

public final class UsersCriteriaAggregate implements CriteriaAggregate<User> {
    public UsersCriteriaAggregate() {
    }

    @Override
    public List<Criterion<User>> criteria() {
        return new ArrayList<>();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj != null && obj.getClass() == this.getClass();
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public String toString() {
        return "UsersCriteriaAggregate {}";
    }

}
