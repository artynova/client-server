package com.nova.cls.data.models;

import java.util.Objects;

public class Good {
    private int goodId;
    private String goodName;
    private String description;
    private String manufacturer;
    private int quantity;
    private int price; // in kopiykas
    private int groupId;

    public Good() {
    }

    public Good(int goodId, String goodName, String description, String manufacturer, int quantity, int price, int groupId) {
        this.goodId = goodId;
        this.goodName = goodName;
        this.description = description;
        this.manufacturer = manufacturer;
        this.quantity = quantity;
        this.price = price;
        this.groupId = groupId;
    }

    public int getGoodId() {
        return goodId;
    }

    public void setGoodId(int goodId) {
        this.goodId = goodId;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Good good = (Good) o;
        return getQuantity() == good.getQuantity() && getPrice() == good.getPrice() && Objects.equals(getGoodName(), good.getGoodName()) && Objects.equals(getDescription(), good.getDescription()) && Objects.equals(getManufacturer(), good.getManufacturer()) && Objects.equals(getGoodId(), good.getGroupId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGoodName(), getDescription(), getManufacturer(), getQuantity(), getPrice(), getGoodId());
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Good {");
        sb.append("goodId=").append(getGoodId());
        sb.append(", goodName='").append(getGoodName()).append('\'');
        sb.append(", description='").append(getDescription()).append('\'');
        sb.append(", manufacturer='").append(getManufacturer()).append('\'');
        sb.append(", quantity=").append(getQuantity());
        sb.append(", price=").append(getPrice());
        sb.append(", groupId='").append(getGroupId()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
