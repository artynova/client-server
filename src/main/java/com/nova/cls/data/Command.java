package com.nova.cls.data;

public enum Command {
    GROUPS_CREATE,
    GROUPS_READ,
    GROUPS_LIST, // read many
    GROUPS_UPDATE,
    GROUPS_DELETE,

    GOODS_CREATE, // with full information
    GOODS_READ, // with full information
    GOODS_LIST, // read many with criteria
    GOODS_UPDATE, // does not update Group (according to requirements, group is non-changeable) and units (tracking is done via separate commands)
    GOODS_ADD_QUANTITY,
    GOODS_SUBTRACT_QUANTITY,
    GOODS_DELETE;

    public static Command get(int i) {
        return values()[i];
    }
}
