package com.neusoft.yyzx.entity;

import java.time.LocalDate;

/** 对应数据表：outward */
public class Outward {

    private Integer id;
    private Integer customerId;
    /** 客户姓名（列表展示用，关联查询得出，非本表字段） */
    private String customerName;
    private String outgoingreason;
    private LocalDate outgoingtime;
    private LocalDate expectedreturntime;
    private LocalDate actualreturntime;
    private String escorted;
    private String relation;
    private String escortedtel;
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

    public String getOutgoingreason() { return outgoingreason; }
    public void setOutgoingreason(String outgoingreason) { this.outgoingreason = outgoingreason; }

    public LocalDate getOutgoingtime() { return outgoingtime; }
    public void setOutgoingtime(LocalDate outgoingtime) { this.outgoingtime = outgoingtime; }

    public LocalDate getExpectedreturntime() { return expectedreturntime; }
    public void setExpectedreturntime(LocalDate expectedreturntime) { this.expectedreturntime = expectedreturntime; }

    public LocalDate getActualreturntime() { return actualreturntime; }
    public void setActualreturntime(LocalDate actualreturntime) { this.actualreturntime = actualreturntime; }

    public String getEscorted() { return escorted; }
    public void setEscorted(String escorted) { this.escorted = escorted; }

    public String getRelation() { return relation; }
    public void setRelation(String relation) { this.relation = relation; }

    public String getEscortedtel() { return escortedtel; }
    public void setEscortedtel(String escortedtel) { this.escortedtel = escortedtel; }

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
        return "Outward{" + "id=" + id + ", " + "customerId=" + customerId + ", " + "customerName=" + customerName + ", " + "outgoingreason=" + outgoingreason + ", " + "outgoingtime=" + outgoingtime + ", " + "expectedreturntime=" + expectedreturntime + ", " + "actualreturntime=" + actualreturntime + ", " + "escorted=" + escorted + ", " + "relation=" + relation + ", " + "escortedtel=" + escortedtel + ", " + "auditstatus=" + auditstatus + ", " + "auditperson=" + auditperson + ", " + "audittime=" + audittime + ", " + "remarks=" + remarks + ", " + "isDeleted=" + isDeleted + "}";
    }
}
