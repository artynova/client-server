package com.nova.cls.network;

import com.nova.cls.data.mappers.GoodsMapper;
import com.nova.cls.data.models.Good;
import com.nova.cls.exceptions.RequestFailureException;

public class GoodsClient extends CollectionClient<Good> {
    public GoodsClient(LoginClient loginClient, Session session) {
        super(loginClient, session, new GoodsMapper(), "goods");
    }

    public void offsetQuantity(long goodId, long offset) throws RequestFailureException {
        request(HttpMethod.PATCH, "/api/goods/" + goodId + "/quantity", String.valueOf(offset));
    }

    @Override
    protected long getId(Good good) {
        return good.getGoodId();
    }
}
