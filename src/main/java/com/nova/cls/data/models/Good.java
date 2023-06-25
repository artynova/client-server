package com.nova.cls.data.models;

import com.fasterxml.jackson.annotation.JsonView;

import java.util.Objects;

public class Good {
    @JsonView({Views.ReadView.class, Views.UpdateView.class})
    private long goodId;
    @JsonView({Views.ReadView.class, Views.CreateView.class, Views.UpdateView.class})
    private String goodName;
    @JsonView({Views.ReadView.class, Views.CreateView.class, Views.UpdateView.class})
    private String description;
    @JsonView({Views.ReadView.class, Views.CreateView.class, Views.UpdateView.class})
    private String manufacturer;
    @JsonView({Views.ReadView.class, Views.CreateView.class, Views.UpdateView.class})
    private long quantity;
    @JsonView({Views.ReadView.class, Views.CreateView.class, Views.UpdateView.class})
    private long price; // in kopiykas
    @JsonView({Views.ReadView.class, Views.CreateView.class})
    private long groupId;

    public Good() {
    }

    public Good(long goodId, String goodName, String description, String manufacturer, long quantity, long price,
        long groupId) {
        this.goodId = goodId;
        this.goodName = goodName;
        this.description = description;
        this.manufacturer = manufacturer;
        this.quantity = quantity;
        this.price = price;
        this.groupId = groupId;
    }

    public long getGoodId() {
        return goodId;
    }

    public void setGoodId(long goodId) {
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

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Good good = (Good) o;
        return Objects.equals(getGoodId(), good.getGoodId()) && Objects.equals(getGoodName(), good.getGoodName())
            && Objects.equals(getDescription(), good.getDescription()) && Objects.equals(getManufacturer(),
            good.getManufacturer()) && Objects.equals(getQuantity(), good.getQuantity()) && Objects.equals(getPrice(),
            good.getPrice()) && Objects.equals(getGroupId(), good.getGroupId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGoodId(), getGoodName(), getDescription(), getManufacturer(), getQuantity(), getPrice(),
            getGroupId());
    }

    @Override
    public String toString() {
        return "Good {" + "goodId=" + getGoodId() + ", goodName='" + getGoodName() + '\'' + ", description='"
            + getDescription() + '\'' + ", manufacturer='" + getManufacturer() + '\'' + ", quantity=" + getQuantity()
            + ", price=" + getPrice() + ", groupId='" + getGroupId() + '\'' + '}';
    }
}
