package com.neusoft.yyzx.entity;

/** 对应数据表：rolemenu */
public class RoleMenu {

    private Integer id;
    private Integer roleId;
    private Integer menu;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }

    public Integer getMenu() { return menu; }
    public void setMenu(Integer menu) { this.menu = menu; }

    @Override
    public String toString() {
        return "RoleMenu{" + "id=" + id + ", " + "roleId=" + roleId + ", " + "menu=" + menu + "}";
    }
}
