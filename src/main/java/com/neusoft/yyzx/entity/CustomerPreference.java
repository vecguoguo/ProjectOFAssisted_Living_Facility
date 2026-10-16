package com.neusoft.yyzx.entity;

/** 对应数据表：customerpreference */
public class CustomerPreference {

    private Integer id;
    private Integer customerId;
    private String preferences;
    private String attention;
    private String remark;
    private Integer isDeleted;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public String getPreferences() { return preferences; }
    public void setPreferences(String preferences) { this.preferences = preferences; }

    public String getAttention() { return attention; }
    public void setAttention(String attention) { this.attention = attention; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }

    @Override
    public String toString() {
        return "CustomerPreference{" + "id=" + id + ", " + "customerId=" + customerId + ", " + "preferences=" + preferences + ", " + "attention=" + attention + ", " + "remark=" + remark + ", " + "isDeleted=" + isDeleted + "}";
    }
}
