package com.neusoft.yyzx.entity;

import java.math.BigDecimal;

/** 对应数据表：food */
public class Food {

    private Integer id;
    private String foodName;
    private String foodType;
    private BigDecimal price;
    private String foodImg;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public String getFoodType() { return foodType; }
    public void setFoodType(String foodType) { this.foodType = foodType; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getFoodImg() { return foodImg; }
    public void setFoodImg(String foodImg) { this.foodImg = foodImg; }

    @Override
    public String toString() {
        return "Food{" + "id=" + id + ", " + "foodName=" + foodName + ", " + "foodType=" + foodType + ", " + "price=" + price + ", " + "foodImg=" + foodImg + "}";
    }
}
