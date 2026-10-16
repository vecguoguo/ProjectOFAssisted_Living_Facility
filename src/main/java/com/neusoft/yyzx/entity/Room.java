package com.neusoft.yyzx.entity;

/** 对应数据表：room */
public class Room {

    private Integer id;
    private String roomFloor;
    private Integer roomNo;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getRoomFloor() { return roomFloor; }
    public void setRoomFloor(String roomFloor) { this.roomFloor = roomFloor; }

    public Integer getRoomNo() { return roomNo; }
    public void setRoomNo(Integer roomNo) { this.roomNo = roomNo; }

    @Override
    public String toString() {
        return "Room{" + "id=" + id + ", " + "roomFloor=" + roomFloor + ", " + "roomNo=" + roomNo + "}";
    }
}
