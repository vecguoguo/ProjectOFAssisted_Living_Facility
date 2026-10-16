package com.neusoft.yyzx.entity;

import java.time.LocalDate;

/** 对应数据表：customer */
public class Customer {

    private Integer id;
    private String customerName;
    private Integer customerAge;
    private Integer customerSex;
    private String idcard;
    private String roomNo;
    private String buildingNo;
    private Integer bedId;
    private Integer levelId;
    private Integer userId;
    private LocalDate checkinDate;
    private LocalDate expirationDate;
    private LocalDate birthday;
    private String contactTel;
    private String familyMember;
    private String height;
    private String weight;
    private String bloodType;
    private String psychosomaticState;
    private String attention;
    private String filepath;
    private Integer isDeleted;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Integer getCustomerAge() { return customerAge; }
    public void setCustomerAge(Integer customerAge) { this.customerAge = customerAge; }

    public Integer getCustomerSex() { return customerSex; }
    public void setCustomerSex(Integer customerSex) { this.customerSex = customerSex; }

    public String getIdcard() { return idcard; }
    public void setIdcard(String idcard) { this.idcard = idcard; }

    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }

    public String getBuildingNo() { return buildingNo; }
    public void setBuildingNo(String buildingNo) { this.buildingNo = buildingNo; }

    public Integer getBedId() { return bedId; }
    public void setBedId(Integer bedId) { this.bedId = bedId; }

    public Integer getLevelId() { return levelId; }
    public void setLevelId(Integer levelId) { this.levelId = levelId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public LocalDate getCheckinDate() { return checkinDate; }
    public void setCheckinDate(LocalDate checkinDate) { this.checkinDate = checkinDate; }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }

    public String getContactTel() { return contactTel; }
    public void setContactTel(String contactTel) { this.contactTel = contactTel; }

    public String getFamilyMember() { return familyMember; }
    public void setFamilyMember(String familyMember) { this.familyMember = familyMember; }

    public String getHeight() { return height; }
    public void setHeight(String height) { this.height = height; }

    public String getWeight() { return weight; }
    public void setWeight(String weight) { this.weight = weight; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public String getPsychosomaticState() { return psychosomaticState; }
    public void setPsychosomaticState(String psychosomaticState) { this.psychosomaticState = psychosomaticState; }

    public String getAttention() { return attention; }
    public void setAttention(String attention) { this.attention = attention; }

    public String getFilepath() { return filepath; }
    public void setFilepath(String filepath) { this.filepath = filepath; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }

    @Override
    public String toString() {
        return "Customer{" + "id=" + id + ", " + "customerName=" + customerName + ", " + "customerAge=" + customerAge + ", " + "customerSex=" + customerSex + ", " + "idcard=" + idcard + ", " + "roomNo=" + roomNo + ", " + "buildingNo=" + buildingNo + ", " + "bedId=" + bedId + ", " + "levelId=" + levelId + ", " + "userId=" + userId + ", " + "checkinDate=" + checkinDate + ", " + "expirationDate=" + expirationDate + ", " + "birthday=" + birthday + ", " + "contactTel=" + contactTel + ", " + "familyMember=" + familyMember + ", " + "height=" + height + ", " + "weight=" + weight + ", " + "bloodType=" + bloodType + ", " + "psychosomaticState=" + psychosomaticState + ", " + "attention=" + attention + ", " + "filepath=" + filepath + ", " + "isDeleted=" + isDeleted + "}";
    }
}
