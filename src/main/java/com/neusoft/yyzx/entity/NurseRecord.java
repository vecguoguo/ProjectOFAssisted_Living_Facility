package com.neusoft.yyzx.entity;

import java.time.LocalDateTime;

/** 对应数据表：nurserecord */
public class NurseRecord {

    private Integer id;
    private Integer customerId;
    private Integer itemId;
    private Integer userId;
    private LocalDateTime nursingTime;
    private String nursingContent;
    private Integer nursingCount;
    private Integer isDeleted;
    /** 联表 nursecontent 展示用（非 nurserecord 列） */
    private String itemName;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public Integer getItemId() { return itemId; }
    public void setItemId(Integer itemId) { this.itemId = itemId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public LocalDateTime getNursingTime() { return nursingTime; }
    public void setNursingTime(LocalDateTime nursingTime) { this.nursingTime = nursingTime; }

    public String getNursingContent() { return nursingContent; }
    public void setNursingContent(String nursingContent) { this.nursingContent = nursingContent; }

    public Integer getNursingCount() { return nursingCount; }
    public void setNursingCount(Integer nursingCount) { this.nursingCount = nursingCount; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    @Override
    public String toString() {
        return "NurseRecord{" + "id=" + id + ", " + "customerId=" + customerId + ", " + "itemId=" + itemId + ", " + "userId=" + userId + ", " + "nursingTime=" + nursingTime + ", " + "nursingContent=" + nursingContent + ", " + "nursingCount=" + nursingCount + ", " + "isDeleted=" + isDeleted + "}";
    }
}
