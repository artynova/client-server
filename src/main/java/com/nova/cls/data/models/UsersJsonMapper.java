package com.nova.cls.data.models;

import com.nova.cls.data.services.criteria.users.UsersCriteriaAggregate;

public class UsersJsonMapper extends ModelJsonMapper<User> {
    public UsersJsonMapper() {
        super(User.class, UsersCriteriaAggregate.class);
    }
}
