package com.nova.cls.data.services.criteria;

/**
 * Base criterion that holds text of a parametrized filter to be used in a WHERE clause,
 * and an array of values to be substituted in it.
 */
public class Criterion {
    private final String sql;
    private final Object[] values;

    public Criterion(String sql, Object... values) {
        this.sql = sql;
        this.values = values;
    }

    public String getSql() {
        return sql;
    }

    public Object[] getValues() {
        return values;
    }
}
