package com.nova.cls.data;

public enum Command {
    PRODUCT_GET_QUANTITY,
    PRODUCT_ADD_UNITS,
    PRODUCT_SUBTRACT_UNITS,
    GROUP_ADD,
    PRODUCT_ADD_TO_GROUP,
    PRODUCT_SET_PRICE;

    public static Command get(int i) {
        return values()[i];
    }
}
