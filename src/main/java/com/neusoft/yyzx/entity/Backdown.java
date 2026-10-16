package com.neusoft.yyzx.entity;

import java.time.LocalDate;

/** 对应数据表：backdown */
public class Backdown {

    private Integer id;
    private Integer customerId;
    /** 客户姓名（列表展示用，关联查询得出，非本表字段） */
    private String customerName;
    private LocalDate retreattime;
    private Integer retreattype;
    private String retreatreason;
    private Integer auditstatus;
    private String auditperson;
    private LocalDate audittime;
    private String remarks;
    private Integer isDeleted;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public LocalDate getRetreattime() { return retreattime; }
    public void setRetreattime(LocalDate retreattime) { this.retreattime = retreattime; }

    public Integer getRetreattype() { return retreattype; }
    public void setRetreattype(Integer retreattype) { this.retreattype = retreattype; }

    public String getRetreatreason() { return retreatreason; }
    public void setRetreatreason(String retreatreason) { this.retreatreason = retreatreason; }

    public Integer getAuditstatus() { return auditstatus; }
    public void setAuditstatus(Integer auditstatus) { this.auditstatus = auditstatus; }

    public String getAuditperson() { return auditperson; }
    public void setAuditperson(String auditperson) { this.auditperson = auditperson; }

    public LocalDate getAudittime() { return audittime; }
    public void setAudittime(LocalDate audittime) { this.audittime = audittime; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }

    @Override
    public String toString() {
        return "Backdown{" + "id=" + id + ", " + "customerId=" + customerId + ", " + "customerName=" + customerName + ", " + "retreattime=" + retreattime + ", " + "retreattype=" + retreattype + ", " + "retreatreason=" + retreatreason + ", " + "auditstatus=" + auditstatus + ", " + "auditperson=" + auditperson + ", " + "audittime=" + audittime + ", " + "remarks=" + remarks + ", " + "isDeleted=" + isDeleted + "}";
    }
}
