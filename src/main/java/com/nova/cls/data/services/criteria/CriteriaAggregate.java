package com.nova.cls.data.services.criteria;

import java.util.List;

public interface CriteriaAggregate<Model> {
    List<Criterion<Model>> criteria();
}
