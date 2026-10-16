package com.neusoft.yyzx.entity;

/** 对应数据表：meal */
public class Meal {

    private Integer id;
    private String weekDay;
    private Integer foodId;
    private Integer mealType;
    private String taste;
    private Integer isDeleted;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getWeekDay() { return weekDay; }
    public void setWeekDay(String weekDay) { this.weekDay = weekDay; }

    public Integer getFoodId() { return foodId; }
    public void setFoodId(Integer foodId) { this.foodId = foodId; }

    public Integer getMealType() { return mealType; }
    public void setMealType(Integer mealType) { this.mealType = mealType; }

    public String getTaste() { return taste; }
    public void setTaste(String taste) { this.taste = taste; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }

    @Override
    public String toString() {
        return "Meal{" + "id=" + id + ", " + "weekDay=" + weekDay + ", " + "foodId=" + foodId + ", " + "mealType=" + mealType + ", " + "taste=" + taste + ", " + "isDeleted=" + isDeleted + "}";
    }
}
