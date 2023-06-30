package com.nova.cls.data.criteria;

/**
 * Base criterion that holds text of a parametrized filter to be used in a WHERE clause,
 * and an array of values to be substituted in it.
 *
 * @param <Model> Model class, to avoid mixing up criteria.
 */
public abstract class Criterion<Model, Type> {
    private final String sql;
    private final String queryParamName;
    private Type value;

    public Criterion(Type value, String sql, String queryParamName) {
        this.value = value;
        this.sql = sql;
        this.queryParamName = queryParamName;
    }

    public String getSql() {
        return sql;
    }

    public String getQueryParamName() {
        return queryParamName;
    }

    public Type getValue() {
        return value;
    }

    public void setValue(Type value) {
        this.value = value;
    }
}
