package com.neusoft.yyzx.entity;

/** 对应数据表：nurselevel */
public class NurseLevel {

    private Integer id;
    private String levelName;
    private Integer levelStatus;
    private Integer isDeleted;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) { this.levelName = levelName; }

    public Integer getLevelStatus() { return levelStatus; }
    public void setLevelStatus(Integer levelStatus) { this.levelStatus = levelStatus; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }

    @Override
    public String toString() {
        return "NurseLevel{" + "id=" + id + ", " + "levelName=" + levelName + ", " + "levelStatus=" + levelStatus + ", " + "isDeleted=" + isDeleted + "}";
    }
}
