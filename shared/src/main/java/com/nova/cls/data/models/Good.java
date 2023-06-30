package com.nova.cls.data.models;

import com.fasterxml.jackson.annotation.JsonView;
import com.nova.cls.data.View;
import com.nova.cls.util.StringUtils;

import java.util.Objects;

public class Good {
    @JsonView(View.Read.class)
    private Long goodId;
    @JsonView({View.Read.class, View.Create.class, View.Update.class})
    private String goodName;
    @JsonView({View.Read.class, View.Create.class, View.Update.class})
    private String description;
    @JsonView({View.Read.class, View.Create.class, View.Update.class})
    private String manufacturer;
    @JsonView({View.Read.class, View.Create.class, View.Update.class})
    private Long quantity;
    @JsonView({View.Read.class, View.Create.class, View.Update.class})
    private Long price; // in kopiykas
    @JsonView({View.Read.class, View.Create.class})
    private Long groupId;

    public Good() {
    }

    public Good(String goodName, String description, String manufacturer, Long quantity, Long price, Long groupId) {
        this.goodName = goodName;
        this.description = description;
        this.manufacturer = manufacturer;
        this.quantity = quantity;
        this.price = price;
        this.groupId = groupId;
    }

    public Good(String goodName, String description, String manufacturer, Long price, Long groupId) {
        this.goodName = goodName;
        this.description = description;
        this.manufacturer = manufacturer;
        this.price = price;
        this.groupId = groupId;
    }

    public Good(Long goodId, String goodName, String description, String manufacturer, Long price) {
        this.goodId = goodId;
        this.goodName = goodName;
        this.description = description;
        this.manufacturer = manufacturer;
        this.price = price;
        this.groupId = groupId;
    }

    public Good(Long goodId, String goodName, String description, String manufacturer, Long quantity, Long price,
        Long groupId) {
        this.goodId = goodId;
        this.goodName = goodName;
        this.description = description;
        this.manufacturer = manufacturer;
        this.quantity = quantity;
        this.price = price;
        this.groupId = groupId;
    }

    public Long getGoodId() {
        return goodId;
    }

    public void setGoodId(Long goodId) {
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

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
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
        return Objects.hash(getGoodId(),
            getGoodName(),
            getDescription(),
            getManufacturer(),
            getQuantity(),
            getPrice(),
            getGroupId());
    }

    @Override
    public String toString() {
        return "Good { goodId = " + getGoodId() + ", goodName = " + StringUtils.wrap(getGoodName()) + ", description = "
            + StringUtils.wrap(getDescription()) + ", manufacturer = " + StringUtils.wrap(getManufacturer())
            + ", quantity = " + getQuantity() + ", price = " + getPrice() + ", groupId = " + getGroupId() + " }";
    }
}
