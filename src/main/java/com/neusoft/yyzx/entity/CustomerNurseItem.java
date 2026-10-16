package com.neusoft.yyzx.entity;

import java.time.LocalDate;

/** 对应数据表：customernurseitem */
public class CustomerNurseItem {

    private Integer id;
    private Integer itemId;
    private Integer custormerId;
    private Integer levelId;
    private Integer nurseNumber;
    private LocalDate buyTime;
    private LocalDate maturityTime;
    private Integer isDeleted;
    /** 联表 nursecontent 展示用（非 customernurseitem 列） */
    private String itemName;
    /** 联表 nursecontent 展示用（非 customernurseitem 列） */
    private String servicePrice;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getItemId() { return itemId; }
    public void setItemId(Integer itemId) { this.itemId = itemId; }

    public Integer getCustormerId() { return custormerId; }
    public void setCustormerId(Integer custormerId) { this.custormerId = custormerId; }

    public Integer getLevelId() { return levelId; }
    public void setLevelId(Integer levelId) { this.levelId = levelId; }

    public Integer getNurseNumber() { return nurseNumber; }
    public void setNurseNumber(Integer nurseNumber) { this.nurseNumber = nurseNumber; }

    public LocalDate getBuyTime() { return buyTime; }
    public void setBuyTime(LocalDate buyTime) { this.buyTime = buyTime; }

    public LocalDate getMaturityTime() { return maturityTime; }
    public void setMaturityTime(LocalDate maturityTime) { this.maturityTime = maturityTime; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getServicePrice() { return servicePrice; }
    public void setServicePrice(String servicePrice) { this.servicePrice = servicePrice; }

    @Override
    public String toString() {
        return "CustomerNurseItem{" + "id=" + id + ", " + "itemId=" + itemId + ", " + "custormerId=" + custormerId + ", " + "levelId=" + levelId + ", " + "nurseNumber=" + nurseNumber + ", " + "buyTime=" + buyTime + ", " + "maturityTime=" + maturityTime + ", " + "isDeleted=" + isDeleted + "}";
    }
}
