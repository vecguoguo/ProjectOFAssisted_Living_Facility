package com.neusoft.yyzx.entity;

/** 对应数据表：bed */
public class Bed {

    private Integer id;
    private Integer roomNo;
    private String bedNo;
    private Integer bedStatus;
    private String remarks;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getRoomNo() { return roomNo; }
    public void setRoomNo(Integer roomNo) { this.roomNo = roomNo; }

    public String getBedNo() { return bedNo; }
    public void setBedNo(String bedNo) { this.bedNo = bedNo; }

    public Integer getBedStatus() { return bedStatus; }
    public void setBedStatus(Integer bedStatus) { this.bedStatus = bedStatus; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    @Override
    public String toString() {
        return "Bed{" + "id=" + id + ", " + "roomNo=" + roomNo + ", " + "bedNo=" + bedNo + ", " + "bedStatus=" + bedStatus + ", " + "remarks=" + remarks + "}";
    }
}
