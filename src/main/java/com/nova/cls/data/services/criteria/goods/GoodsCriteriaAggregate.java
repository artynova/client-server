package com.nova.cls.data.services.criteria.goods;

import com.fasterxml.jackson.annotation.JsonView;
import com.nova.cls.data.models.Good;
import com.nova.cls.data.models.Views;
import com.nova.cls.data.services.criteria.CriteriaAggregate;
import com.nova.cls.data.services.criteria.Criterion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Intermediate class between a JSON message body and a set of Criteria that the message specifies for a list of goods.
 * A property that is equal to "null" means that a criterion is not used.
 */
@JsonView(Views.ReadView.class)
public final class GoodsCriteriaAggregate implements CriteriaAggregate<Good> {
    private Long groupId;
    private String manufacturer;
    private Long minPrice;
    private Long maxPrice;
    private Long minQuantity;
    private Long maxQuantity;

    public GoodsCriteriaAggregate() {
    }

    public GoodsCriteriaAggregate(Long groupId, String manufacturer, Long minPrice, Long maxPrice, Long minQuantity,
        Long maxQuantity) {
        this.groupId = groupId;
        this.manufacturer = manufacturer;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
    }

    public List<Criterion<Good>> criteria() {
        List<Criterion<Good>> list = new ArrayList<>(6);
        if (getGroupId() != null) {
            list.add(new GroupIdCriterion(getGroupId()));
        }
        if (getManufacturer() != null) {
            list.add(new ManufacturerCriterion(getManufacturer()));
        }
        if (getMinPrice() != null) {
            list.add(new MinPriceCriterion(getMinPrice()));
        }
        if (getMaxPrice() != null) {
            list.add(new MaxPriceCriterion(getMaxPrice()));
        }
        if (getMinQuantity() != null) {
            list.add(new MinQuantityCriterion(getMinQuantity()));
        }
        if (getMaxQuantity() != null) {
            list.add(new MaxQuantityCriterion(getMaxQuantity()));
        }
        return list;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Long getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Long minPrice) {
        this.minPrice = minPrice;
    }

    public Long getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Long maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Long getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(Long minQuantity) {
        this.minQuantity = minQuantity;
    }

    public Long getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(Long maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (GoodsCriteriaAggregate) obj;
        return Objects.equals(this.groupId, that.groupId) && Objects.equals(this.manufacturer, that.manufacturer)
            && Objects.equals(this.minPrice, that.minPrice) && Objects.equals(this.maxPrice, that.maxPrice)
            && Objects.equals(this.minQuantity, that.minQuantity) && Objects.equals(this.maxQuantity, that.maxQuantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, manufacturer, minPrice, maxPrice, minQuantity, maxQuantity);
    }

    @Override
    public String toString() {
        return "GoodsCriteriaAggregate[" + "groupId=" + groupId + ", " + "manufacturer=" + manufacturer + ", "
            + "minPrice=" + minPrice + ", " + "maxPrice=" + maxPrice + ", " + "minQuantity=" + minQuantity + ", "
            + "maxQuantity=" + maxQuantity + ']';
    }


}
