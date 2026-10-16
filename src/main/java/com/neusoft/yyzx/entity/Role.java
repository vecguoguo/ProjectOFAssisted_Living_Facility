package com.neusoft.yyzx.entity;

import java.time.LocalDateTime;

/** 对应数据表：role */
public class Role {

    private Integer id;
    private String name;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer updateBy;
    private Integer isDeleted;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    public Integer getUpdateBy() { return updateBy; }
    public void setUpdateBy(Integer updateBy) { this.updateBy = updateBy; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }

    @Override
    public String toString() {
        return "Role{" + "id=" + id + ", " + "name=" + name + ", " + "createTime=" + createTime + ", " + "updateTime=" + updateTime + ", " + "updateBy=" + updateBy + ", " + "isDeleted=" + isDeleted + "}";
    }
}
