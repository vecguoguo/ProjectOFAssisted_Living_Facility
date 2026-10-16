package com.neusoft.yyzx.entity;

/** 对应数据表：nursecontent */
public class NurseContent {

    private Integer id;
    private String serialNumber;
    private String nursingName;
    private String servicePrice;
    private String message;
    private Integer status;
    private String executionCycle;
    private String executionTimes;
    private Integer isDeleted;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getNursingName() { return nursingName; }
    public void setNursingName(String nursingName) { this.nursingName = nursingName; }

    public String getServicePrice() { return servicePrice; }
    public void setServicePrice(String servicePrice) { this.servicePrice = servicePrice; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getExecutionCycle() { return executionCycle; }
    public void setExecutionCycle(String executionCycle) { this.executionCycle = executionCycle; }

    public String getExecutionTimes() { return executionTimes; }
    public void setExecutionTimes(String executionTimes) { this.executionTimes = executionTimes; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }

    @Override
    public String toString() {
        return "NurseContent{" + "id=" + id + ", " + "serialNumber=" + serialNumber + ", " + "nursingName=" + nursingName + ", " + "servicePrice=" + servicePrice + ", " + "message=" + message + ", " + "status=" + status + ", " + "executionCycle=" + executionCycle + ", " + "executionTimes=" + executionTimes + ", " + "isDeleted=" + isDeleted + "}";
    }
}
