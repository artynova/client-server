package com.nova.cls.data.models;

import com.fasterxml.jackson.annotation.JsonView;
import com.nova.cls.data.Views;

@JsonView(Views.UpdateView.class)
public class OffsetGoodQuantity {
    private long goodId;
    private long offset;

    public OffsetGoodQuantity() {
    }

    public OffsetGoodQuantity(long goodId, long offset) {
        this.goodId = goodId;
        this.offset = offset;
    }

    public long getGoodId() {
        return goodId;
    }

    public void setGoodId(long goodId) {
        this.goodId = goodId;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }
}
